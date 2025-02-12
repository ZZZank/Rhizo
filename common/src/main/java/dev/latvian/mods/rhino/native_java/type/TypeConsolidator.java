package dev.latvian.mods.rhino.native_java.type;

import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;
import dev.latvian.mods.rhino.native_java.type.info.VariableTypeInfo;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author ZZZank
 */
public final class TypeConsolidator {
    private static final Map<Class<?>, Map<VariableTypeInfo, TypeInfo>> MAPPINGS = new Reference2ObjectOpenHashMap<>();

    private static final boolean DEBUG = false;

    private TypeConsolidator() {
    }

    @NotNull
    public static Map<VariableTypeInfo, TypeInfo> getMapping(Class<?> type) {
        if (DEBUG) {
            System.out.println("getting mapping from: " + type);
        }
        val got = getImpl(type);
        return got == null ? Collections.emptyMap() : got;
    }

    @NotNull
    public static TypeInfo consolidateOrNone(VariableTypeInfo variable, Map<VariableTypeInfo, TypeInfo> mapping) {
        return mapping.getOrDefault(variable, TypeInfo.NONE);
    }

    @NotNull
    public static TypeInfo[] consolidateAll(
        @NotNull TypeInfo @NotNull [] original,
        @NotNull Map<VariableTypeInfo, TypeInfo> mapping
    ) {
        val len = original.length;
        if (DEBUG) {
            System.out.println("consolidating" + Arrays.toString(original));
        }
        if (len == 0) {
            return original;
        } else if (len == 1) {
            val consolidated = original[0].consolidate(mapping);
            return consolidated != original[0] ? new TypeInfo[]{consolidated} : original;
        }
        TypeInfo[] consolidatedAll = null;
        for (int i = 0; i < len; i++) {
            val type = original[i];
            val consolidated = type.consolidate(mapping);
            if (consolidated != type) {
                if (consolidatedAll == null) {
                    consolidatedAll = new TypeInfo[len];
                    System.arraycopy(original, 0, consolidatedAll, 0, i);
                }
                consolidatedAll[i] = consolidated;
            } else if (consolidatedAll != null) {
                consolidatedAll[i] = consolidated;
            }
        }
        return consolidatedAll == null ? original : consolidatedAll;
    }

    @Nullable
    private static Map<VariableTypeInfo, TypeInfo> getImpl(Class<?> type) {
        if (type == null || type.isPrimitive() || type == Object.class) {
            return null;
        }
        synchronized (MAPPINGS) {
            return MAPPINGS.computeIfAbsent(type, TypeConsolidator::collect);
        }
    }

    @NotNull
    private static Map<VariableTypeInfo, TypeInfo> collect(Class<?> type) {
        val mapping = new IdentityHashMap<VariableTypeInfo, TypeInfo>();

        /**
         * let's consider the most extreme case:
         * classes are named as 'XXX': A, B, C, ...
         * type variables are named as 'Tx': Ta, Tb, Tc, ...
         * <p>
         * there are 3 classes:
         * class A<Ta> {}
         * interface B<Tb> {}
         * class C<Tc> extends A<Tc> {}
         * class D<Td> extends C<Td> implements B<A<Td>> {}
         *
         * assuming that input 'type' is C.class
         */

        //collect current level mapping
        //current level types will only be consolidated by mappings from its subclasses
        val parent = type.getSuperclass();

        //in our D.class example, this will collect mapping from C<Td>, forming Tc -> Td
        extractSuperMapping(type.getGenericSuperclass(), mapping);

        //in our D.class example, this will collect mapping from B<A<Td>>, forming Tb -> A<Td>
        for (val genericInterface : type.getGenericInterfaces()) {
            extractSuperMapping(genericInterface, mapping);
        }

        //mapping from super
        //in our D.class example, super mapping will only include Ta -> Tc
        val superMapping = getImpl(parent);

        if (superMapping == null || superMapping.isEmpty()) {
            return postMapping(mapping);
        }

        //'flatten' super mapping
        val merged = new IdentityHashMap<>(superMapping);
        for (val entry : merged.entrySet()) {
            //in our D.class example, super mapping Ta -> Tc will be 'flattened' to Ta -> Td
            entry.setValue(entry.getValue().consolidate(mapping));
        }
        //merge two mapping
        merged.putAll(mapping);

        //in our D.class example, our mapping will include Ta -> Td, Tb -> A<Td>, Tc -> Td.
        //the 'flattened' means that all related type (Ta, Tb, Tc) can be directly mapped to
        //the type used by D.class (Td), so we only need to apply the mapping ONCE
        return postMapping(merged);
    }

    private static void extractSuperMapping(
        Type superType,
        IdentityHashMap<VariableTypeInfo, TypeInfo> pushTo
    ) {
        if (superType instanceof ParameterizedType parameterized
            && parameterized.getRawType() instanceof Class<?> parent
        ) {
            final var params = parent.getTypeParameters(); // T
            val args = parameterized.getActualTypeArguments(); // T is mapped to
            for (int i = 0; i < args.length; i++) {
                pushTo.put(TypeInfo.of(params[i]), TypeInfo.of(args[i]));
            }
        }
    }

    private static Map<VariableTypeInfo, TypeInfo> postMapping(Map<VariableTypeInfo, TypeInfo> mapping) {
        switch (mapping.size()) {
            case 0:
                if (DEBUG) {
                    System.out.println("collected empty mapping");
                }
                return Collections.emptyMap();
            case 1:
                val entry = mapping.entrySet().iterator().next();
                if (DEBUG) {
                    System.out.println("collected singleton mapping: " + entry.getKey() + " -> " + entry.getValue());
                }
                return Collections.singletonMap(entry.getKey(), entry.getValue());
            default:
                if (DEBUG) {
                    System.out.println("collected mapping with size: " + mapping.size());
                }
                return Collections.unmodifiableMap(mapping);
        }
    }
}
