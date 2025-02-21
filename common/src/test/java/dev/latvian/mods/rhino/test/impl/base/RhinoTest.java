package dev.latvian.mods.rhino.test.impl.base;

import dev.latvian.mods.rhino.*;
import dev.latvian.mods.rhino.test.impl.*;
import dev.latvian.mods.rhino.test.impl.event.EventBus;
import dev.latvian.mods.rhino.util.wrap.TypeWrapper;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import lombok.val;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class RhinoTest {
	public final String testName;
	public final ContextFactory factory;
	public TestConsole console;
	public final Map<String, Object> shared = new HashMap<>();
	public final Map<String, Function<RhinoTest, Object>> bindings = new HashMap<>();

	public RhinoTest(String n) {
		this.testName = n;
		this.factory = new TestContextFactory();
		this.console = new TestConsole(factory);

		var typeWrappers = factory.getTypeWrappers();
		typeWrappers.register(TestMaterial.class, TestMaterial::get);
		typeWrappers.register(WithContext.class, WithContext::of);
		typeWrappers.register(Holder.class, Holder::of);
	}

	public RhinoTest withBinding(String name, Function<RhinoTest, Object> bindingGenerator) {
		bindings.put(name, bindingGenerator);
		return this;
	}

	public <T> RhinoTest withTypeWrapper(Class<T> target, TypeWrapper<T> wrapper) {
		this.factory.getTypeWrappers().register(target, wrapper);
		return this;
	}

	public <T> RhinoTest withTypeWrapper(Consumer<TypeWrappers> handler) {
		handler.accept(this.factory.getTypeWrappers());
		return this;
	}

	public static void addToScope(Context cx, Scriptable scope, String name, Object value) {
		if (value instanceof Class<?> c) {
			ScriptableObject.putProperty(scope, name, new NativeJavaClass(cx, scope, c));
		} else {
			ScriptableObject.putProperty(scope, name, Context.javaToJS(cx, value, scope));
		}
	}

	public void test(String name, String script, String match) {
		test(name, script, match, false);
		test(name, script, match, true);
	}

	public void test(String name, String script, String match, boolean compile) {
		try {
			val cx = (TestContext) factory.enterContext();
			val rootScope = cx.initStandardObjects();

			addToScope(cx, rootScope, "console", console);
			addToScope(cx, rootScope, "shared", shared);
			addToScope(cx, rootScope, "EventBus", new EventBus(console));
			for (val entry : bindings.entrySet()) {
				addToScope(cx, rootScope, entry.getKey(), entry.getValue().apply(this));
			}

			cx.testName = name;
			if (compile) {
				cx.setOptimizationLevel(9);
			}
			cx.evaluateString(rootScope, script, testName + "/" + name, 1, null);
		} catch (Exception ex) {
			ex.printStackTrace();
			console.info(TestConsole.formatException(ex.getMessage()));
		} finally {
			Context.exit();
		}

		Assertions.assertEquals(match.trim(), console.getConsoleOutput().trim());
	}
}
