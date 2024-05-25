package dev.latvian.mods.rhino.mod.remapper;

import dev.latvian.mods.rhino.util.remapper.RemapperException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class MappingIO {

    public static void writeVarInt(OutputStream stream, int value) throws IOException {
        while ((value & -128) != 0) {
            stream.write(value & 127 | 128);
            value >>>= 7;
        }

        stream.write(value);
    }

    public static void writeUtf(OutputStream stream, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeVarInt(stream, bytes.length);
        stream.write(bytes);
    }

    public static int readVarInt(InputStream stream) throws IOException {
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

    public static String readUtf(InputStream stream) throws IOException {
        byte[] bytes = new byte[readVarInt(stream)];

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) stream.read();
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }
}
