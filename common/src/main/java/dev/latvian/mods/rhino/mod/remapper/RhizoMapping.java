package dev.latvian.mods.rhino.mod.remapper;

import com.google.gson.JsonObject;
import dev.latvian.mods.rhino.mod.RhinoProperties;
import dev.latvian.mods.rhino.mod.util.JsonUtils;
import dev.latvian.mods.rhino.util.JavaPortingHelper;
import dev.latvian.mods.rhino.util.remapper.RemapperException;
import net.neoforged.srgutils.IMappingFile;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.zip.GZIPOutputStream;

public abstract class RhizoMapping {

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
    public static void generate(@NotNull String mcVersion, NativeMappingLoader callback) {
        if (RhinoProperties.isDev()) {
            RemappingHelper.buildMinecraftRemapper(true);
            return;
        }
        try {
            //mapped -> obf
            var vanillaMapping = loadVanilla(mcVersion);
            //obf -> in-game
            var nativeMapping = callback.load(mcVersion);
            //in-game -> mapped
            var target = vanillaMapping.chain(nativeMapping).reverse();
            //write mapping
            try (var out = new GZIPOutputStream(Files.newOutputStream(JavaPortingHelper.ofPath("mm.jsmappings")))) {
                var classes = target.getClasses();
                //TODO: do not write lambda
                MappingIO.writeVarInt(out, classes.size());
                for (IMappingFile.IClass clazz : classes) {
                    MappingIO.writeUtf(out, clazz.getOriginal());
                    MappingIO.writeUtf(out, clazz.getMapped());
                    //method
                    var methods = clazz.getMethods();
                    MappingIO.writeVarInt(out, methods.size());
                    for (IMappingFile.IMethod method : methods) {
                        //TODO
                        method.getDescriptor();
                    }
                    //field
                    var fields = clazz.getFields();
                    MappingIO.writeVarInt(out, fields.size());
                    for (IMappingFile.IField field : fields) {
                        var desc = field.getDescriptor();
                        MappingIO.writeUtf(out, desc == null ? "" : desc);
                        desc = field.getMappedDescriptor();
                        MappingIO.writeUtf(out, desc == null ? "" : desc);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            RemappingHelper.LOGGER.error("Mapping generation failed");
            e.printStackTrace();
            return;
        }
        RemappingHelper.LOGGER.info("Finished generating mappings!");
    }

    /**
     * mapped -> obf
     */
    private static IMappingFile loadVanilla(@NotNull String mcVersion) throws IOException {
        //find info for provided `mcVersion`
        JsonObject verInfo = null;
        try (var metaInfoReader = RemappingHelper.createUrlReader(
            "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json")) {
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

    public interface NativeMappingLoader {
        /**
         * obf -> in-game
         */
        IMappingFile load(String mcVersion);
    }
}
