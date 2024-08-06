package dev.latvian.mods.rhino.mod.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @see dev.latvian.mods.rhino.mod.RhinoProperties
 */
public class RhinoPropertiesImpl {
	public static Path getGameDir() {
		return FMLLoader.getGamePath();
	}

	public static boolean isDev() {
		return !FMLLoader.isProduction();
	}

	@NotNull
	public static InputStream openResource(String path) throws Exception {
		return Files.newInputStream(ModList.get().getModFileById("rhino").getFile().findResource(path));
	}
}
