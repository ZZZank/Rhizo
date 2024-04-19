package dev.latvian.mods.rhino.mod.forge;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.mod.RhinoProperties;
import dev.latvian.mods.rhino.mod.remapper.MojMappings;
import dev.latvian.mods.rhino.mod.remapper.RemappingHelper;
import dev.latvian.mods.rhino.util.remapper.AnnotatedRemapper;
import dev.latvian.mods.rhino.mod.remapper.CsvRemapper;
import dev.latvian.mods.rhino.util.remapper.SequencedRemapper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Mod("rhino")
public class RhinoModForge {

    public RhinoModForge() {
        FMLJavaModLoadingContext.get().getModEventBus().register(RhinoModForge.class);
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        Context.setRemapper(new SequencedRemapper(AnnotatedRemapper.INSTANCE, CsvRemapper.INSTANCE));
        if (RhinoProperties.INSTANCE.generateMapping) {
            RemappingHelper.run("1.16.5", RhinoModForge::generateMappings);
        }
    }

    private static void generateMappings(RemappingHelper.MappingContext context) throws Exception {
        //using an old, hardcoded link because the newest is using official mapping, while 1.16.5 is still using MCP
        final String link = "https://raw.githubusercontent.com/MinecraftForge/MCPConfig/0cdc6055297f0b30cf3e27e59317f229a30863a6/versions/release/1.16.5/joined.tsrg";
        final var pattern = Pattern.compile("[\t ]");

        List<String[]> srg = new ArrayList<>(100);
        try (var reader = new BufferedReader(RemappingHelper.createReader(link))) {
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
                s[0] = s[0].replace('/', '.');
                // TODO: class def getting seems not valid, no logs for member remapping
                current = context.mappings().getClass(s[0]);

                if (current != null) {
                    RemappingHelper.LOGGER.info("- Checking class {} ; {}", s[0], current.displayName);
                    RemappingHelper.LOGGER.info("- class rawName: {}, mmName: {}", current.rawName, current.mmName);
                } else {
                    RemappingHelper.LOGGER.info("- Skipping class {}", s[0]);
                }
                continue;//this line is consumed, go to next
            }
            if (current == null) {
                continue;
//                throw new IllegalStateException("Bad mapping file, there's class member showing up before any valid class definition!");
            }
            if (s.length == 4) {//method
                //    a (FF)Lcom/mojang/datafixers/util/Pair; func_226112_a_
                //["", "a", "(FF)Lcom/mojang/datafixers/util/Pair;", "func_226112_a_"]

                //old srg mapping does not include <init> or <cinit>, so no check needed

                var sigStr = s[2].substring(0, s[2].lastIndexOf(')') + 1).replace('/', '.');
                var sig = new MojMappings.NamedSignature(s[1], context.mappings().readSignatureFromDescriptor(sigStr));
                var m = current.members.get(sig);

                if (m != null && !m.mmName().equals(s[3])) {
                    m.unmappedName().setValue(s[3]);
                    RemappingHelper.LOGGER.info("Remapped method {}{} to {}", s[3], sigStr, m.mmName());
                } else if (m == null && !current.ignoredMembers.contains(sig)) {
                    RemappingHelper.LOGGER.info("Method {} [{}] not found!", s[3], sig);
                }
            } else if (s.length == 3) {//field, length==4?
                //    a field_226097_a_
                //['', 'a', 'field_226097_a_']
                var sig = new MojMappings.NamedSignature(s[1], null);
                var m = current.members.get(sig);

                if (m != null) {
                    if (!m.mmName().equals(s[2])) {
                        m.unmappedName().setValue(s[2]);
                        RemappingHelper.LOGGER.info("Remapped field {} [{}] to {}", s[2], m.rawName(), m.mmName());
                    }
                } else if (!current.ignoredMembers.contains(sig)) {
                    RemappingHelper.LOGGER.info("Field {} [{}] not found!", s[2], s[1]);
                }
            }
        }
    }
}
