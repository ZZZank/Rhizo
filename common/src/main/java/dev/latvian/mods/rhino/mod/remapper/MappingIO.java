package dev.latvian.mods.rhino.mod.remapper;

import dev.latvian.mods.rhino.util.remapper.RemapperException;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public interface MappingIO {

    Logger LOGGER = LogManager.getLogger("Rhizo Java Remapper");

    static void writeVarInt(OutputStream stream, int value) throws IOException {
        while ((value & -128) != 0) {
            stream.write(value & 127 | 128);
            value >>>= 7;
        }

        stream.write(value);
    }

    static void writeUtf(OutputStream stream, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeVarInt(stream, bytes.length);
        stream.write(bytes);
    }

    static int readVarInt(InputStream stream) throws IOException {
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

    static String readUtf(InputStream stream) throws IOException {
        byte[] bytes = new byte[readVarInt(stream)];

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) stream.read();
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }

    static Reader createUrlReader(String url) throws IOException {
        LOGGER.info("Fetching {}...", url);
        val connection = getUrlConnection(url);
        return new InputStreamReader(new BufferedInputStream(connection.getInputStream()), StandardCharsets.UTF_8);
    }

    /**
     * connect to an url, with more tolerance in timeout
     */
    static @NotNull URLConnection getUrlConnection(String url) throws IOException {
        val connection = new URL(url).openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);
        return connection;
    }
}
