package dev.latvian.mods.rhino.mod.fabric;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.mod.RhinoProperties;
import dev.latvian.mods.rhino.mod.remapper.MinecraftRemapper;
import dev.latvian.mods.rhino.mod.remapper.info.Clazz;
import dev.latvian.mods.rhino.mod.remapper.info.MojMappings;
import dev.latvian.mods.rhino.mod.remapper.RemappingHelper;
import dev.latvian.mods.rhino.util.remapper.AnnotatedRemapper;
import dev.latvian.mods.rhino.util.remapper.SequencedRemapper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.fabricmc.loader.api.ModContainer;
import net.neoforged.srgutils.IMappingFile;
import net.neoforged.srgutils.IRenamer;

import java.util.HashMap;
import java.util.Map;

public class RhinoModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Context.setRemapper(new SequencedRemapper(AnnotatedRemapper.INSTANCE, MinecraftRemapper.instance()));
        if (RhinoProperties.INSTANCE.generateMapping) {
            RemappingHelper.run(FabricLoader.getInstance()
                .getModContainer("minecraft")
                .map(ModContainer::getMetadata)
                .map(ModMetadata::getVersion)
                .map(Version::getFriendlyString)
                .orElse(""), RhinoModFabric::generateMappings);
        }
    }

    private static IRenamer loadNativeMappingRenamer(String mcVersion, IMappingFile vanillaMapping) {
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
            classMap.put(unmappedC, clazz);
            //method
            for(var method: c.getMethods()) {
                var unmappedM = method.getName(runtimeNamespace);
                var rawM = method.getName(rawNamespace);
                var desc = method.getDesc(tree.getNamespaceId(runtimeNamespace));
                clazz.acceptMethod(unmappedM, desc, rawM);
            }
            //field
            for(var field: c.getFields()) {
                var unmappedF = field.getName(runtimeNamespace);
                var rawF = field.getName(rawNamespace);
                clazz.acceptField(unmappedF, rawF);
            }
        }
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
                for (var m: methods) {
                    if (value.getMappedDescriptor().startsWith(m.paramDescriptor())) {
                        return m.remapped();
                    }
                }
                return value.getMapped();
            }
        };
    }

    private static void generateMappings(String mcVersion, MojMappings mappings) throws Exception {
        var runtimeNamespace = FabricLauncherBase.getLauncher().getTargetNamespace();
        var rawNamespace = "official";
        var tree = FabricLauncherBase.getLauncher().getMappingConfiguration().getMappings();

        for (var classDef : tree.getClasses()) {
            var unmappedClassName = classDef.getName(runtimeNamespace).replace('/', '.');
            var rawClassName = classDef.getName(rawNamespace);

            RemappingHelper.LOGGER.info("- Checking class {}", rawClassName);

            var mmClass = mappings.getClass(rawClassName.replace('/', '.'));
            if (mmClass == null) {
                continue;
            }

            if (!mmClass.mmName.equals(unmappedClassName)) {
                mmClass.setUnmappedName(unmappedClassName);
            }

            RemappingHelper.LOGGER.info("Remapped class {} to {}", unmappedClassName, mmClass.displayName);

            for (var fieldDef : classDef.getFields()) {
                var rawFieldName = fieldDef.getName(rawNamespace);
                var sig = new MojMappings.NamedSignature(rawFieldName, null);
                var mmField = mmClass.members.get(sig);

                if (mmField != null) {
                    var unmappedFieldName = fieldDef.getName(runtimeNamespace);

                    if (!unmappedFieldName.equals(mmField.mmName())) {
                        mmField.unmappedName().setValue(unmappedFieldName);
                        RemappingHelper.LOGGER.info("Remapped field {} [{}] to {}",
                            unmappedFieldName,
                            mmField.rawName(),
                            mmField.mmName()
                        );
                    }
                } else if (!mmClass.ignoredMembers.contains(sig)) {
                    RemappingHelper.LOGGER.info("Field {} not found!", sig);
                }
            }

            for (var methodDef : classDef.getMethods()) {
                var rawMethodName = methodDef.getName(rawNamespace);
                var rawMethodDesc = methodDef.getDesc(tree.getNamespaceId(rawNamespace));
                var sig = new MojMappings.NamedSignature(rawMethodName, mappings.readSignatureFromDescriptor(rawMethodDesc));
                var mmMethod = mmClass.members.get(sig);

                if (mmMethod != null) {
                    var unmappedMethodName = methodDef.getName(runtimeNamespace);

                    if (!unmappedMethodName.equals(mmMethod.mmName())) {
                        mmMethod.unmappedName().setValue(unmappedMethodName);
                        RemappingHelper.LOGGER.info("Remapped method {}{} to {}",
                            unmappedMethodName,
                            rawMethodDesc,
                            mmMethod.mmName()
                        );
                    }
                } else if (!mmClass.ignoredMembers.contains(sig)) {
                    RemappingHelper.LOGGER.info("Method {} not found!", sig);
                }
            }
        }
    }
}
