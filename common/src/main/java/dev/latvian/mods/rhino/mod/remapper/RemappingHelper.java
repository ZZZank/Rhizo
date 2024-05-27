package dev.latvian.mods.rhino.mod.remapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RemappingHelper {
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

    public static Reader createUrlReader(String url) throws IOException {
        LOGGER.info("Fetching {}...", url);
        var connection = getUrlConnection(url);
        return new InputStreamReader(new BufferedInputStream(connection.getInputStream()), StandardCharsets.UTF_8);
    }

    /**
     * connect to an url, with more tolerance in timeout
     */
    public static @NotNull URLConnection getUrlConnection(String url) throws IOException {
        var connection = new URL(url).openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);
        return connection;
    }
}
