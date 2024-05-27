package dev.latvian.mods.rhino.mod.fabric;

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
                new RhizoMappingGen.NativeMappingLoader() {
                    @Override
                    public @Nullable IMappingFile load(String mcVersion,
                        IMappingFile vanillaMapping) throws IOException {
                        return null;
                    }

                    @Override
                    public IRenamer toRenamer(IMappingFile link) {
                        var classMap = loadNativeMappingClassMap();
                        return new IRenamer() {
                            public String rename(IMappingFile.IClass value) {
                                var clazz = classMap.get(value.getMapped());
                                if (clazz == null) {
                                    return value.getMapped();
                                }
                                return clazz.remapped();
                            }

                            public String rename(IMappingFile.IField value) {
                                var clazz = classMap.get(value.getParent().getMapped());
                                if (clazz == null) {
                                    return value.getMapped();
                                }
                                var f = clazz.fields().get(value.getMapped());
                                if (f == null) {
                                    return value.getMapped();
                                }
                                return f.remapped();
                            }

                            public String rename(IMappingFile.IMethod value) {
                                var clazz = classMap.get(value.getParent().getMapped());
                                if (clazz == null) {
                                    return value.getMapped();
                                }
                                var methods = clazz.methods().get(value.getMapped());
                                if (methods.isEmpty()) {
                                    return value.getMapped();
                                }
                                for (var m : methods) {
                                    if (value.getMappedDescriptor().startsWith(m.paramDescriptor())) {
                                        return m.remapped();
                                    }
                                }
                                return value.getMapped();
                            }
                        };
                    }
                }
            );
        }
    }

    private static Map<String, Clazz> loadNativeMappingClassMap() {
        final var runtimeNamespace = FabricLauncherBase.getLauncher().getTargetNamespace();
        final var rawNamespace = "official";
        final var tree = FabricLauncherBase.getLauncher().getMappingConfiguration().getMappings();

        final Map<String, Clazz> classMap = new HashMap<>();
        //class
        for (var c : tree.getClasses()) {
            //clazz
            //similar to SRG name in Forge
            var unmappedC = c.getName(runtimeNamespace).replace('/', '.');
            //obf name
            var rawC = c.getName(rawNamespace).replace('/', '.');
            var clazz = new Clazz(unmappedC, rawC);
            classMap.put(rawC, clazz);
            //method
            for (var method : c.getMethods()) {
                var unmappedM = method.getName(runtimeNamespace);
                var rawM = method.getName(rawNamespace);
                var desc = method.getDesc(tree.getNamespaceId(runtimeNamespace));
                clazz.acceptMethod(rawM, desc, unmappedM);
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
}
