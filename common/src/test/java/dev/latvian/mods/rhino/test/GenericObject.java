package dev.latvian.mods.rhino.test;

import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class GenericObject<T> {
	public static final Map<String, String> ASSERTIONS;

    static {
        ASSERTIONS = new HashMap<>();
        ASSERTIONS.put("none", "?");
        ASSERTIONS.put("any", "?");
        ASSERTIONS.put("object", "?");
        ASSERTIONS.put("string", "java.lang.String");
        ASSERTIONS.put("anyString", "java.lang.CharSequence");
        ASSERTIONS.put("anySuperString", "java.lang.CharSequence");
        ASSERTIONS.put("t", "?");
        ASSERTIONS.put("tString", "java.lang.CharSequence");
        ASSERTIONS.put("k", "dev.latvian.mods.rhino.test.GenericObject<java.lang.CharSequence>");
    }

    public static String test = "";

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

	public GenericObject<? super CharSequence> anySuperString() {
		return null;
	}

	public <T> GenericObject<T> t() {
		return null;
	}

	public GenericObject<? extends CharSequence> tString() {
		return null;
	}

	public GenericObject<? extends GenericObject<? extends CharSequence>> k() {
		return null;
	}
}
