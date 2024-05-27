package dev.latvian.mods.rhino.mod.remapper;

import dev.latvian.mods.rhino.mod.RhinoProperties;
import dev.latvian.mods.rhino.mod.remapper.info.Clazz;
import dev.latvian.mods.rhino.util.JavaPortingHelper;
import dev.latvian.mods.rhino.util.remapper.Remapper;
import dev.latvian.mods.rhino.util.remapper.RemapperException;
import org.jetbrains.annotations.Nullable;

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
    private final Map<String, Clazz> classUnmap;

    private RhizoRemapper() {
        //init
        this.classMap = new HashMap<>();
        this.classUnmap = new HashMap<>();
        //load
        try (var in = locateMappingFile(RhizoMappingGen.MAPPING_FILENAME)) {
            if (in == null) {
                throw new RemapperException("No Rhizo mapping file available!");
            }
            if (in.read() != RhizoMappingGen.MAPPING_MARK) {
                throw new RemapperException("Invalid Rhizo mapping file!");
            }
            if (in.read() != RhizoMappingGen.MAPPING_VERSION) {
                throw new RemapperException(
                    "Rhizo mapping file version not matching expected version " + RhizoMappingGen.MAPPING_VERSION);
            }
            RemappingHelper.LOGGER.info("Loading mappings for {}", MappingIO.readUtf(in));
            final String SKIP_MARK = MappingIO.readUtf(in);
            //class
            final int classCount = MappingIO.readVarInt(in);
            for (int i = 0; i < classCount; i++) {
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
                    var paramDesc = MappingIO.readUtf(in);
                    var mappedM = MappingIO.readUtf(in);
                    clazz.acceptMethod(originalM, paramDesc, mappedM);
                }
                //field
                final int fieldCount = MappingIO.readVarInt(in);
                for (int j = 0; j < fieldCount; j++) {
                    var originalF = MappingIO.readUtf(in);
                    if (SKIP_MARK.equals(originalF)) {
                        continue;
                    }
                    var mappedF = MappingIO.readUtf(in);
                    clazz.acceptField(originalF, mappedF);
                }
            }
        } catch (Exception e) {
            RemappingHelper.LOGGER.error("Failed to load Rhizo Minecraft remapper!", e);
        }
    }

    private static InputStream locateMappingFile(String name) {
        var cfgPath = RhinoProperties.getGameDir().resolve("config/" + name);
        try {
            if (Files.exists(cfgPath)) {
                RemappingHelper.LOGGER.info("Found Rhizo mapping file from config/{}.", name);
                return new GZIPInputStream(Files.newInputStream(cfgPath));
            }
            var in = new GZIPInputStream(RhinoProperties.openResource(name));
            RemappingHelper.LOGGER.info("Found Rhizo mapping file from Rhizo mod jar.");
            return in;
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
        var clazz = new Clazz(original, remapped);
        this.classMap.put(original, clazz);
        this.classUnmap.put(remapped, clazz);
        return clazz;
    }

    @Override
    public String getMappedClass(Class<?> from) {
        var clz = getClazzFiltered(from);
        if (clz == null) {
            return NOT_REMAPPED;
        }
        return clz.remapped();
    }

    private @Nullable Clazz getClazzFiltered(Class<?> from) {
        if (from == null || from == Object.class || JavaPortingHelper.getPackageName(from).startsWith("java.")) {
            return null;
        }
        return classMap.get(from.getName());
    }

    @Override
    public String getUnmappedClass(String from) {
        var un = classUnmap.get(from);
        if (un == null) {
            return NOT_REMAPPED;
        }
        return un.original();
    }

    @Override
    public String getMappedField(Class<?> from, Field field) {
        var clazz = getClazzFiltered(from);
        if (clazz == null) {
            return NOT_REMAPPED;
        }
        var fInfo = clazz.fields().get(field.getName());
        if (fInfo == null) {
            return NOT_REMAPPED;
        }
        return fInfo.remapped();
    }

    @Override
    public String getMappedMethod(Class<?> from, Method method) {
        //class level
        var clazz = getClazzFiltered(from);
        if (clazz == null) {
            return NOT_REMAPPED;
        }
        //method name level
        var methods = clazz.methods().get(method.getName());
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
            if (m.paramDescriptor().equals(paramDesc)) {
                return m.remapped();
            }
        }
        //failed
        return NOT_REMAPPED;
    }

}
