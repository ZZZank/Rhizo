package dev.latvian.mods.rhino.mod.remapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.latvian.mods.rhino.mod.RhinoProperties;
import dev.latvian.mods.rhino.util.JavaPortingHelper;
import dev.latvian.mods.rhino.util.remapper.RemapperException;
import org.apache.commons.io.IOUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class RemappingHelper {
    //    public static final boolean GENERATE = true;
    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().disableHtmlEscaping().create();
    public static final Logger LOGGER = LogManager.getLogger("Rhino Script Remapper");
    private static final Map<String, Optional<Class<?>>> CLASS_CACHE = new HashMap<>();

    private static Optional<Class<?>> loadClass(String name) {
        return switch (name) {
            case "void" -> Optional.of(Void.TYPE);
            case "boolean" -> Optional.of(Boolean.TYPE);
            case "char" -> Optional.of(Character.TYPE);
            case "byte" -> Optional.of(Byte.TYPE);
            case "short" -> Optional.of(Short.TYPE);
            case "int" -> Optional.of(Integer.TYPE);
            case "long" -> Optional.of(Long.TYPE);
            case "float" -> Optional.of(Float.TYPE);
            case "double" -> Optional.of(Double.TYPE);
            default -> {
                try {
                    yield Optional.of(Class.forName(name));
                } catch (Exception ex) {
                    yield Optional.empty();
                }
            }
        };
    }

    public static Optional<Class<?>> getClass(String name) {
        return CLASS_CACHE.computeIfAbsent(name, RemappingHelper::loadClass);
    }

    public static final class MappingContext {
        private final String mcVersion;
        private final MojMappings mappings;

        public MappingContext(String mcVersion, MojMappings mappings) {
            this.mcVersion = mcVersion;
            this.mappings = mappings;
        }

        public String mcVersion() {
            return mcVersion;
        }

        public MojMappings mappings() {
            return mappings;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            var that = (MappingContext) obj;
            return Objects.equals(this.mcVersion, that.mcVersion) &&
                Objects.equals(this.mappings, that.mappings);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mcVersion, mappings);
        }

        @Override
        public String toString() {
            return "MappingContext[" +
                "mcVersion=" + mcVersion + ", " +
                "mappings=" + mappings + ']';
        }

    }

    public interface Callback {
        void generateMappings(MappingContext context) throws Exception;
    }

    private static MinecraftRemapper minecraftRemapper = null;

    public static MinecraftRemapper getMinecraftRemapper(boolean debug) {
        if (minecraftRemapper == null) {
            LOGGER.info("Loading Rhino Minecraft remapper...");
            long time = System.currentTimeMillis();
            minecraftRemapper = buildMinecraftRemapper(debug);
            LOGGER.info(String.format("Done in %.03f s", (System.currentTimeMillis() - time) / 1000F));
        }

        return minecraftRemapper;
    }

    /**
     * build a remapper via "mm.jsmappings" file from either rhino.jar or "config" folder
     */
    public static MinecraftRemapper buildMinecraftRemapper(boolean debug) {
        var configPath = RhinoProperties.getGameDir().resolve("config/mm.jsmappings");

        if (Files.exists(configPath)) {
            LOGGER.info("Loading Rhino Minecraft remapper from config/mm.jsmappings.");
            try (var in = new BufferedInputStream(new GZIPInputStream(Objects.requireNonNull(Files.newInputStream(
                configPath))))) {
                return MinecraftRemapper.load(in, debug);
            } catch (Exception ex) {
                LOGGER.error("Failed to load Rhino Minecraft remapper from config/mm.jsmappings!", ex);
                return new MinecraftRemapper(Collections.emptyMap(), Collections.emptyMap());
            }
        } else {
            LOGGER.info("Loading Rhino Minecraft remapper from Rhino jar file.");
            try (var in = new BufferedInputStream(new GZIPInputStream(Objects.requireNonNull(RhinoProperties.openResource(
                "mm.jsmappings"))))) {
                return MinecraftRemapper.load(in, debug);
            } catch (Exception ex) {
                LOGGER.error("Failed to load Rhino Minecraft remapper from mod jar!", ex);
                return new MinecraftRemapper(Collections.emptyMap(), Collections.emptyMap());
            }
        }
    }

    public static MinecraftRemapper getMinecraftRemapper() {
        return getMinecraftRemapper(false);
    }

    public static Reader createUrlReader(String url) throws Exception {
        LOGGER.info("Fetching {}...", url);
        var connection = getUrlConnection(url);
        return new InputStreamReader(new BufferedInputStream(connection.getInputStream()), StandardCharsets.UTF_8);
    }

    private static @NotNull URLConnection getUrlConnection(String url) throws IOException {
        var connection = new URL(url).openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);
        return connection;
    }

//    public static void main(String[] args) {
//        run("1.16.5", null);
//    }

    public static void run(String mcVersion, Callback callback) {
        try {
            generate(mcVersion, callback);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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
    private static void generate(String mcVersion, Callback callback) throws Exception {
        if (mcVersion.isEmpty()) {
            throw new RuntimeException("Invalid Minecraft version!");
        }

        if (RhinoProperties.isDev()) {
            buildMinecraftRemapper(true);
            return;
        }

        JsonObject vInfo = null;
        try (var metaInfoReader = createUrlReader("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json")) {
            for (var metaInfo : GSON.fromJson(metaInfoReader, JsonObject.class).get("versions").getAsJsonArray()) {
                if (mcVersion.equals(metaInfo.getAsJsonObject().get("id").getAsString())) {
                    vInfo = metaInfo.getAsJsonObject();
                    break;
                }
            }
        }
        if (vInfo == null) {
            throw new RemapperException(String.format("No version information for '%s' from official source", mcVersion));
        }

        String metaUrl = vInfo.get("url").getAsString();
        try (var metaReader = createUrlReader(metaUrl)) {
            var meta = GSON.fromJson(metaReader, JsonObject.class);
            if (!(meta.get("downloads") instanceof JsonObject o)
                || !(o.get("client_mappings") instanceof JsonObject cmap)
                || !cmap.has("url")) {
                throw new RemapperException("This Minecraft version doesn't have mappings!");
            }
            try (var cmapReader = createUrlReader(cmap.get("url").getAsString())) {
                var mojangMappings = MojMappings.parseOfficial(mcVersion, IOUtils.readLines(cmapReader));
                callback.generateMappings(new MappingContext(mcVersion, mojangMappings));
                mojangMappings.cleanup();

                try (var out = new BufferedOutputStream(new GZIPOutputStream(Files.newOutputStream(JavaPortingHelper.ofPath(
                    "mm.jsmappings"))))) {
                    mojangMappings.write(out);
                }

                LOGGER.info("Finished generating mappings!");
                return;
            }
        }

        //throw new RemapperException("Failed for unknown reason!");
    }
}
