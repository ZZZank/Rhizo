package dev.latvian.mods.rhino.util.remapper;

import dev.latvian.mods.rhino.mod.RhinoProperties;
import dev.latvian.mods.rhino.mod.util.RemappingHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * a {@link Remapper} impl based on file
 */
public class FileRemapper implements Remapper {

    private static final int VERSION = 1;
    private static final int MARK = 27;

    public static final FileRemapper INSTANCE = new FileRemapper();

    private final Map<String, String> fields;
    private final Map<String, String> methods;

    private FileRemapper() {
        this.fields = load("fields.jsmappings");
        this.methods = load("methods.jsmappings");
        RemappingHelper.LOGGER.info("FileRemapper loaded");
    }

    private static Map<String, String> load(String fileName) {
        try (var in = RhinoProperties.openResource(fileName)) {
            if (in.read() != MARK) {
                throw new Exception("Invalid jsmappings file for: " + fileName);
            }
            int version = in.read();
            if (version != VERSION) {
                throw new Exception("Invalid jsmappings file version for: " + fileName);
            }
            Map<String, String> map = new HashMap<>();
            final int size = RemappingHelper.readVarInt(in);
            for (int i = 0; i < size; i++) {
                map.put(RemappingHelper.readUtf(in), RemappingHelper.readUtf(in));
            }
            return map;
        } catch (Exception e) {
            RemappingHelper.LOGGER.error("Error when processing: {}", fileName);
        }
        return Collections.emptyMap();
    }

    @Override
    public String getMappedField(Class<?> from, Field field) {
        return this.fields.getOrDefault(field.getName(), "");
    }

    @Override
    public String getMappedMethod(Class<?> from, Method method) {
        return this.methods.getOrDefault(method.getName(), "");
    }
}
