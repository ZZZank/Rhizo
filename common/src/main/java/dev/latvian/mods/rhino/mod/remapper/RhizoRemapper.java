package dev.latvian.mods.rhino.mod.remapper;

import com.github.bsideup.jabel.Desugar;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.latvian.mods.rhino.mod.RhinoProperties;
import dev.latvian.mods.rhino.util.JavaPortingHelper;
import dev.latvian.mods.rhino.util.remapper.Remapper;
import dev.latvian.mods.rhino.util.remapper.RemapperException;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author ZZZank
 */
public class RhizoRemapper implements Remapper {

    private static RhizoRemapper INSTANCE = null;

    private final Map<String, Clazz> classMap;

    private RhizoRemapper() {
        //init
        this.classMap = new HashMap<>();
        //load
        try (var in = locateMappingFile("rhizo.jsmappings")) {
            if (in == null) {
                throw new RemapperException("No Minecraft Remapper file available!");
            }
            if (in.read() != RhizoMappingGen.MAPPING_MARK) {
                throw new RemapperException("Invalid Minecraft Remapper file!");
            }
            if (in.read() > RhizoMappingGen.MAPPING_VERSION) {
                throw new RemapperException("Minecraft Remapper file version too high!");
            }
            MappingIO.readUtf(in); //mc version
            final String SKIP_MARK = MappingIO.readUtf(in);
            final int classCount = MappingIO.readVarInt(in);
            for (int i = 0; i < classCount; i++) { //read class count
                var original = MappingIO.readUtf(in);
                if (SKIP_MARK.equals(original)) {
                    continue;
                }
                var mapped = MappingIO.readUtf(in);
                var clazz = acceptClass(original, mapped);
                //method
                final int methodCount = MappingIO.readVarInt(in);
                for (int j = 0; j < methodCount; j++) {
                    var originalM = MappingIO.readUtf(in);
                    if (SKIP_MARK.equals(originalM)) {
                        continue;
                    }
                    var descriptor = MappingIO.readUtf(in);
                    var mappedM = MappingIO.readUtf(in);
                    clazz.acceptMethod(originalM, descriptor, mappedM);
                }
                //field
                final int fieldCount = MappingIO.readVarInt(in);
                for (int j = 0; j < fieldCount; j++) {
                    var originalF = MappingIO.readUtf(in);
                    var mappedF = MappingIO.readUtf(in);
                    clazz.acceptField(originalF, mappedF);
                }
            }
        } catch (Exception e) {
            RemappingHelper.LOGGER.error("Failed to load Rhizo Minecraft remapper!", e);
        }
    }

    private static InputStream locateMappingFile(String name) throws IOException {
        var cfgPath = RhinoProperties.getGameDir().resolve("config/" + name);
        if (Files.exists(cfgPath)) {
            RemappingHelper.LOGGER.info("Found Rhizo mapping file from config/{}.", name);
            return new GZIPInputStream(Files.newInputStream(cfgPath));
        }
        try {
            RemappingHelper.LOGGER.info("Found Rhizo mapping file from Rhizo mod jar.");
            return new GZIPInputStream(RhinoProperties.openResource(name));
        } catch (Exception e) {
            return null;
        }
    }

    public static RhizoRemapper instance() {
        if (INSTANCE == null) {
            INSTANCE = new RhizoRemapper();
        }
        return INSTANCE;
    }

    Clazz acceptClass(String original, String remapped) {
        var clazz = new Clazz(original, remapped, ArrayListMultimap.create(), new HashMap<>());
        this.classMap.put(original, clazz);
        return clazz;
    }

    @Override
    public String getMappedClass(Class<?> from) {
        var clz = getClazzFiltered(from);
        if (clz == null) {
            return NOT_REMAPPED;
        }
        return clz.remapped;
    }

    private @Nullable Clazz getClazzFiltered(Class<?> from) {
        if (from == null || from == Object.class || JavaPortingHelper.getPackageName(from).startsWith("java.")) {
            return null;
        }
        return classMap.get(from.getName());
    }

    @Override
    public String getUnmappedClass(String from) {
        throw new AssertionError("not implemented yet");
    }

    @Override
    public String getMappedField(Class<?> from, Field field) {
        var clazz = getClazzFiltered(from);
        if (clazz == null) {
            return NOT_REMAPPED;
        }
        var fInfo = clazz.fields.get(field.getName());
        if (fInfo == null) {
            return NOT_REMAPPED;
        }
        return fInfo.remapped;
    }

    @Override
    public String getMappedMethod(Class<?> from, Method method) {
        //class level
        var clazz = getClazzFiltered(from);
        if (clazz == null) {
            return NOT_REMAPPED;
        }
        //method name level
        var methods = clazz.methods.get(method.getName());
        if (methods.isEmpty()) {
            return NOT_REMAPPED;
        }
        //parameters level
        var sb = new StringBuilder().append('(');
        for (var t : method.getParameterTypes()) {
            sb.append(JavaPortingHelper.descriptorString(t));
        }
        var paramDesc = sb.toString();
        for (var m : methods) {
            if (m.paramDescriptor.equals(paramDesc)) {
                return m.remapped;
            }
        }
        //failed
        return NOT_REMAPPED;
    }

    @Desugar
    record Clazz(String original, String remapped, Multimap<String, MethodInfo> methods,
                 Map<String, FieldInfo> fields) {
        void acceptMethod(String original, String descriptor, String remapped) {
            int rightBracket = descriptor.lastIndexOf(')');
            if (rightBracket < 0) {
                throw new IllegalArgumentException(String.format("arg 'paramDescriptor' with value '%s' not valid",
                    descriptor
                ));
            }
            this.methods.put(original, new MethodInfo(original, descriptor.substring(0, rightBracket), remapped));
        }

        void acceptField(String original, String remapped) {
            this.fields.put(original, new FieldInfo(original, remapped));
        }
    }

    @Desugar
    record MethodInfo(String original, String paramDescriptor, String remapped) {
    }

    @Desugar
    record FieldInfo(String original, String remapped) {
    }
}
