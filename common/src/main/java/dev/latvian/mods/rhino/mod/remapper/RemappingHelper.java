package dev.latvian.mods.rhino.mod.remapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class RemappingHelper {
    public static final Logger LOGGER = LogManager.getLogger("Rhizo Java Remapper");

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
