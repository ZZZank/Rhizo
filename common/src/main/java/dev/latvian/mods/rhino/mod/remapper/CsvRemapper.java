package dev.latvian.mods.rhino.mod.remapper;

import dev.latvian.mods.rhino.mod.RhinoProperties;
import dev.latvian.mods.rhino.util.remapper.Remapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * a {@link Remapper} impl based on CSV file
 */
public class CsvRemapper implements Remapper {

    public static final CsvRemapper INSTANCE = new CsvRemapper();
    private static final int VERSION = 1;
    private static final int MARK = 27;
    private final Map<String, String> fields;
    private final Map<String, String> methods;

    private CsvRemapper() {
        this.fields = load("field_.csv");
        this.methods = load("func_.csv");
        RemappingHelper.LOGGER.info("CsvRemapper loaded");
    }

    private static Map<String, String> load(String fileName) {
        //content preparing
        List<String[]> lines = new ArrayList<>();
        try (var in = new BufferedReader(new InputStreamReader(RhinoProperties.openResource(fileName)))) {
            in.lines().map(s -> s.split(",")).forEach(lines::add);
        } catch (Exception ignored) {
            RemappingHelper.LOGGER.error("Error when trying to read '{}' CSV file", fileName);
        }
        //read
        var mapping = new HashMap<String, String>();
        for (String[] line : lines) {
            mapping.put(line[0], line[1]);
        }
        return mapping;
    }

    @Override
    public String getMappedField(Class<?> from, Field field) {
        String name = field.getName();
        if (!name.startsWith("field_")) {
            return "";
        }
        return this.fields.getOrDefault(name.substring(6), "");
    }

    @Override
    public String getMappedMethod(Class<?> from, Method method) {
        String name = method.getName();
        if (!name.startsWith("func_")) {
            return "";
        }
        return this.methods.getOrDefault(name.substring(5), "");
    }
}
