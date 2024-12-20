package dev.latvian.mods.rhino.native_java.type.info;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.TypeVariable;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author ZZZank
 */
public class VariableTypeInfo extends TypeInfoBase {
    static final Map<TypeVariable<?>, VariableTypeInfo> CACHE = new IdentityHashMap<>();

    private Object bound;

    VariableTypeInfo(TypeVariable<?> rawType) {
        this.bound = rawType;
    }

    public TypeInfo getBound() {
        if (bound instanceof TypeVariable<?> t) {
            // a variable type can have multiple bounds, but we only resolves the first one, since type wrapper cannot
            // magically find or create a class that meets multiple bounds
            val bound = t.getBounds()[0];
            if (bound == Object.class) {
                this.bound = TypeInfo.NONE;
            } else {
                this.bound = TypeInfo.of(bound);
            }
        }
        return (TypeInfo) bound;
    }

    @Override
    public Class<?> asClass() {
        return getBound().asClass();
    }

    @Override
    public @NotNull TypeInfo consolidate(@NotNull Map<VariableTypeInfo, TypeInfo> mapping) {
        val got = mapping.get(this);
        return got == null ? this : got;
    }
}
