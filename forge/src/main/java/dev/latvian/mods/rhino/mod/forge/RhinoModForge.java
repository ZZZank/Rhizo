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

@Mod("rhino")
public class RhinoModForge {

    public RhinoModForge() {
        FMLJavaModLoadingContext.get().getModEventBus().register(RhinoModForge.class);
        Context.setRemapper(new SequencedRemapper(AnnotatedRemapper.INSTANCE, RhizoRemapper.instance()));
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        if (RhinoProperties.INSTANCE.generateMapping) {
            RhizoMappingGen.generate(
                "1.16.5",
                (mcVersion, vanillaMapping) -> IMappingFile.load(RemappingHelper.getUrlConnection(
                    "https://github.com/ZZZank/Rhizo/raw/1.16-rhizo/_dev/joined_old.tsrg").getInputStream())
            );
        }
    }
}
