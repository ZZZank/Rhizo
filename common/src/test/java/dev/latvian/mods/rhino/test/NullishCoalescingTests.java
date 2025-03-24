package dev.latvian.mods.rhino.test;

import dev.latvian.mods.rhino.test.impl.base.RhinoTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class NullishCoalescingTests {
	public static final RhinoTest TEST = new RhinoTest("nullishCoalescing")
        .withBinding("list", t -> new ArrayList<>());

	@Test
	public void bothNonNull() {
		TEST.test("bothNonNull", """
			let a = 10
			let b = 20
			let c = a ?? b
			console.info(c)
			""", """
			10
			""");
	}

	@Test
	public void firstNull() {
		TEST.test("firstNull", """
			let a = null
			let b = 20
			let c = a ?? b
			console.info(c)
			""", """
			20
			""");
	}

	@Test
	public void firstUndefined() {
		TEST.test("firstUndefined", """
			let a = undefined
			let b = 20
			let c = a ?? b
			console.info(c)
			""", """
			20
			""");
	}

	@Test
	public void firstFalse() {
		TEST.test("firstFalse", """
			let a = false
			let b = 20
			let c = a ?? b
			console.info(c)
			""", """
			false
			""");
	}

	@Test
	public void firstZero() {
		TEST.test("firstZero", """
			let a = 0
			let b = 20
			let c = a ?? b
			console.info(c)
			""", """
			0
			""");
	}

	@Test
	public void secondNull() {
		TEST.test("secondNull", """
			let a = 10
			let b = null
			let c = a ?? b
			console.info(c)
			""", """
			10
			""");
	}

    @Test
    public void evalOnce() {
        TEST.test("evalOnce", """
            console.log(123 ?? list.add("huh"))
            console.log(null ?? list.add("oh"))
            console.log(list.size())
            """, """
            123
            true
            1""");
    }
}
