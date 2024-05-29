package dev.latvian.mods.rhino.mod.fabric;

import com.github.bsideup.jabel.Desugar;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.mod.RhinoProperties;
import dev.latvian.mods.rhino.mod.remapper.RhizoMappingGen;
import dev.latvian.mods.rhino.mod.remapper.RhizoRemapper;
import dev.latvian.mods.rhino.mod.remapper.info.Clazz;
import dev.latvian.mods.rhino.util.remapper.AnnotatedRemapper;
import dev.latvian.mods.rhino.util.remapper.SequencedRemapper;
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
        Context.setRemapper(new SequencedRemapper(AnnotatedRemapper.INSTANCE, RhizoRemapper.instance()));
        if (RhinoProperties.INSTANCE.generateMapping) {
            RhizoMappingGen.generate(
                "1.16.5",
                new RenameOnlyMappingLoader(loadNativeMappingClassMap())
            );
        }
    }

    private static Map<String, Clazz> loadNativeMappingClassMap() {
        final var runtimeNamespace = FabricLauncherBase.getLauncher().getTargetNamespace();
        final var rawNamespace = "official";
        final var tree = FabricLauncherBase.getLauncher().getMappingConfiguration().getMappings();

        final Map<String, Clazz> classMap = new HashMap<>();
        for (var c : tree.getClasses()) {
            //clazz
            //similar to SRG name in Forge
            var unmappedC = c.getName(runtimeNamespace).replace('/', '.');
            //obf name
            var rawC = c.getName(rawNamespace).replace('/', '.');
            var clazz = new Clazz(rawC, unmappedC);
            classMap.put(rawC, clazz);
            //method
            for (var method : c.getMethods()) {
                var unmappedM = method.getName(runtimeNamespace);
                var rawM = method.getName(rawNamespace);
                var desc = method.getDesc(tree.getNamespaceId(rawNamespace));
                clazz.acceptMethod(rawM, desc.substring(0, desc.lastIndexOf(')')), unmappedM);
            }
            //field
            for (var field : c.getFields()) {
                var unmappedF = field.getName(runtimeNamespace);
                var rawF = field.getName(rawNamespace);
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
            var clazz = classMap.get(c.getMapped());
            if (clazz == null) {
                return c.getMapped();
            }
            return clazz.remapped();
        }

        public String rename(IMappingFile.IField f) {
            var clazz = classMap.get(f.getParent().getMapped());
            if (clazz == null) {
                return f.getMapped();
            }
            var fInfo = clazz.fields().get(f.getMapped());
            if (fInfo == null) {
                return f.getMapped();
            }
            return fInfo.remapped();
        }

        public String rename(IMappingFile.IMethod m) {
            var clazz = classMap.get(m.getParent().getMapped());
            if (clazz == null) {
                return m.getMapped();
            }
            var methods = clazz.nArgMethods().get(m.getMapped());
            if (methods.isEmpty()) {
                return m.getMapped();
            }
            for (var method : methods) {
                if (m.getMappedDescriptor().startsWith(method.paramDescriptor())) {
                    return method.remapped();
                }
            }
            return m.getMapped();
        }
    }
}
