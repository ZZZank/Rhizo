package dev.latvian.mods.rhino.mod.fabric;

import dev.latvian.mods.rhino.mod.util.MojangMappings;
import dev.latvian.mods.rhino.mod.util.RemappingHelper;
import dev.latvian.mods.rhino.mod.util.MojangMappings.ClassDef;
import dev.latvian.mods.rhino.mod.util.MojangMappings.MemberDef;
import dev.latvian.mods.rhino.mod.util.MojangMappings.NamedSignature;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.fabricmc.loader.impl.lib.mappingio.tree.MappingTree;
import net.fabricmc.loader.impl.lib.mappingio.tree.MappingTree.ClassMapping;
import net.fabricmc.loader.impl.lib.mappingio.tree.MappingTree.FieldMapping;
import net.fabricmc.loader.impl.lib.mappingio.tree.MappingTree.MethodMapping;

public class RhinoModFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		if (RemappingHelper.GENERATE) {
			RemappingHelper.run(FabricLoader.getInstance().getModContainer("minecraft").map(ModContainer::getMetadata).map(m -> m.getVersion().getFriendlyString()).orElse(""), RhinoModFabric::generateMappings);
		}
	}

	private static void generateMappings(RemappingHelper.MappingContext context) throws Exception {
		String runtimeNamespace = FabricLauncherBase.getLauncher().getTargetNamespace();
		String rawNamespace = "official";
		MappingTree tinyTree = FabricLauncherBase.getLauncher().getMappingConfiguration().getMappings();

		for (ClassMapping classDef : tinyTree.getClasses()) {
			String unmappedClassName = classDef.getName(runtimeNamespace).replace('/', '.');
			String rawClassName = classDef.getName(rawNamespace);

			RemappingHelper.LOGGER.info("- Checking class " + rawClassName);

			ClassDef mmClass = context.mappings().getClass(rawClassName.replace('/', '.'));

			if (mmClass != null) {
				if (!mmClass.mmName.equals(unmappedClassName)) {
					mmClass.unmappedName().setValue(unmappedClassName);
				}

				RemappingHelper.LOGGER.info("Remapped class " + unmappedClassName + " to " + mmClass.displayName);

				for (FieldMapping fieldDef : classDef.getFields()) {
					String rawFieldName = fieldDef.getName(rawNamespace);
					NamedSignature sig = new MojangMappings.NamedSignature(rawFieldName, null);
					MemberDef mmField = mmClass.members.get(sig);

					if (mmField != null) {
						String unmappedFieldName = fieldDef.getName(runtimeNamespace);

						if (!unmappedFieldName.equals(mmField.mmName())) {
							mmField.unmappedName().setValue(unmappedFieldName);
							RemappingHelper.LOGGER.info("Remapped field " + unmappedFieldName + " [" + mmField.rawName() + "] to " + mmField.mmName());
						}
					} else if (!mmClass.ignoredMembers.contains(sig)) {
						RemappingHelper.LOGGER.info("Field " + sig + " not found!");
					}
				}

				for (MethodMapping methodDef : classDef.getMethods()) {
					String rawMethodName = methodDef.getName(rawNamespace);
					//TODO: verify getNamespaceId()
					String rawMethodDesc = methodDef.getDesc(methodDef.getTree().getNamespaceId(rawNamespace));
					NamedSignature sig = new MojangMappings.NamedSignature(rawMethodName, context.mappings().readSignatureFromDescriptor(rawMethodDesc));
					MemberDef mmMethod = mmClass.members.get(sig);

					if (mmMethod != null) {
						String unmappedMethodName = methodDef.getName(runtimeNamespace);

						if (!unmappedMethodName.equals(mmMethod.mmName())) {
							mmMethod.unmappedName().setValue(unmappedMethodName);
							RemappingHelper.LOGGER.info("Remapped method " + unmappedMethodName + rawMethodDesc + " to " + mmMethod.mmName());
						}
					} else if (!mmClass.ignoredMembers.contains(sig)) {
						RemappingHelper.LOGGER.info("Method " + sig + " not found!");
					}
				}
			}
		}
	}
}
