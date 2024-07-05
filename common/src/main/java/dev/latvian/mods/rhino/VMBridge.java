/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

// API class

package dev.latvian.mods.rhino;

import lombok.val;

import java.lang.reflect.*;

public abstract class VMBridge {

	static final VMBridge vm = makeVMInstance();

	private static VMBridge makeVMInstance() {
		/*
		String[] classNames = {"dev.latvian.mods.rhino.VMBridge_custom", "dev.latvian.mods.rhino.VMBridge.VMBridge_jdk18",};
		for (int i = 0; i != classNames.length; ++i) {
			String className = classNames[i];
			Class<?> cl = Kit.classOrNull(className);
			if (cl != null) {
				VMBridge bridge = (VMBridge) Kit.newInstanceOrNull(cl);
				if (bridge != null) {
					return bridge;
				}
			}
		}
		throw new IllegalStateException("Failed to create VMBridge instance");
		 */

		return new VMBridge_jdk18();
	}

	/**
	 * Return a helper object to optimize {@link Context} access.
	 * <p>
	 * The runtime will pass the resulting helper object to the subsequent
	 * calls to {@link #getContext(Object contextHelper)} and
	 * {@link #setContext(Object contextHelper, Context cx)} methods.
	 * In this way the implementation can use the helper to cache
	 * information about current thread to make {@link Context} access faster.
	 */
	protected abstract Object getThreadContextHelper();

	/**
	 * Get {@link Context} instance associated with the current thread
	 * or null if none.
	 *
	 * @param contextHelper The result of {@link #getThreadContextHelper()}
	 *                      called from the current thread.
	 */
	protected abstract Context getContext(Object contextHelper);

	/**
	 * Associate {@link Context} instance with the current thread or remove
	 * the current association if <code>cx</code> is null.
	 *
	 * @param contextHelper The result of {@link #getThreadContextHelper()}
	 *                      called from the current thread.
	 */
	protected abstract void setContext(Object contextHelper, Context cx);

	/**
	 * In many JVMSs, public methods in private
	 * classes are not accessible by default (Sun Bug #4071593).
	 * VMBridge instance should try to workaround that via, for example,
	 * calling method.setAccessible(true) when it is available.
	 * The implementation is responsible to catch all possible exceptions
	 * like SecurityException if the workaround is not available.
	 *
	 * @return true if it was possible to make method accessible
	 * or false otherwise.
	 */
	protected abstract boolean tryToMakeAccessible(AccessibleObject accessible);

	/**
	 * Create helper object to create later proxies implementing the specified
	 * interfaces later. Under JDK 1.3 the implementation can look like:
	 * <pre>
	 * return java.lang.reflect.Proxy.getProxyClass(..., interfaces).
	 *     getConstructor(new Class[] {
	 *         java.lang.reflect.InvocationHandler.class });
	 * </pre>
	 *
	 * @param interfaces Array with one or more interface class objects.
	 */
	protected abstract Object getInterfaceProxyHelper(ContextFactory cf, Class<?>[] interfaces);

	/**
	 * Create proxy object for {@link InterfaceAdapter}. The proxy should call
	 * {@link InterfaceAdapter#invoke(ContextFactory, Object, Scriptable,
	 * Object, Method, Object[])}
	 * as implementation of interface methods associated with
	 * <code>proxyHelper</code>. {@link Method}
	 *
	 * @param proxyHelper The result of the previous call to
	 *                    {@link #getInterfaceProxyHelper(ContextFactory, Class[])}.
	 */
	protected abstract Object newInterfaceProxy(Object proxyHelper, ContextFactory cf, InterfaceAdapter adapter, Object target, Scriptable topScope);

	/**
	 * VMBridge implemented targeting Java 8
	 */
	public static class VMBridge_jdk18 extends VMBridge {
		private static final ThreadLocal<Object[]> contextLocal = new ThreadLocal<>();

		@Override
		protected Object getThreadContextHelper() {
			// To make subsequent batch calls to getContext/setContext faster
			// associate permanently one element array with contextLocal
			// so getContext/setContext would need just to read/write the first
			// array element.
			// Note that it is necessary to use Object[], not Context[] to allow
			// garbage collection of Rhino classes. For details see comments
			// by Attila Szegedi in
			// https://bugzilla.mozilla.org/show_bug.cgi?id=281067#c5

			Object[] storage = contextLocal.get();
			if (storage == null) {
				storage = new Object[1];
				contextLocal.set(storage);
			}
			return storage;
		}

		@Override
		protected Context getContext(Object contextHelper) {
			Object[] storage = (Object[]) contextHelper;
			return (Context) storage[0];
		}

		@Override
		protected void setContext(Object contextHelper, Context cx) {
			Object[] storage = (Object[]) contextHelper;
			storage[0] = cx;
		}

		@Override
		protected boolean tryToMakeAccessible(AccessibleObject accessible) {
			if (accessible.isAccessible()) {
				return true;
			}

			try {
				accessible.setAccessible(true);
			} catch (Exception ignored) {
			}

			return accessible.isAccessible();
		}

		@Override
		protected Object getInterfaceProxyHelper(ContextFactory cf, Class<?>[] interfaces) {
			// XXX: How to handle interfaces array withclasses from different
			// class loaders? Using cf.getApplicationClassLoader() ?
			val loader = interfaces[0].getClassLoader();
			val cl = Proxy.getProxyClass(loader, interfaces);
			Constructor<?> c;
			try {
				c = cl.getConstructor(InvocationHandler.class);
			} catch (NoSuchMethodException ex) {
				// Should not happen
				throw new IllegalStateException(ex);
			}
			return c;
		}

		@Override
		protected Object newInterfaceProxy(
			Object proxyHelper,
			final ContextFactory cf,
			final InterfaceAdapter adapter,
			final Object target,
			final Scriptable topScope
		) {
			val c = (Constructor<?>) proxyHelper;

			InvocationHandler handler = (proxy, method, args) -> {
				// In addition to methods declared in the interface, proxies
				// also route some java.lang.Object methods through the
				// invocation handler.
				if (method.getDeclaringClass() == Object.class) {
					String methodName = method.getName();
                    switch (methodName) {
                        case "equals":
							// Note: we could compare a proxy and its wrapped function
                            // as equal here but that would break symmetry of equal().
                            // The reason == suffices here is that proxies are cached
                            // in ScriptableObject (see NativeJavaObject.coerceType())
                            return proxy == args[0];
                        case "hashCode":
                            return target.hashCode();
                        case "toString":
                            return "Proxy[" + target.toString() + "]";
                    }
				}
				return adapter.invoke(cf, target, topScope, proxy, method, args);
			};
			Object proxy;
			try {
				proxy = c.newInstance(handler);
			} catch (InvocationTargetException ex) {
				throw Context.throwAsScriptRuntimeEx(ex);
			} catch (IllegalAccessException | InstantiationException ex) {
				// Should not happen
				throw new IllegalStateException(ex);
			}
			return proxy;
		}
	}
}
