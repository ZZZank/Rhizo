package dev.latvian.mods.rhino.mod.remapper;

import dev.latvian.mods.rhino.util.remapper.RemapperException;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class MappingIO {

    public interface Handler<T> {
        /**
         * write data into stream. Implementation of this should write enough data for {@link Handler#read(InputStream)}
         * to rebuild the object itself
         */
        void write(OutputStream stream);

        /**
         * read data from stream, and build a new object from these data. Implementation of this should consume all written
         * data of such type, and should NOT return caller of this method
         * @return a NEW object containing data fetched from stream
         */
        T read(InputStream stream);
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
