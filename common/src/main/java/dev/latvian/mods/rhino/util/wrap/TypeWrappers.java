package dev.latvian.mods.rhino.util.wrap;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;
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
    @Nullable
	private final TypeWrappers fallback;

    public TypeWrappers(@Nullable TypeWrappers fallback) {
        this.fallback = fallback;
    }

	public TypeWrappers() {
		this(null);
	}

    public void removeAll() {
		wrappers.clear();
        if (fallback != null) {
            fallback.removeAll();
        }
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
		} else if (wrappers.containsKey(target)) {
			throw new IllegalArgumentException("Wrapper for class " + target.getName() + " already exists!");
		}
        if (fallback != null && target.getName().startsWith("java.lang")) {
            // trying to register type wrapper for fundamental types
            fallback.register(target, typeWrapper);
        }
        wrappers.put(target, typeWrapper);
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

	public boolean hasWrapperNoFallback(Context cx, Object from, TypeInfo target) {
		if (target instanceof TypeWrapper<?> wrapper && wrapper.canWrap(cx, from, target)) {
			return true;
		}

		val wrapper = wrappers.get(target.asClass());
		return wrapper != null && wrapper.canWrap(cx, from, target);
	}

    public boolean hasWrapper(Context cx, Object from, TypeInfo target) {
        return hasWrapperNoFallback(cx, from, target)
            || (fallback != null && fallback.hasWrapper(cx, from, target));
    }

	public TypeWrapper<?> getWrapper(Context cx, @Nullable Object from, TypeInfo target) {
		val wrapper = getWrapperNoFallback(cx, from, target);
		return wrapper == null ? getFallbackWrapper(cx, from, target) : wrapper;
	}

	@Nullable
	public TypeWrapper<?> getWrapperNoFallback(Context cx, @Nullable Object from, TypeInfo target) {
		if (!target.shouldConvert()) {
			return null;
		}
        val wrapper = wrappers.get(target.asClass());
        return wrapper != null && wrapper.canWrap(cx, from, target) ? wrapper : null;
    }

	@Nullable
	public TypeWrapper<?> getFallbackWrapper(Context cx, @Nullable Object from, TypeInfo target) {
		return fallback != null ? fallback.getWrapper(cx, from, target) : null;
    }

	/**
	 * only kept for backward compat
	 * @see #getWrapper(Context, Object, TypeInfo)
	 */
	@Nullable
	@Deprecated
	public TypeWrapperFactory<?> getWrapperFactory(Class<?> target, @Nullable Object from) {
		val typeInfo = TypeInfo.of(target);
		val cx = Context.getContext();
		val wrapper = getWrapper(cx, from, typeInfo);
		return wrapper == null
			? null
			: o -> wrapper.wrap(cx, o, typeInfo);
	}
}
