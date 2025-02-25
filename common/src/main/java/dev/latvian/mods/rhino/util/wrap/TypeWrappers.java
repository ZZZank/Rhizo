package dev.latvian.mods.rhino.util.wrap;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class TypeWrappers {
	private final Map<Class<?>, @NotNull TypeWrapper<?>> wrappers = new Reference2ObjectOpenHashMap<>();
	/**
	 * we have known how many wrappers in KubeJS will be redirected to fallback wrapper
	 */
	private final Map<Class<?>, @NotNull TypeWrapper<?>> fallbackWrappers = new Reference2ObjectArrayMap<>();

	public void removeAll() {
		wrappers.clear();
	}

	@Deprecated
	public <F, T> void register(String id, Class<F> from, Class<T> to, Function<F, T> factory) {
		// Keep old one for now so that it doesn't crash
	}

    public <T> void register(Class<T> target, TypeWrapper<T> typeWrapper) {
		if (typeWrapper == null) {
			throw new IllegalArgumentException("type wrapper can't be null!");
		} else if (target == null || target == Object.class) {
			throw new IllegalArgumentException("target can't be Object.class!");
		} else if (target.isArray()) {
			throw new IllegalArgumentException("target can't be an array!");
		} else if (target.isPrimitive()) {
			throw new IllegalArgumentException("target can't be a primitive class!");
		} else if (wrappers.containsKey(target) || fallbackWrappers.containsKey(target)) {
			throw new IllegalArgumentException("Wrapper for class " + target.getName() + " already exists!");
		}
		if (target.getName().startsWith("java.lang")) {
			// trying to register type wrapper for fundamental types
			fallbackWrappers.put(target, typeWrapper);
		} else {
			wrappers.put(target, typeWrapper);
		}
    }

	public <T> void register(Class<T> target, TypeWrapperValidator validator, TypeWrapper.Always<T> wrapper) {
		register(target, new DynamicWrapper<>(validator, wrapper));
	}

	public <T> void register(Class<T> target, TypeWrapper.Always<T> wrapper) {
		register(target, (TypeWrapper<T>) wrapper);
	}

	/**
	 * kept for backward compat, using {@link #register(Class, TypeWrapperValidator, TypeWrapper.Always)} or
	 * {@link #register(Class, TypeWrapper)} instead of this is recommended
	 */
	public <T> void register(Class<T> target, Predicate<Object> validator, TypeWrapperFactory<T> wrapper) {
		register(target, new LegacyDynamicWrapper<>(validator, wrapper));
	}

	/**
	 * kept for backward compat, using {@link #register(Class, TypeWrapper.Always)} instead of this is recommended
	 */
	public <T> void register(Class<T> target, TypeWrapperFactory<T> wrapper) {
		register(target, (TypeWrapper<T>) wrapper);
	}

	public boolean hasWrapper(Context cx, Object from, TypeInfo target) {
		if (target instanceof TypeWrapper<?> wrapper && wrapper.canWrap(cx, from, target)) {
			return true;
		}

		val wrapper = wrappers.get(target.asClass());
		return wrapper != null && wrapper.canWrap(cx, from, target);
	}

	@Nullable
	public TypeWrapper<?> getWrapper(Context cx, @Nullable Object from, TypeInfo target) {
		if (!target.shouldConvert()) {
			return null;
		}
        val wrapper = wrappers.get(target.asClass());
        return wrapper != null && wrapper.canWrap(cx, from, target) ? wrapper : null;
    }

	@Nullable
	public TypeWrapper<?> getFallbackWrapper(Context cx, @Nullable Object from, TypeInfo target) {
		if (!target.shouldConvert()) {
			return null;
		}
		val wrapper = fallbackWrappers.get(target.asClass());
        if (wrapper == null || !wrapper.canWrap(cx, from, target)) {
            return null;
        }
        return wrapper;
    }
}
