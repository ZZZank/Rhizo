package dev.latvian.mods.rhino.util.wrap;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;

/**
 * named as "TypeWrapperFactory" instead of "SimpleTypeWrapper" just for the sake of backward compat
 *
 * @author LatvianModder
 */
@FunctionalInterface
public interface TypeWrapperFactory<T> extends TypeWrapper.Always<T> {

	T wrap(Object o);

	default T wrap(Context cx, Object o, TypeInfo target) {
		return wrap(o);
	}
}
