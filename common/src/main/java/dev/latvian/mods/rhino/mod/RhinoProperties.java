package dev.latvian.mods.rhino.mod;

import dev.latvian.mods.rhino.mod.remapper.MappingIO;
import dev.latvian.mods.rhino.util.Lazy;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public abstract class RhinoProperties {

	private static final Lazy<RhinoProperties> implementation = Lazy.serviceLoader(RhinoProperties.class);

	public static RhinoProperties get() {
		return implementation.get();
	}

	public boolean generateMapping;
	public boolean enableCompiler;
	public int optimizationLevel;

	public abstract Path getGameDir();

	public abstract boolean isDev();

	@NotNull
	public abstract InputStream openResource(String path) throws Exception;

	private final Properties properties;
	// public boolean forceLocalMappings;
	private boolean writeProperties;

	 public RhinoProperties() {
		this.properties = new Properties();

		try {
			var propertiesFile = getGameDir().resolve("rhino.local.properties");
			writeProperties = false;

			if (Files.exists(propertiesFile)) {
				try (Reader reader = Files.newBufferedReader(propertiesFile)) {
					properties.load(reader);
				}
			} else {
				writeProperties = true;
			}

			// forceLocalMappings = get("forceLocalMappings", false);
			this.generateMapping = get("generateMapping", false);
			this.enableCompiler = get("enableCompiler", false);
			this.optimizationLevel = Integer.parseInt(get("optimizationLevel", "1"));

			if (writeProperties) {
				try (Writer writer = Files.newBufferedWriter(propertiesFile)) {
					properties.store(writer, "Local properties for Rhino, please do not push this to version control if you don't know what you're doing!");
				}
			}
		} catch (Exception ex) {
			MappingIO.LOGGER.info("Error happened during Rhino properties loading.");
			ex.printStackTrace();
		} catch (AssertionError e) {
			System.out.println("[ERROR]AssertionError happened. If you're not running Rhino in-game, this indicates a severely broken Jar!");
		}

		MappingIO.LOGGER.info("Rhino properties loaded.");
	}

	private void remove(String key) {
		var s = properties.getProperty(key);

		if (s != null) {
			properties.remove(key);
			writeProperties = true;
		}
	}

	private String get(String key, String def) {
		var s = properties.getProperty(key);

		if (s == null) {
			properties.setProperty(key, def);
			writeProperties = true;
			return def;
		}

		return s;
	}

	private boolean get(String key, boolean def) {
		return get(key, def ? "true" : "false").equals("true");
	}
}
