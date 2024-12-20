package dev.latvian.mods.rhino.test.impl;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.ContextFactory;

public class TestContextFactory extends ContextFactory {
	@Override
	public Context enterContext() {
		return new TestContext(this);
	}
}
