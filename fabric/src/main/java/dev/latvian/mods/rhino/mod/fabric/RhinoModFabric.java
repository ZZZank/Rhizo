package dev.latvian.mods.rhino.mod.fabric;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.mod.util.remapper.MojMappings;
import dev.latvian.mods.rhino.mod.util.remapper.RemappingHelper;
import dev.latvian.mods.rhino.util.remapper.AnnotatedRemapper;
import dev.latvian.mods.rhino.util.remapper.SequencedRemapper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.fabricmc.loader.api.ModContainer;

public class RhinoModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Context.setRemapper(new SequencedRemapper(AnnotatedRemapper.INSTANCE, RemappingHelper.getMinecraftRemapper()));
        if (RemappingHelper.GENERATE) {
            RemappingHelper.run(FabricLoader.getInstance()
                .getModContainer("minecraft")
                .map(ModContainer::getMetadata)
                .map(ModMetadata::getVersion)
                .map(Version::getFriendlyString)
                .orElse(""), RhinoModFabric::generateMappings);
        }
    }

    private static void generateMappings(RemappingHelper.MappingContext context) throws Exception {
        var runtimeNamespace = FabricLauncherBase.getLauncher().getTargetNamespace();
        var rawNamespace = "official";
        var tinyTree = FabricLauncherBase.getLauncher().getMappingConfiguration().getMappings();

        for (var classDef : tinyTree.getClasses()) {
            var unmappedClassName = classDef.getName(runtimeNamespace).replace('/', '.');
            var rawClassName = classDef.getName(rawNamespace);

            RemappingHelper.LOGGER.info("- Checking class {}", rawClassName);

            var mmClass = context.mappings().getClass(rawClassName.replace('/', '.'));

            if (mmClass != null) {
                if (!mmClass.mmName.equals(unmappedClassName)) {
                    mmClass.unmappedName().setValue(unmappedClassName);
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
                    var rawMethodDesc = methodDef.getDesc(tinyTree.getNamespaceId(rawNamespace));
                    var sig = new MojMappings.NamedSignature(rawMethodName, context.mappings()
                        .readSignatureFromDescriptor(rawMethodDesc));
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
}
