/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;
import dev.latvian.mods.rhino.util.Deletable;
import dev.latvian.mods.rhino.util.MapLike;
import lombok.val;

public class NativeJavaMapLike extends NativeJavaObject {
	private final MapLike<Object, Object> map;
    private final TypeInfo valueType;

	public NativeJavaMapLike(Context cx, Scriptable scope, MapLike mapLike, TypeInfo type) {
		super(cx, scope, mapLike, type);
		this.map = mapLike;
        this.valueType = type.param(1);
	}

	@Override
	public String getClassName() {
		return "JavaMapLike";
	}

	@Override
	public boolean has(Context cx, String name, Scriptable start) {
		if (map.containsKeyML(name)) {
			return true;
		}
		return super.has(cx, name, start);
	}

	@Override
	public boolean has(Context cx, int index, Scriptable start) {
		if (map.containsKeyML(index)) {
			return true;
		}
		return super.has(cx, index, start);
	}

	@Override
	public Object get(Context cx, String name, Scriptable start) {
		if (map.containsKeyML(name)) {
			val obj = map.getML(name);
			return cx.getWrapFactory().wrap(cx, this, obj, valueType);
		}
		return super.get(cx, name, start);
	}

	@Override
	public Object get(Context cx, int index, Scriptable start) {
		if (map.containsKeyML(index)) {
			Object obj = map.getML(index);
			return cx.getWrapFactory().wrap(cx, this, obj, valueType);
		}
		return super.get(cx, index, start);
	}

	@Override
	public void put(Context cx, String name, Scriptable start, Object value) {
		map.putML(name, Context.jsToJava(Context.getContext(), value, this.valueType));
	}

	@Override
	public void put(Context cx, int index, Scriptable start, Object value) {
		map.putML(index, Context.jsToJava(Context.getContext(), value, this.valueType));
	}

	@Override
	public Object[] getIds(Context cx) {
		Object[] k = map.keysML().toArray();

		for (int i = 0; i < k.length; i++) {
			if (!(k[i] instanceof Integer)) {
				k[i] = ScriptRuntime.toString(k[i]);
			}
		}

		return k;
	}

	@Override
	public void delete(Context cx, String name) {
		val obj = map.getML(name);
		map.removeML(name);
		Deletable.deleteObject(obj);
	}

	@Override
	public void delete(Context cx, int index) {
		val obj = map.getML(index);
		map.removeML(index);
		Deletable.deleteObject(obj);
	}
}
