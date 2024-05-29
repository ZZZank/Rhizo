package dev.latvian.mods.rhino.mod.remapper.info;

import com.github.bsideup.jabel.Desugar;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZZZank
 */
@Desugar
public record Clazz(String original, String remapped, Multimap<String, MethodInfo> nArgMethods,
             Map<String, MethodInfo> noArgMethods, Map<String, FieldInfo> fields) {

    public Clazz(String original, String remapped) {
        this(original, remapped, ArrayListMultimap.create(), new HashMap<>(), new HashMap<>());
    }

    public void acceptMethod(String original, String paramDesc, String remapped) {
        if (paramDesc.equals("(")) {
            this.noArgMethods.put(original, new MethodInfo(original, null, remapped));
        } else {
            this.nArgMethods.put(original, new MethodInfo(original, paramDesc, remapped));
        }
    }

    public void acceptField(String original, String remapped) {
        this.fields.put(original, new FieldInfo(original, remapped));
    }
}
