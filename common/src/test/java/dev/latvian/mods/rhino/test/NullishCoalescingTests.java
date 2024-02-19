package dev.latvian.mods.rhino.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unused")
public class NullishCoalescingTests {

	public static final RhinoTest TEST = new RhinoTest("nullishCoalescing");

	@Test
	@DisplayName("Both Non-Null")
	public void bothNonNull() {
		TEST.test(
			"bothNonNull",
			String.join("\n", "let a = 10", "let b = 20", "let c = a ?? b", "console.info(c)"),
			String.join("\n", "10.0")
		);
	}

	@Test
	@DisplayName("First Null")
	public void firstNull() {
		TEST.test(
			"firstNull",
			String.join("\n", "let a = null", "let b = 20", "let c = a ?? b", "console.info(c)"),
			String.join("\n", "20.0")
		);
	}

	@Test
	@DisplayName("First Undefined")
	public void firstUndefined() {
		TEST.test(
			"firstUndefined",
			String.join(
				"\n",
				"let a = undefined",
				"let b = 20",
				"let c = a ?? b",
				"console.info(c)"
			),
			String.join("\n", "20.0")
		);
	}

	@Test
	@DisplayName("First False")
	public void firstFalse() {
		TEST.test(
			"firstFalse",
			String.join("\n", "let a = false", "let b = 20", "let c = a ?? b", "console.info(c)"),
			String.join("\n", "false")
		);
	}

	@Test
	@DisplayName("First Zero")
	public void firstZero() {
		TEST.test(
			"firstZero",
			String.join("\n", "let a = 0", "let b = 20", "let c = a ?? b", "console.info(c)"),
			String.join("\n", "0.0")
		);
	}

	@Test
	@DisplayName("Second Null")
	public void secondNull() {
		TEST.test(
			"secondNull",
			String.join("\n", "let a = 10", "let b = null", "let c = a ?? b", "console.info(c)"),
			String.join("\n", "10.0")
		);
	}
}
