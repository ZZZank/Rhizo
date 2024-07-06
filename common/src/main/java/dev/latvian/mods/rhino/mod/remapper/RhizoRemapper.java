package dev.latvian.mods.rhino.mod.remapper;

import dev.latvian.mods.rhino.mod.RhinoProperties;
import dev.latvian.mods.rhino.mod.remapper.info.Clazz;
import dev.latvian.mods.rhino.util.JavaPortingHelper;
import dev.latvian.mods.rhino.util.remapper.Remapper;
import dev.latvian.mods.rhino.util.remapper.RemapperException;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Collections;
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
        try (val in = locateMappingFile(RhizoMappingGen.MAPPING_FILENAME)) {
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
            MappingIO.LOGGER.info("Loading mappings for {}", MappingIO.readUtf(in));
            val SKIP_MARK = MappingIO.readUtf(in);
            //class
            val classCount = MappingIO.readVarInt(in);
            for (int i = 0; i < classCount; i++) {
                val original = MappingIO.readUtf(in);
                if (SKIP_MARK.equals(original)) {
                    continue;
                }
                val mapped = MappingIO.readUtf(in);
                val clazz = acceptClass(original, mapped);
                //method
                val methodCount = MappingIO.readVarInt(in);
                for (int j = 0; j < methodCount; j++) {
                    val originalM = MappingIO.readUtf(in);
                    if (SKIP_MARK.equals(originalM)) {
                        continue;
                    }
                    val paramDesc = MappingIO.readUtf(in);
                    val mappedM = MappingIO.readUtf(in);
                    clazz.acceptMethod(originalM, paramDesc, mappedM);
                }
                //field
                val fieldCount = MappingIO.readVarInt(in);
                for (int j = 0; j < fieldCount; j++) {
                    val originalF = MappingIO.readUtf(in);
                    if (SKIP_MARK.equals(originalF)) {
                        continue;
                    }
                    val mappedF = MappingIO.readUtf(in);
                    clazz.acceptField(originalF, mappedF);
                }
            }
        } catch (Exception e) {
            MappingIO.LOGGER.error("Failed to load Rhizo Minecraft remapper!", e);
        }
    }

    private static InputStream locateMappingFile(String name) {
        val cfgPath = RhinoProperties.getGameDir().resolve("config/" + name);
        try {
            if (Files.exists(cfgPath)) {
                MappingIO.LOGGER.info("Found Rhizo mapping file from config/{}.", name);
                return new GZIPInputStream(Files.newInputStream(cfgPath));
            }
            val in = new GZIPInputStream(RhinoProperties.openResource(name));
            MappingIO.LOGGER.info("Found Rhizo mapping file from Rhizo mod jar.");
            return in;
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Clazz> getClazzMappingView() {
        return Collections.unmodifiableMap(classMap);
    }

    public Map<String, Clazz> getClazzUnmappingView() {
        return Collections.unmodifiableMap(classUnmap);
    }

    public static RhizoRemapper instance() {
        if (INSTANCE == null) {
            long start = System.currentTimeMillis();
            INSTANCE = new RhizoRemapper();
            MappingIO.LOGGER.info("Rhizo remapper initialization took {} milliseconds", System.currentTimeMillis()-start);
        }
        return INSTANCE;
    }

    Clazz acceptClass(String original, String remapped) {
        val clazz = new Clazz(original, remapped);
        this.classMap.put(original, clazz);
        this.classUnmap.put(remapped, clazz);
        return clazz;
    }

    @Override
    public String remapClass(Class<?> from) {
        val clz = getClazzFiltered(from);
        return clz == null ? NOT_REMAPPED : clz.remapped();
    }

    private @Nullable Clazz getClazzFiltered(Class<?> from) {
        if (from == null || from == Object.class || JavaPortingHelper.getPackageName(from).startsWith("java.")) {
            return null;
        }
        return classMap.get(from.getName());
    }

    @Override
    public String unmapClass(String from) {
        val un = classUnmap.get(from);
        return un == null ? NOT_REMAPPED : un.original();
    }

    @Override
    public String remapField(Class<?> from, Field field) {
        val clazz = getClazzFiltered(from);
        if (clazz == null) {
            return NOT_REMAPPED;
        }
        val fInfo = clazz.fields().get(field.getName());
        return fInfo == null ? NOT_REMAPPED : fInfo.remapped();
    }

    @Override
    public String remapMethod(Class<?> from, Method method) {
        //class level
        val clazz = getClazzFiltered(from);
        if (clazz == null) {
            return NOT_REMAPPED;
        }
        //method level
        val params = method.getParameterTypes();
        if (params.length == 0) {
            val noArgMethod = clazz.noArgMethods().get(method.getName());
            if (noArgMethod != null) {
                return noArgMethod.remapped();
            }
        } else {
            val nArgMethods = clazz.nArgMethods().get(method.getName());
            if (nArgMethods.isEmpty()) {
                return NOT_REMAPPED;
            }
            val sb = new StringBuilder().append('(');
            for (val t : params) {
                sb.append(JavaPortingHelper.descriptorString(t));
            }
            val paramDesc = sb.toString();
            for (val m : nArgMethods) {
                if (m.paramDescriptor().equals(paramDesc)) {
                    return m.remapped();
                }
            }
        }
        //failed
        return NOT_REMAPPED;
    }
}
