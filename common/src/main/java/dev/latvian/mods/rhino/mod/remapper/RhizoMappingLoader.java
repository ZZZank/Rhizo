package dev.latvian.mods.rhino.mod.remapper;

import com.google.gson.JsonObject;
import dev.latvian.mods.rhino.mod.RhinoProperties;
import dev.latvian.mods.rhino.mod.util.JsonUtils;
import dev.latvian.mods.rhino.util.JavaPortingHelper;
import dev.latvian.mods.rhino.util.remapper.RemapperException;
import net.neoforged.srgutils.IMappingFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

public abstract class RhizoMappingLoader {

    //the number 31, is obtained by running SHA256 on string "lambda", and calculating `HASH mod 128`
    private static final String SKIP_MARK = "31";

    /**
     * generate a mapping file called "mm.jsmappings", that can provides name conversion between
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
            writeRhizoMapping(JavaPortingHelper.ofPath("mm.jsmappings"), target);
        } catch (IOException e) {
            RemappingHelper.LOGGER.error("Mapping generation failed");
            e.printStackTrace();
            return;
        }
        RemappingHelper.LOGGER.info("Finished generating mappings!");
    }

    private static void writeRhizoMapping(Path path, IMappingFile mapping) throws IOException {
        try (var out = new GZIPOutputStream(Files.newOutputStream(path))) {
            //used for auto fetching skip mark when parsing
            MappingIO.writeUtf(out, SKIP_MARK);
            //class
            var classes = mapping.getClasses();
            MappingIO.writeVarInt(out, classes.size());
            for (IMappingFile.IClass clazz : classes) {
                if (isAnonymousClass(clazz.getMapped())) {
                    MappingIO.writeUtf(out, SKIP_MARK);
                    continue;
                }
                MappingIO.writeUtf(out, clazz.getOriginal());
                MappingIO.writeUtf(out, clazz.getMapped());
                //method
                var methods = clazz.getMethods();
                MappingIO.writeVarInt(out, methods.size());
                for (IMappingFile.IMethod method : methods) {
                    if (method.getMapped().startsWith("lambda$")) {
                        MappingIO.writeUtf(out, SKIP_MARK);
                        continue;
                    }
                    MappingIO.writeUtf(out, method.getDescriptor());
                    MappingIO.writeUtf(out, method.getMapped());
                }
                //field
                var fields = clazz.getFields();
                MappingIO.writeVarInt(out, fields.size());
                for (IMappingFile.IField field : fields) {
                    var desc = field.getOriginal();
                    MappingIO.writeUtf(out, desc == null ? "" : desc);
                    desc = field.getMapped();
                    MappingIO.writeUtf(out, desc == null ? "" : desc);
                }
            }
        }
    }

    /**
     * @param className the one you can get via {@link Class#getName()}
     * @return true if the class that the name represents is an anonymous class
     */
    private static boolean isAnonymousClass(final String className) {
        var lastIndex = className.lastIndexOf('$');
        if (lastIndex < 0) {
            return false;
        }
        return className.substring(lastIndex + 1).chars().allMatch(c -> c >= '0' && c <= '9');
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
            throw new RemapperException(String.format("No version information for '%s' from official source",
                mcVersion
            ));
        }
        //read meta info from version info
        URLConnection mappingUrl = null;
        try (var metaReader = RemappingHelper.createUrlReader(verInfo.get("url").getAsString())) {
            var meta = JsonUtils.GSON.fromJson(metaReader, JsonObject.class);
            if (!(meta.get("downloads") instanceof JsonObject o)
                || !(o.get("client_mappings") instanceof JsonObject cmap) || !cmap.has("url")) {
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
