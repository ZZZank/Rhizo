package dev.latvian.mods.rhino.mod.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class RemappingHelper {

	public static final boolean GENERATE = System.getProperty("generaterhinomappings", "0").equals("1");
	private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().disableHtmlEscaping().create();
	public static final Logger LOGGER = LogManager.getLogger("Rhino Script Remapper");
	private static final Map<String, Optional<Class<?>>> CLASS_CACHE = new HashMap<>();

	private static Optional<Class<?>> loadClass(String name) {
		switch (name) {
			case "void": return Optional.of(Void.TYPE);
			case "boolean": return Optional.of(Boolean.TYPE);
			case "char": return Optional.of(Character.TYPE);
			case "byte": return Optional.of(Byte.TYPE);
			case "short": return Optional.of(Short.TYPE);
			case "int": return Optional.of(Integer.TYPE);
			case "long": return Optional.of(Long.TYPE);
			case "float": return Optional.of(Float.TYPE);
			case "double": return Optional.of(Double.TYPE);
			default: {
					try {
						return Optional.of(Class.forName(name));
					} catch (Exception ex) {
						return Optional.empty();
					}
				}
		}
	}

	public static Optional<Class<?>> getClass(String name) {
		return CLASS_CACHE.computeIfAbsent(name, RemappingHelper::loadClass);
	}

	public static class MappingContext {
	
		//TODO++: this is originally a record
		private final String mcVersion;
		private final MojangMappings mappings;
		public MappingContext(String mcVersion, MojangMappings mappings) {
			this.mappings = mappings;
			this.mcVersion = mcVersion;
	    }
		public String mcVersion() {
			return this.mcVersion;
		}
		public MojangMappings mappings() {
			return this.mappings;
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
			Path configPath = RhinoProperties.getGameDir().resolve("config/mm.jsmappings");

			if (Files.exists(configPath)) {
				try (BufferedInputStream in = new BufferedInputStream(new GZIPInputStream(Objects.requireNonNull(Files.newInputStream(configPath))))) {
					minecraftRemapper = MinecraftRemapper.load(in, debug);
				} catch (Exception ex) {
					ex.printStackTrace();
					LOGGER.error("Failed to load Rhino Minecraft remapper from config/mm.jsmappings!", ex);
					minecraftRemapper = new MinecraftRemapper(new HashMap<>(), new HashMap<>());
				}
			} else {
				try (BufferedInputStream in = new BufferedInputStream(new GZIPInputStream(Objects.requireNonNull(RhinoProperties.openResource("mm.jsmappings"))))) {
					minecraftRemapper = MinecraftRemapper.load(in, debug);
				} catch (Exception ex) {
					ex.printStackTrace();
					LOGGER.error("Failed to load Rhino Minecraft remapper from mod jar!", ex);
					minecraftRemapper = new MinecraftRemapper(new HashMap<>(), new HashMap<>());
				}
			}

			LOGGER.info(String.format("Done in %.03f s", (System.currentTimeMillis() - time) / 1000F));
		}

		return minecraftRemapper;
	}

	public static MinecraftRemapper getMinecraftRemapper() {
		return getMinecraftRemapper(false);
	}

	public static Reader createReader(String url) throws Exception {
		LOGGER.info("Fetching " + url + "...");
		URLConnection connection = new URL(url).openConnection();
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(10000);
		return new InputStreamReader(new BufferedInputStream(connection.getInputStream()), StandardCharsets.UTF_8);
	}

	public static void run(String mcVersion, Callback callback) {
		try {
			generate(mcVersion, callback);
		} catch (RuntimeException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static void generate(String mcVersion, Callback callback) throws Exception {
		if (mcVersion.isEmpty()) {
			throw new RuntimeException("Invalid Minecraft version!");
		}

		if (RhinoProperties.isDev()) {
			getMinecraftRemapper(true);
			return;
		}

		try (Reader metaInfoReader = createReader("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json")) {
			for (JsonElement metaInfo : GSON.fromJson(metaInfoReader, JsonObject.class).get("versions").getAsJsonArray()) {
				if (!metaInfo.getAsJsonObject().get("id").getAsString().equals(mcVersion)) {
					continue;
				}
				String metaUrl = metaInfo.getAsJsonObject().get("url").getAsString();

				try (Reader metaReader = createReader(metaUrl)) {
					JsonObject meta = GSON.fromJson(metaReader, JsonObject.class);

					JsonObject clientMappings = null;
					JsonElement tmp = meta.get("downloads");

					/*These lines are the same as:
					if (meta.get("downloads") instanceof JsonObject o 
						&& o.get("client_mappings") instanceof JsonObject cmap 
						&& cmap.has("url")) {
						try (Reader cmapReader = createReader(cmap.get("url").getAsString())) {
					*/
					if (tmp instanceof JsonObject) {
						tmp = ((JsonObject) tmp).get("client_mappings");
						if (tmp instanceof JsonObject) {
							clientMappings = (JsonObject) tmp;
							if (!clientMappings.has("url")) {
								clientMappings = null;
							}
						}
					}

					if (clientMappings == null) {
						throw new RemapperException("This Minecraft version doesn't have mappings!");
					}
					try (Reader cmapReader = createReader(clientMappings.get("url").getAsString())) {
						MojangMappings mojangMappings = MojangMappings.parse(mcVersion, IOUtils.readLines(cmapReader));
						callback.generateMappings(new MappingContext(mcVersion, mojangMappings));
						mojangMappings.cleanup();
						//TODO++: varify if its path is correct
						try (BufferedOutputStream out = new BufferedOutputStream(new GZIPOutputStream(Files.newOutputStream(FileSystems.getDefault().getPath("mm.jsmappings"))))) {
							mojangMappings.write(out);
						}

						LOGGER.info("Finished generating mappings!");
					}
				}
			}
		}

		throw new RemapperException("Failed for unknown reason!");
	}

	public static void writeVarInt(OutputStream stream, int value) throws Exception {
		while ((value & -128) != 0) {
			stream.write(value & 127 | 128);
			value >>>= 7;
		}

		stream.write(value);
	}

	public static void writeUtf(OutputStream stream, String value) throws Exception {
		byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
		writeVarInt(stream, bytes.length);
		stream.write(bytes);
	}

	public static int readVarInt(InputStream stream) throws Exception {
		int i = 0;
		int j = 0;

		byte b;
		do {
			b = (byte) stream.read();
			i |= (b & 127) << j++ * 7;
			if (j > 5) {
				throw new RemapperException("VarInt too big");
			}
		} while ((b & 128) == 128);

		return i;
	}

	public static String readUtf(InputStream stream) throws Exception {
		byte[] bytes = new byte[readVarInt(stream)];

		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) stream.read();
		}

		return new String(bytes, StandardCharsets.UTF_8);
	}
}
