package dev.latvian.mods.rhino.native_java.type.info;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

/**
 * @author ZZZank
 */
public class VariableTypeInfo extends TypeInfoBase {
    static final Map<TypeVariable<?>, VariableTypeInfo> CACHE = new IdentityHashMap<>();

    private final TypeVariable<?> raw;
    private TypeInfo[] bounds = null;

    VariableTypeInfo(TypeVariable<?> raw) {
        this.raw = raw;
    }

    /**
     * NOTE: {@link Object} class in original bound will be skipped
     */
    public TypeInfo[] getBounds() {
        if (bounds == null) {
            val rawBounds = raw.getBounds();
            if (rawBounds.length == 1 && rawBounds[0] == Object.class) {
                // shortcut for most variable types with no bounds
                bounds = TypeInfo.EMPTY_ARRAY;
            } else {
                val filtered = new ArrayList<Type>(rawBounds.length);
                for (val t : rawBounds) {
                    if (t != Object.class) {
                        filtered.add(t);
                    }
                }
                bounds = TypeInfo.ofArray(filtered.toArray(new Type[0]));
            }
        }
        return bounds;
    }

    public String getName() {
        return raw.getName();
    }

    @Override
    public Class<?> asClass() {
        return Object.class;
    }

    @Override
    public String toString() {
        return raw.getName();
    }

    @Override
    public @NotNull TypeInfo consolidate(@NotNull Map<VariableTypeInfo, TypeInfo> mapping) {
        return mapping.getOrDefault(this, this);
    }
}
