package dev.latvian.mods.rhino.natived;

import dev.latvian.mods.rhino.natived.reflectasm.MethodAccess;
import dev.latvian.mods.rhino.util.remapper.RemapperManager;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.val;

/**
 * @author ZZZank
 */
public class JMethodsAccess {

    private final Class<?> raw;
    private final MethodAccess access;
    /**
     * native name -> method index
     * <p>
     * we assume that methods with different indexes always have different names
     */
    private final Object2IntOpenHashMap<String> nameIndex;

    public JMethodsAccess(Class<?> clazz) {
        val remapper = RemapperManager.getDefault();

        this.raw = clazz;
        this.access = MethodAccess.get(raw);
        val methods = clazz.getMethods();
        this.nameIndex = new Object2IntOpenHashMap<>(methods.length);
        for (val method : methods) {
            val remapped = remapper.remapMethod(raw, method);
            nameIndex.put(method.getName(), access.getIndex(method.getName()));
        }
    }
}
