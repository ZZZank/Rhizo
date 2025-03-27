package dev.latvian.mods.rhino.test.impl.base;

import dev.latvian.mods.rhino.Context;

public class TestContext extends Context {
	public String testName = "";

	public TestContext(TestContextFactory factory) {
		super(factory);
	}

	public TestContext(TestContextFactory factory, String name) {
		super(factory);
		testName = name;
	}

	@Override
	public String toString() {
		return testName;
	}
}
