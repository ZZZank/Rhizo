package dev.latvian.mods.rhino.test.impl;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.ContextFactory;
import lombok.val;

public class TestContextFactory extends ContextFactory {
	@Override
	public Context enterContext() {
        return super.enterContext(new TestContext(this));
	}
}
