package dev.latvian.mods.rhino.util.remapper;

import dev.latvian.mods.rhino.mod.util.MinecraftRemapper;
import dev.latvian.mods.rhino.mod.util.RemappingHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Default impl of {@link Remapper}, will apply {@link AnnotatedRemapper} and {@link MinecraftRemapper},
 * {@code MinecraftRemapper} is applied only after {@code AnnotatedRemapper} fails to remap
 */
public class DefaultRemapper implements Remapper {
	public static final DefaultRemapper INSTANCE = new DefaultRemapper();
	private final AnnotatedRemapper annotated;
	private final MinecraftRemapper minecraft;

	private DefaultRemapper() {
		this.annotated = AnnotatedRemapper.INSTANCE;
		this.minecraft = RemappingHelper.getMinecraftRemapper();
	}

	@Override
	public String getUnmappedClass(String from) {
		return this.minecraft.getUnmappedClass(from);
	}

	@Override
	public String getMappedClass(Class<?> from) {
		String remapped = this.annotated.getMappedClass(from);
		if (!remapped.isEmpty()) {
			return remapped;
		}
		remapped = this.minecraft.getMappedClass(from);
		if (!remapped.isEmpty()) {
			return remapped;
		}
		return from.getName();
	}

	@Override
	public String getMappedField(Class<?> from, Field field) {
		String remapped = this.annotated.getMappedField(from, field);
		if (!remapped.isEmpty()) {
			return remapped;
		}
		remapped = this.minecraft.getMappedField(from, field);
		if (!remapped.isEmpty()) {
			return remapped;
		}
		return field.getName();
	}

	@Override
	public String getMappedMethod(Class<?> from, Method method) {
		String remapped = this.annotated.getMappedMethod(from, method);
		if (!remapped.isEmpty()) {
			return remapped;
		}
		remapped = this.minecraft.getMappedMethod(from, method);
		if (!remapped.isEmpty()) {
			return remapped;
		}
		return method.getName();
	}
}