package dev.latvian.mods.rhino.mod;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.latvian.mods.rhino.mod.remapper.MappingIO;
import lombok.val;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * @see dev.latvian.mods.rhino.mod.forge.RhinoPropertiesImpl
 * @see dev.latvian.mods.rhino.mod.fabric.RhinoPropertiesImpl
 */
public class RhinoProperties {

	public static boolean generateMapping = false;
	public static boolean enableCompiler = false;
	public static int optimizationLevel = 0;
	public static boolean concurrentContext = true;

	@ExpectPlatform
	@Contract(value = " -> _", pure = true)
	public static Path getGameDir() {
		return null;
	}

	@ExpectPlatform
	@Contract(value = " -> _", pure = true)
	public static boolean isDev() {
		return false;
	}

	@ExpectPlatform
	@NotNull
	@Contract(value = "_ -> _", pure = true)
	public static InputStream openResource(String path) throws Exception {
		throw new AssertionError();
	}

	private static Properties properties;
	// public boolean forceLocalMappings;
	private static boolean writeProperties;

	static  {
		try {
			load();
		} catch (Exception ex) {
            MappingIO.LOGGER.error("Error happened during Rhino properties loading: \n\t{}", ex.toString());
		} catch (AssertionError e) {
			System.out.println("[ERROR]AssertionError happened. If you're not running Rhino in-game, this indicates a severely broken Jar!");
		}

        MappingIO.LOGGER.info("Rhino properties loaded.");
	}

	public static void load() throws IOException {
		properties = new Properties();
		val gameDir = getGameDir();
		if (gameDir == null) {
			return;
		}

		val propertiesFile = gameDir.resolve("rhino.local.properties").toAbsolutePath();
		writeProperties = false;

		if (Files.exists(propertiesFile)) {
			try (val reader = Files.newBufferedReader(propertiesFile)) {
				properties.load(reader);
			}
		} else {
			writeProperties = true;
		}

		generateMapping = getBool("generateMapping", false);
		enableCompiler = getBool("enableCompiler", false);
		optimizationLevel = getInt("optimizationLevel", 1);
		concurrentContext = getBool("concurrentContext", true);

		if (writeProperties) {
			save(propertiesFile);
		}
	}

	public static void save() throws IOException {
		val gameDir = getGameDir();
		if (gameDir == null) {
			return;
		}
		val propertiesFile = gameDir.resolve("rhino.local.properties").toAbsolutePath();
		save(propertiesFile);
	}

	private static void save(Path propertiesFile) throws IOException {
		try (val writer = Files.newBufferedWriter(propertiesFile)) {
			properties.store(writer, "Local properties for Rhino, please do not push this to version control if you don't know what you're doing!");
		}
	}

	private void remove(String key) {
		var s = properties.getProperty(key);

		if (s != null) {
			properties.remove(key);
			writeProperties = true;
		}
	}

	private static String get(String key, String def) {
		var s = properties.getProperty(key);

		if (s == null) {
			properties.setProperty(key, def);
			writeProperties = true;
			return def;
		}

		return s;
	}

	private static boolean getBool(String key, boolean def) {
		return get(key, Boolean.toString(def)).equals("true");
	}

	private static int getInt(String key, int def) {
		return Integer.parseInt(get(key, Integer.toString(def)));
	}
}
