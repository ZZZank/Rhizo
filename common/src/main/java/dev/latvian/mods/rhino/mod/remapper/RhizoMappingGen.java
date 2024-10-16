package dev.latvian.mods.rhino.mod.remapper;

import com.google.gson.JsonObject;
import dev.latvian.mods.rhino.mod.util.JsonUtils;
import dev.latvian.mods.rhino.util.JavaPortingHelper;
import dev.latvian.mods.rhino.util.remapper.RemapperException;
import lombok.val;
import net.neoforged.srgutils.IMappingFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

public abstract class RhizoMappingGen {

    public static final String SKIP_MARK = "~";
    public static final int MAPPING_MARK = 21;
    public static final int MAPPING_VERSION = 3;
    public static final String MAPPING_FILENAME = "rhizo.jsmapping";

    /**
     * generate a mapping file called {@link RhizoMappingGen#MAPPING_FILENAME}, that can provides name conversion between
     * in-game name(srg name?) and mapped name.
     * <p>
     * Official mapping provides raw name <-> mapped name conversion, and the param {@code callback}
     * should provide raw name <-> in-game name conversion
     *
     * @param mcVersion version of the game, like "1.16.5", "1.20.1"
     */
    public static void generate(@NotNull String mcVersion) {
        try {
            //mapped -> obf
            val vanillaMapping = loadVanilla(mcVersion);
            //mapped -> in-game
            val transformer = MappingTransformer.get();
            val renamed = transformer.transform(vanillaMapping);
            //in-game -> mapped
            val target = renamed.reverse();
            //write mapping
            writeRhizoMapping(JavaPortingHelper.ofPath(MAPPING_FILENAME), target, mcVersion);
        } catch (Exception e) {
            MappingIO.LOGGER.error("Mapping generation failed", e);
            return;
        }
        MappingIO.LOGGER.info("Mapping generation finished!");
    }

    /**
     * write mapping data into specified file path, in a special format
     */
    private static void writeRhizoMapping(
        @NotNull Path path,
        @NotNull IMappingFile mapping,
        @NotNull String mcVersion
    ) throws IOException {
        val transformer = MappingTransformer.get();

        val out = new GZIPOutputStream(Files.newOutputStream(path));
        MappingIO.LOGGER.info("writing Rhizo mapping.");
        //metadata
        out.write(MAPPING_MARK); //minecraft mapping mark
        out.write(MAPPING_VERSION); //mapping version
        MappingIO.writeUtf(out, mcVersion); //minecraft version
        //class
        val classes = mapping.getClasses();
        MappingIO.writeVarInt(out, classes.size());
        for (IMappingFile.IClass clazz : classes) {
            if (!transformer.filterClass(clazz)) {
                MappingIO.writeUtf(out, SKIP_MARK);
                continue;
            }
            val originalC = clazz.getOriginal().replace('/', '.');
            val mappedC = clazz.getMapped().replace('/', '.');
            MappingIO.writeUtf(out, originalC);
            MappingIO.writeUtf(out, mappedC);
            MappingIO.LOGGER.info("class: '{}' -> '{}'", originalC, mappedC);
            //method
            val methods = clazz.getMethods();
            MappingIO.writeVarInt(out, methods.size());
            for (IMappingFile.IMethod method : methods) {
                val originalM = method.getOriginal();
                val mappedM = method.getMapped();
                if (!transformer.filterMethod(method)) {
                    MappingIO.writeUtf(out, SKIP_MARK);
                    continue;
                }
                MappingIO.LOGGER.info("    method: '{}' -> '{}'", originalM, mappedM);
                MappingIO.writeUtf(out, transformer.trimMethod(originalM));
                MappingIO.writeUtf(out, mappedM);
            }
            //field
            val fields = clazz.getFields();
            MappingIO.writeVarInt(out, fields.size());
            for (IMappingFile.IField field : fields) {
                val originalF = field.getOriginal();
                val mappedF = field.getMapped();
                if (!transformer.filterField(field)) {
                    MappingIO.writeUtf(out, SKIP_MARK);
                    continue;
                }
                MappingIO.writeUtf(out, transformer.trimField(originalF));
                MappingIO.writeUtf(out, mappedF);
                MappingIO.LOGGER.info("    field: '{}' -> '{}'", originalF, mappedF);
            }
        }
        out.close();
    }

    /**
     * @param className the one you can get via {@link Class#getName()}
     * @return true if the class that the name represents is an anonymous class
     */
    static boolean isAnonymousClass(final String className) {
        val lastIndex = className.lastIndexOf('$');
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
        try (val metaInfoReader = MappingIO.createUrlReader(
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
        try (val metaReader = MappingIO.createUrlReader(verInfo.get("url").getAsString())) {
            val meta = JsonUtils.GSON.fromJson(metaReader, JsonObject.class);
            if (meta.get("downloads") instanceof JsonObject o
                && o.get("client_mappings") instanceof JsonObject cmap
                && cmap.has("url")
            ) {
                mappingUrl = MappingIO.getUrlConnection(cmap.get("url").getAsString());
            } else {
                throw new RemapperException("This Minecraft version doesn't have mappings!");
            }
        }
        //generate mapping
        return IMappingFile.load(mappingUrl.getInputStream());
    }
}
