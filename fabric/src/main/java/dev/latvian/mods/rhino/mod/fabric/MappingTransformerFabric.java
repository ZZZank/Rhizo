package dev.latvian.mods.rhino.mod.fabric;

import dev.latvian.mods.rhino.mod.remapper.MappingTransformer;
import lombok.val;
import net.neoforged.srgutils.IMappingFile;

import java.util.regex.Pattern;

/**
 * @author ZZZank
 */
public class MappingTransformerFabric implements MappingTransformer {
    private static final Pattern FIELD_PATTERN = Pattern.compile("^field_[0-9]+$");
    private static final Pattern METHOD_PATTERN = Pattern.compile("^method_[0-9]+$");

    @Override
    public IMappingFile transform(IMappingFile vanillaMapping) {
        return vanillaMapping.rename(new RhinoModFabric.ClazzBasedRenamer(RhinoModFabric.loadNativeMappingClassMap()));
    }

    @Override
    public boolean filterMethod(IMappingFile.IMethod method) {
        val original = method.getOriginal();
        val mapped = method.getMapped();
        return !original.equals(mapped)
            && METHOD_PATTERN.matcher(original).matches()
            && !mapped.startsWith("lambda$")
            && !mapped.startsWith("<");
    }

    @Override
    public String trimMethod(String name) {
        return name.substring("method_".length());
    }

    @Override
    public String restoreMethod(String trimmed) {
        return "method_" + trimmed;
    }

    @Override
    public boolean filterField(IMappingFile.IField field) {
        val original = field.getOriginal();
        val mapped = field.getMapped();
        return !original.equals(mapped)
            && FIELD_PATTERN.matcher(original).matches();
    }

    @Override
    public String trimField(String name) {
        return name.substring("field_".length());
    }

    @Override
    public String restoreField(String trimmed) {
        return "field_" + trimmed;
    }
}
