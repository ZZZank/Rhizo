package dev.latvian.mods.rhino.mod.fabric;

import dev.latvian.mods.rhino.mod.RhinoProperties;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @see dev.latvian.mods.rhino.mod.RhinoProperties
 */
public class RhinoPropertiesImpl extends RhinoProperties {
	public Path getGameDir() {
		return FabricLoader.getInstance().getGameDir();
	}

	public boolean isDev() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	@NotNull
	public InputStream openResource(String path) throws Exception {
		return Files.newInputStream(FabricLoader.getInstance().getModContainer("rhino").get().findPath(path).get());
	}
}
