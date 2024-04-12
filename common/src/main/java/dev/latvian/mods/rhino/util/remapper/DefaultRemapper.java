package dev.latvian.mods.rhino.util.remapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Default impl of {@link Remapper}, will only check {@link RemapForJS} annotation
 */
public class DefaultRemapper implements Remapper {
	public static final DefaultRemapper INSTANCE = new DefaultRemapper();

	private DefaultRemapper() {}

	@Override
	public String getMappedClass(Class<?> from) {
		RemapForJS remap = from.getAnnotation(RemapForJS.class);
		if (remap != null) {
			return remap.value();
		}
		return "";
	}

	@Override
	public String getMappedField(Class<?> from, Field field) {
		RemapForJS remap = field.getAnnotation(RemapForJS.class);
		if (remap != null) {
			return remap.value();
		}
		return "";
	}

	@Override
	public String getMappedMethod(Class<?> from, Method method) {
		RemapForJS remap = method.getAnnotation(RemapForJS.class);
		if (remap != null) {
			return remap.value();
		}
		return "";
	}
}