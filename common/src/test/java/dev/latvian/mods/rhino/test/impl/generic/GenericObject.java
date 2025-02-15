package dev.latvian.mods.rhino.test.impl.generic;

import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class GenericObject<T> {
	public static final Map<String, String> ASSERTIONS = new HashMap<>();

    static {
        ASSERTIONS.put("none", "?");
        ASSERTIONS.put("any", "?");
        ASSERTIONS.put("object", "?");
        ASSERTIONS.put("string", "java.lang.String");
        ASSERTIONS.put("anyString", "java.lang.CharSequence");
        ASSERTIONS.put("anySuperString", "java.lang.String");
        ASSERTIONS.put("t", "T_");
        ASSERTIONS.put("tString", "java.lang.CharSequence");
        ASSERTIONS.put("k", "dev.latvian.mods.rhino.test.impl.generic.GenericObject<java.lang.CharSequence>");
    }

    public static String testing = "";

	public static void test(String name, Type type) {
		var typeInfo = TypeInfo.of(type).param(0);
		Assertions.assertEquals(name + ": " + typeInfo, name + ": " + GenericObject.ASSERTIONS.get(name));
	}

	public GenericObject none() {
		return null;
	}

	public GenericObject<?> any() {
		return null;
	}

	public GenericObject<Object> object() {
		return null;
	}

	public GenericObject<String> string() {
		return null;
	}

	public GenericObject<? extends CharSequence> anyString() {
		return null;
	}

	public GenericObject<? super String> anySuperString() {
		return null;
	}

	public <T_> GenericObject<T_> t() {
		return null;
	}

	public GenericObject<? extends CharSequence> tString() {
		return null;
	}

	public GenericObject<? extends GenericObject<? extends CharSequence>> k() {
		return null;
	}
}
