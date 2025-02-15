package dev.latvian.mods.rhino.test.impl.base;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.ContextFactory;

public class TestContextFactory extends ContextFactory {
	@Override
	public Context enterContext() {
        return super.enterContext(new TestContext(this));
	}
}
