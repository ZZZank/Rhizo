package dev.latvian.mods.rhino.mod.fabric;

import com.github.bsideup.jabel.Desugar;
import dev.latvian.mods.rhino.mod.RhinoProperties;
import dev.latvian.mods.rhino.mod.remapper.RhizoMappingGen;
import dev.latvian.mods.rhino.mod.remapper.info.Clazz;
import lombok.val;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.neoforged.srgutils.IMappingFile;
import net.neoforged.srgutils.IRenamer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RhinoModFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        if (RhinoProperties.INSTANCE.generateMapping) {
            RhizoMappingGen.generate(
                "1.16.5",
                new RenameOnlyMappingLoader(loadNativeMappingClassMap())
            );
        }
    }

    private static Map<String, Clazz> loadNativeMappingClassMap() {
        val runtimeNamespace = FabricLauncherBase.getLauncher().getTargetNamespace();
        val rawNamespace = "official";
        val tree = FabricLauncherBase.getLauncher().getMappingConfiguration().getMappings();

        val classMap = new HashMap<String, Clazz>();
        for (val c : tree.getClasses()) {
            //clazz
            //similar to SRG name in Forge
            val unmappedC = c.getName(runtimeNamespace).replace('/', '.');
            //obf name
            val rawC = c.getName(rawNamespace).replace('/', '.');
            val clazz = new Clazz(rawC, unmappedC);
            classMap.put(rawC, clazz);
            //method
            for (val method : c.getMethods()) {
                val unmappedM = method.getName(runtimeNamespace);
                val rawM = method.getName(rawNamespace);
                val desc = method.getDesc(tree.getNamespaceId(rawNamespace));
                clazz.acceptMethod(rawM, desc.substring(0, desc.lastIndexOf(')')), unmappedM);
            }
            //field
            for (val field : c.getFields()) {
                val unmappedF = field.getName(runtimeNamespace);
                val rawF = field.getName(rawNamespace);
                clazz.acceptField(rawF, unmappedF);
            }
        }
        return classMap;
    }

    @Desugar
    private record RenameOnlyMappingLoader(Map<String, Clazz> classMap)
        implements RhizoMappingGen.NativeMappingLoader, IRenamer {
        /**
         * returning null because there are too many features to cover, if we want to return an actual mapping file,
         * actual logic will be handled by the returned IRenamer from {@link RenameOnlyMappingLoader#toRenamer(IMappingFile)}
         */
        @Override
        public @Nullable IMappingFile load(String mcVersion, IMappingFile vanillaMapping) throws IOException {
            return null;
        }

        @Override
        public IRenamer toRenamer(IMappingFile link) {
            return this;
        }

        public String rename(IMappingFile.IClass c) {
            val clazz = classMap.get(c.getMapped());
            if (clazz == null) {
                return c.getMapped();
            }
            return clazz.remapped();
        }

        public String rename(IMappingFile.IField f) {
            val clazz = classMap.get(f.getParent().getMapped());
            if (clazz == null) {
                return f.getMapped();
            }
            val fInfo = clazz.fields().get(f.getMapped());
            if (fInfo == null) {
                return f.getMapped();
            }
            return fInfo.remapped();
        }

        public String rename(IMappingFile.IMethod m) {
            val clazz = classMap.get(m.getParent().getMapped());
            if (clazz == null) {
                return m.getMapped();
            }
            val methods = clazz.nArgMethods().get(m.getMapped());
            if (methods.isEmpty()) {
                return m.getMapped();
            }
            for (val method : methods) {
                if (m.getMappedDescriptor().startsWith(method.paramDescriptor())) {
                    return method.remapped();
                }
            }
            return m.getMapped();
        }
    }
}
