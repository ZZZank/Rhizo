package dev.latvian.mods.rhino.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unused")
public class NBTTests {

	public static final RhinoTest TEST = new RhinoTest("nbt");

	@Test
	@DisplayName("Compound")
	public void compound() {
		TEST.test(
			"compound",
			String.join(
				"\n",
				"let compoundTagTest = NBT.compoundTag()",
				"",
				"const testObject = {",
				"a: -39, b: '2', c: 3439438.1",
				"}",
				"",
				"compoundTagTest.merge(testObject);",
				"console.info(compoundTagTest)"
			),
			String.join("\n", "{a:-39.0d,b:\"2\",c:3439438.1d}")
		);
	}

	@Test
	@DisplayName("List")
	public void list() {
		TEST.test(
			"list",
			String.join(
				"\n",
				"",
				"listTagTest.push('a')",
				"listTagTest.push('b')",
				"listTagTest.push('c')",
				"",
				"console.info(listTagTest)"
			),
			String.join("\n", "[\"a\",\"b\",\"c\"]")
		);
	}
}
