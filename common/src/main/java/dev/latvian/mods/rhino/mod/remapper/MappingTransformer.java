package dev.latvian.mods.rhino.mod.remapper;

import net.neoforged.srgutils.IMappingFile;
import org.apache.commons.lang3.mutable.MutableObject;

/**
 * @author ZZZank
 */
public interface MappingTransformer {

    MutableObject<MappingTransformer> IMPL = new MutableObject<>();

    static MappingTransformer get() {
        return IMPL.getValue();
    }

    /**
     * @param vanillaMapping mapped name -> obf name
     * @return mapped name -> in-game name
     */
    IMappingFile transform(IMappingFile vanillaMapping);

    default boolean filterClass(IMappingFile.IClass clazz) {
        return !RhizoMappingGen.isAnonymousClass(clazz.getMapped());
    }

    boolean filterMethod(IMappingFile.IMethod method);

    /**
     * @param name original name
     */
    String trimMethod(String name);

    /**
     * @param trimmed trimmed original name
     */
    String restoreMethod(String trimmed);

    boolean filterField(IMappingFile.IField field);

    /**
     * @param name original name
     */
    String trimField(String name);

    /**
     * @param trimmed trimmed original name
     */
    String restoreField(String trimmed);
}
