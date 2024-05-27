package dev.latvian.mods.rhino.mod.forge;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.mod.RhinoProperties;
import dev.latvian.mods.rhino.mod.remapper.*;
import dev.latvian.mods.rhino.util.remapper.AnnotatedRemapper;
import dev.latvian.mods.rhino.util.remapper.SequencedRemapper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.srgutils.IMappingFile;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Mod("rhino")
public class RhinoModForge {

    public RhinoModForge() {
        FMLJavaModLoadingContext.get().getModEventBus().register(RhinoModForge.class);
        //        Context.setRemapper(DefaultRemapper.INSTANCE);
        Context.setRemapper(new SequencedRemapper(AnnotatedRemapper.INSTANCE, RhizoRemapper.instance()));
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        if (RhinoProperties.INSTANCE.generateMapping) {
            RhizoMappingGen.generate(
                "1.16.5",
                mcVersion -> IMappingFile.load(RemappingHelper.getUrlConnection(
                        "https://raw.githubusercontent.com/ZZZank/Rhizo/1.16-rhizo/_dev/joined_old.tsrg")
                    .getInputStream())
            );
//            RemappingHelper.run("1.16.5", RhinoModForge::generateMappings);
        }
    }

    private static void generateMappings(RemappingHelper.MappingContext context) throws Exception {
        //using an old, hardcoded link because the newest is using another format of srg name, which is different from
        //names used in game
        final String link = "https://raw.githubusercontent.com/MinecraftForge/MCPConfig/0cdc6055297f0b30cf3e27e59317f229a30863a6/versions/release/1.16.5/joined.tsrg";
        final var pattern = Pattern.compile("[\t ]");

        List<String[]> srg = new ArrayList<>(100);
        try (var reader = new BufferedReader(RemappingHelper.createUrlReader(link))) {
            reader.lines()
                //split into slices to ease processing
                .map(pattern::split)
                //remove abnormal line
                .filter(parts -> parts.length > 1 && !parts[1].isEmpty())
                .forEach(srg::add);
        }

        MojMappings.ClassDef current = null;
        for (String[] s : srg) {
            /*example:
            a net/minecraft/util/math/vector/Matrix3f
	            a field_226097_a_
	            a (FF)Lcom/mojang/datafixers/util/Pair; func_226112_a_
             */
            if (!s[0].isEmpty()) {//found class def line, refresh "current"
                //a net/minecraft/util/math/vector/Matrix3f
                var rawName = s[0].replace('/', '.');
                var mcpName = s[1];//in-game name
                current = context.mappings().getClass(rawName);

                if (current != null) {
                    current.setUnmappedName(mcpName);
                    RemappingHelper.LOGGER.info("- Checking class {} ; {}", rawName, current.displayName);
                    RemappingHelper.LOGGER.info("- class rawName: {}, mmName: {}", current.rawName, current.mmName);
                } else {
                    RemappingHelper.LOGGER.info("- Skipping class {}", rawName);
                }
                continue;//this line is consumed, go to next
            }
            if (current == null) {
                continue;
            }
            if (s.length == 4) {//method
                //    a (FF)Lcom/mojang/datafixers/util/Pair; func_226112_a_
                //["", "a", "(FF)Lcom/mojang/datafixers/util/Pair;", "func_226112_a_"]

                //old srg mapping does not include <init> or <cinit>, so no check needed

                var rawName = s[1]; //also available in the end of srg name
                var srgName = s[3];
                var paramSigStr = s[2].substring(0, s[2].lastIndexOf(')') + 1).replace('/', '.');
                var paramSig = new MojMappings.NamedSignature(s[1],
                    context.mappings().readSignatureFromDescriptor(paramSigStr)
                );
                var mDef = current.members.get(paramSig);

                if (mDef != null && !mDef.mmName().equals(srgName)) {
                    mDef.unmappedName().setValue(srgName);
                    RemappingHelper.LOGGER.info("Remapped method {}{} to {}", srgName, paramSigStr, mDef.mmName());
                } else if (mDef == null && !current.ignoredMembers.contains(paramSig)) {
                    RemappingHelper.LOGGER.info("Method {} [{}] not found!", srgName, paramSig);
                }
            } else if (s.length == 3) {//field, length==4?
                //    a field_226097_a_
                //['', 'a', 'field_226097_a_']
                var rawName = s[1];
                var srgName = s[2];
                var sig = new MojMappings.NamedSignature(rawName, null);
                var fDef = current.members.get(sig);

                if (fDef != null) {
                    if (!fDef.mmName().equals(srgName)) {
                        fDef.unmappedName().setValue(srgName);
                        RemappingHelper.LOGGER.info("Remapped field {} [{}] to {}",
                            srgName,
                            fDef.rawName(),
                            fDef.mmName()
                        );
                    }
                } else if (!current.ignoredMembers.contains(sig)) {
                    RemappingHelper.LOGGER.info("Field {} [{}] not found!", srgName, rawName);
                }
            }
        }
    }
}
