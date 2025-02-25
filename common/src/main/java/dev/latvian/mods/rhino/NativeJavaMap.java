/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;
import dev.latvian.mods.rhino.util.Deletable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NativeJavaMap extends NativeJavaObject {

    public final TypeInfo mapKeyType;
	public final TypeInfo mapValueType;

	public NativeJavaMap(Context cx, Scriptable scope, Map map, TypeInfo type) {
		super(cx, scope, map, type);
        this.mapKeyType = type.param(0);
		this.mapValueType = type.param(1);
	}

	@Override
	public String getClassName() {
		return "JavaMap";
	}

	@Override
	public boolean has(Context cx, String name, Scriptable start) {
		if (map().containsKey(name)) {
			return true;
		}
		return super.has(cx, name, start);
	}

	@Override
	public boolean has(Context cx, int index, Scriptable start) {
		if (map().containsKey(index)) {
			return true;
		}
		return super.has(cx, index, start);
	}

	@Override
	public Object get(Context cx, String name, Scriptable start) {
		if (map().containsKey(name)) {
			Object obj = map().get(name);
			return cx.getWrapFactory().wrap(cx, this, obj, this.mapValueType);
		}
		return super.get(cx, name, start);
	}

	@Override
	public Object get(Context cx, int index, Scriptable start) {
		if (map().containsKey(index)) {
			Object obj = map().get(index);
			return cx.getWrapFactory().wrap(cx, this, obj, this.mapValueType);
		}
		return super.get(cx, index, start);
	}

	@Override
	public void put(Context cx, String name, Scriptable start, Object value) {
		map().put(name, Context.jsToJava(Context.getCurrentContext(), value, this.mapValueType));
	}

	@Override
	public void put(Context cx, int index, Scriptable start, Object value) {
		map().put(index, Context.jsToJava(Context.getCurrentContext(), value, this.mapValueType));
	}

	@Override
	public Object[] getIds(Context cx) {
		List<Object> ids = new ArrayList<>(map().size());
		for (Object key : map().keySet()) {
			if (key instanceof Integer) {
				ids.add(key);
			} else {
				ids.add(ScriptRuntime.toString(key));
			}
		}
		return ids.toArray();
	}

	@Override
	public void delete(Context cx, String name) {
		Deletable.deleteObject(map().remove(name));
	}

	@Override
	public void delete(Context cx, int index) {
		Deletable.deleteObject(map().remove(index));
	}

	public Map<Object, Object> map() {
		return (Map<Object, Object>) javaObject;
	}
}
