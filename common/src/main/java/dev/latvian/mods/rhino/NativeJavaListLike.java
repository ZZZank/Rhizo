/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;
import dev.latvian.mods.rhino.util.Deletable;
import dev.latvian.mods.rhino.util.ListLike;
import lombok.val;

import java.util.Arrays;
import java.util.List;

public class NativeJavaListLike extends NativeJavaObject {
	private final ListLike<Object> list;
	private final TypeInfo componentType;

	public NativeJavaListLike(Context cx, Scriptable scope, ListLike object, TypeInfo type) {
		super(cx, scope, object, type);
		this.list = object;
		this.componentType = type.param(0);
	}

	@Override
	public String getClassName() {
		return "JavaList";
	}

	@Override
	public boolean has(Context cx, String name, Scriptable start) {
		if (name.equals("length")) {
			return true;
		}
		return super.has(cx, name, start);
	}

	@Override
	public boolean has(Context cx, int index, Scriptable start) {
		if (isWithValidIndex(index)) {
			return true;
		}
		return super.has(cx, index, start);
	}

	@Override
	public boolean has(Context cx, Symbol key, Scriptable start) {
		if (SymbolKey.IS_CONCAT_SPREADABLE.equals(key)) {
			return true;
		}
		return super.has(cx, key, start);
	}

	@Override
	public Object get(Context cx, String name, Scriptable start) {
		if ("length".equals(name)) {
			return list.sizeLL();
		}
		return super.get(cx, name, start);
	}

	@Override
	public Object get(Context cx, int index, Scriptable start) {
		if (isWithValidIndex(index)) {
			val obj = list.getLL(index);
			return cx.getWrapFactory().wrap(cx, this, obj, componentType);
		}
		return Undefined.instance;
	}

	@Override
	public Object get(Context cx, Symbol key, Scriptable start) {
		if (SymbolKey.IS_CONCAT_SPREADABLE.equals(key)) {
			return Boolean.TRUE;
		}
		return super.get(cx, key, start);
	}

	@Override
	public void put(Context cx, int index, Scriptable start, Object value) {
		if (isWithValidIndex(index)) {
			list.setLL(index, Context.jsToJava(Context.getContext(), value, this.componentType));
			return;
		}
		super.put(cx, index, start, value);
	}

	@Override
	public Object[] getIds(Context cx) {
		val list = (List<?>) javaObject;
		val result = new Object[list.size()];
		Arrays.setAll(result, i -> i);
		return result;
	}

	private boolean isWithValidIndex(int index) {
		return index >= 0 && index < list.sizeLL();
	}

	@Override
	public void delete(Context cx, int index) {
		if (isWithValidIndex(index)) {
			Object obj = list.getLL(index);
			list.removeLL(index);
			Deletable.deleteObject(obj);
		}
	}
}
