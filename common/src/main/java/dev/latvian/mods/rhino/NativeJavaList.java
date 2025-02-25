/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;
import dev.latvian.mods.rhino.util.Deletable;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class NativeJavaList extends NativeJavaObject implements Iterable<Object> {

    public final TypeInfo listType;

	public NativeJavaList(Context cx, Scriptable scope, List list, TypeInfo type) {
		super(cx, scope, list, type);
        this.listType = type.param(0);
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
			return list().size();
		}
		return super.get(cx, name, start);
	}

	@Override
	public Object get(Context cx, int index, Scriptable start) {
		if (isWithValidIndex(index)) {
			Object obj = list().get(index);
			return cx.getWrapFactory().wrap(cx, this, obj, this.listType);
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
			list().set(index, Context.jsToJava(Context.getContext(), value, TypeInfo.OBJECT));
			return;
		}
		super.put(cx, index, start, value);
	}

	@Override
	public Object[] getIds(Context cx) {
		val result = new Object[list().size()];
		int i = list().size();
		while (--i >= 0) {
			result[i] = i;
		}
		return result;
	}

	private boolean isWithValidIndex(int index) {
		return index >= 0 && index < list().size();
	}

	@Override
	public void delete(Context cx, int index) {
		if (isWithValidIndex(index)) {
			Object obj = list().remove(index);
			Deletable.deleteObject(obj);
		}
	}

	@NotNull
	@Override
	public Iterator<Object> iterator() {
		return this.list().iterator();
	}

	public List<Object> list() {
		return (List<Object>) javaObject;
	}
}
