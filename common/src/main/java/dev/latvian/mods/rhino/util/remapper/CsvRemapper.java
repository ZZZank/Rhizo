package dev.latvian.mods.rhino.util.remapper;

import dev.latvian.mods.rhino.mod.RhinoProperties;
import dev.latvian.mods.rhino.mod.util.RemappingHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * a Remapper impl based on CSV file
 */
public class CsvRemapper implements Remapper {

    public static final CsvRemapper INSTANCE = CsvRemapper.load();

    private final Map<String,String> fields;
    private final Map<String,String> methods;

    private CsvRemapper() {
        this.fields = new HashMap<>();
        this.methods = new HashMap<>();
    }

    private static CsvRemapper load() {
        CsvRemapper remapper = new CsvRemapper();
        remapper.fields.putAll(loadCsv("fields.csv"));
        remapper.methods.putAll(loadCsv("method.csv"));
        return remapper;
    }

    private static Map<String,String> loadCsv(String fileName) {
        try (var reader = new BufferedReader(new InputStreamReader(RhinoProperties.openResource(fileName)))) {
            Map<String,String> map = new HashMap<>();
            var lines = reader.lines().collect(Collectors.toList());
            for (var line : lines) {
                var split = line.split(",");
                map.put(split[0], split[1]);
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
