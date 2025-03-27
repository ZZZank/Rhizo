package dev.latvian.mods.rhino.test.impl.base;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.ContextFactory;

public class TestContextFactory extends ContextFactory {
	public String name;

	public TestContextFactory(String name) {
		this.name = name;
	}

	public TestContextFactory() {
		this("name-not-provided");
	}

	@Override
	public Context enterContext() {
        return super.enterContext(new TestContext(this, name));
	}

	@Override
	public String toString() {
		return name;
	}
}
