import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class mmjs {

    public static void main(String[] args) {
        writeCsv_2("fields");
        writeCsv_2("methods");
    }

    public static void writeCsv_2(String fileName) {
        try (var reader = new BufferedReader(new FileReader(fileName + ".csv"))) {
            var lines = reader.lines().toList();
            var out = new FileOutputStream(fileName + ".jsmappings");
            out.write(27); // mark
            out.write(1); // version
            out.write(lines.size());
            for (String line : lines) {
                var spl = line.split(",");
                writeUtf(out, spl[0]);
                writeUtf(out, spl[1]);
            }
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println(String.format("File '%s' not found", fileName));
        } catch (Exception ioE) {
        }
    }

    public static void writeVarInt(OutputStream stream, int value)
            throws Exception {
        while ((value & -128) != 0) {
            stream.write(value & 127 | 128);
            value >>>= 7;
        }
        stream.write(value);
    }

    public static void writeUtf(OutputStream stream, String value)
            throws Exception {
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
                throw new Exception("VarInt too big");
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
