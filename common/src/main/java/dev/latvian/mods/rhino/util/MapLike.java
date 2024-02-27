package dev.latvian.mods.rhino.util;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaMap;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.SharedContextData;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public interface MapLike<K, T> extends CustomJavaToJsWrapper {
	@Override
	default Scriptable convertJavaToJs(SharedContextData cx, Scriptable scope, Class<?> staticType) {
		return new NativeJavaMap(cx, scope, this, new HashMap<>());
	}

	/**
	 * @deprecated
	 * @return null
	 */
	default Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Class<?> staticType) {
		return null;
	}

	@Nullable
	T getML(K key);

	default boolean containsKeyML(K key) {
		return getML(key) != null;
	}

	default void putML(K key, T value) {
		throw new UnsupportedOperationException("Can't insert values in this map!");
	}

	default Collection<K> keysML() {
		return Collections.emptySet();
	}

	default void removeML(K key) {
		throw new UnsupportedOperationException("Can't delete values from this map!");
	}

	default void clearML() {
		throw new UnsupportedOperationException("Can't clear this map!");
	}
}
