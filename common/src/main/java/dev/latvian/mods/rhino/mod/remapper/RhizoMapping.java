package dev.latvian.mods.rhino.mod.remapper;

import com.google.gson.JsonObject;
import dev.latvian.mods.rhino.mod.RhinoProperties;
import dev.latvian.mods.rhino.mod.util.JsonUtils;
import dev.latvian.mods.rhino.util.remapper.RemapperException;
import net.neoforged.srgutils.IMappingFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URLConnection;

public abstract class RhizoMapping {

    public interface NativeMappingLoader {
        IMappingFile load(String mcVersion);
    }

    /**
     * generate a mapping file called "mm.jsmappings", can provide conversion between raw name,
     * in-game name(srg name) and mapped name.
     * <p>
     * Official mapping provides raw name <-> mapped name conversion, and the param {@code callback}
     * should provide raw name <-> in-game name conversion
     *
     * @param mcVersion version of the game, like "1.16.5", "1.20.1"
     * @param callback  should provide "in-game name -> raw name" conversion
     */
    private static void generate(@NotNull String mcVersion, NativeMappingLoader callback) {
        if (RhinoProperties.isDev()) {
            RemappingHelper.buildMinecraftRemapper(true);
            return;
        }
        try {
            //load mappings
            var vanillaMapping = loadVanilla(mcVersion);
            var nativeMapping = callback.load(mcVersion);

        } catch (IOException e) {
            RemappingHelper.LOGGER.error("Mapping generation failed");
            e.printStackTrace();
        }


        RemappingHelper.LOGGER.info("Finished generating mappings!");
    }

    private static IMappingFile loadVanilla(@NotNull String mcVersion) throws IOException {
        //find info for provided `mcVersion`
        JsonObject verInfo = null;
        try (var metaInfoReader = RemappingHelper.createUrlReader("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json")) {
            for (var metaInfo : JsonUtils.GSON.fromJson(metaInfoReader, JsonObject.class)
                .get("versions")
                .getAsJsonArray()) {
                if (mcVersion.equals(metaInfo.getAsJsonObject().get("id").getAsString())) {
                    verInfo = metaInfo.getAsJsonObject();
                    break;
                }
            }
        }
        if (verInfo == null) {
            throw new RemapperException(String.format(
                "No version information for '%s' from official source",
                mcVersion
            ));
        }
        //read meta info from version info
        URLConnection mappingUrl = null;
        try (var metaReader = RemappingHelper.createUrlReader(verInfo.get("url").getAsString())) {
            var meta = JsonUtils.GSON.fromJson(metaReader, JsonObject.class);
            if (!(meta.get("downloads") instanceof JsonObject o)
                || !(o.get("client_mappings") instanceof JsonObject cmap)
                || !cmap.has("url")) {
                throw new RemapperException("This Minecraft version doesn't have mappings!");
            }
            mappingUrl = RemappingHelper.getUrlConnection(cmap.get("url").getAsString());
        }
        //generate mapping
        return IMappingFile.load(mappingUrl.getInputStream());
    }
}
