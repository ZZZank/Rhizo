package dev.latvian.mods.rhino.mod.forge;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.mod.util.MojMappings;
import dev.latvian.mods.rhino.mod.util.RemappingHelper;
import dev.latvian.mods.rhino.util.remapper.AnnotatedRemapper;
import dev.latvian.mods.rhino.util.remapper.CsvRemapper;
import dev.latvian.mods.rhino.util.remapper.SequencedRemapper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.BufferedReader;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Mod("rhino")
public class RhinoModForge {

    public RhinoModForge() {
        FMLJavaModLoadingContext.get().getModEventBus().register(RhinoModForge.class);
    }

    @SubscribeEvent
    public static void loaded(FMLCommonSetupEvent event) {
        Context.getContext().setRemapper(new SequencedRemapper(AnnotatedRemapper.INSTANCE, CsvRemapper.INSTANCE));
        if (RemappingHelper.GENERATE) {
            RemappingHelper.run("1.16.5", RhinoModForge::generateMappings);
        }
    }

    private static void generateMappings(RemappingHelper.MappingContext context) throws Exception {
        MojMappings.ClassDef current = null;

        List<String> srg = Collections.emptyList();
        try (var reader = new BufferedReader(RemappingHelper.createReader("https://raw.githubusercontent.com/MinecraftForge/MCPConfig/master/versions/release/" + context.mcVersion() + "/joined.tsrg"))) {
            srg = reader.lines().collect(Collectors.toList());
        }

        var pattern = Pattern.compile("[\t ]");

        for (int i = 1; i < srg.size(); i++) {
            var s = pattern.split(srg.get(i));

            if (s.length < 3 || s[1].isEmpty()) {
                continue;
            }

            if (!s[0].isEmpty()) {
                s[0] = s[0].replace('/', '.');
                current = context.mappings().getClass(s[0]);

                if (current != null) {
                    RemappingHelper.LOGGER.info("- Checking class {} ; {}", s[0], current.displayName);
                    RemappingHelper.LOGGER.info("- class rawName: {}, mmName: {}", current.rawName, current.mmName);
                } else {
                    RemappingHelper.LOGGER.info("- Skipping class {}", s[0]);
                }
            } else if (current != null) {
                if (s.length == 5) {
                    if (s[1].equals("<init>") || s[1].equals("<clinit>")) {
                        continue;
                    }

                    var sigs = s[2].substring(0, s[2].lastIndexOf(')') + 1).replace('/', '.');
                    var sig = new MojMappings.NamedSignature(s[1], context.mappings().readSignatureFromDescriptor(sigs));
                    var m = current.members.get(sig);

                    if (m != null && !m.mmName().equals(s[3])) {
                        m.unmappedName().setValue(s[3]);
                        RemappingHelper.LOGGER.info("Remapped method {}{} to {}", s[3], sigs, m.mmName());
                    } else if (m == null && !current.ignoredMembers.contains(sig)) {
                        RemappingHelper.LOGGER.info("Method {} [{}] not found!", s[3], sig);
                    }
                } else if (s.length == 4) {
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
}
