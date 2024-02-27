package dev.latvian.mods.rhino.util;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaList;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.SharedContextData;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;

public interface ListLike<T> extends CustomJavaToJsWrapper {
	@Override
	default Scriptable convertJavaToJs(SharedContextData cx, Scriptable scope, Class<?> staticType) {
		return new NativeJavaList(cx, scope, this, new ArrayList<>());
	}

	/**
	 * @deprecated
	 * @return null
	 */
	default Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Class<?> staticType) {
		return null;
	}

	@Nullable
	T getLL(int index);

	default void setLL(int index, T value) {
		throw new UnsupportedOperationException("Can't insert values in this list!");
	}

	int sizeLL();

	default void removeLL(int index) {
		throw new UnsupportedOperationException("Can't delete values from this list!");
	}

	default void clearLL() {
		throw new UnsupportedOperationException("Can't clear this list!");
	}
}
