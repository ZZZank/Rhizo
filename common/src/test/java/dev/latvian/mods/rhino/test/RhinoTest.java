package dev.latvian.mods.rhino.test;

import dev.latvian.mods.rhino.*;
import dev.latvian.mods.rhino.test.impl.*;
import lombok.val;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;

public class RhinoTest {
	public final String testName;
	public final ContextFactory factory;
	public TestConsole console;
	public final Map<String, Object> shared;

	public RhinoTest(String n) {
		this.testName = n;
		this.factory = new TestContextFactory();
		this.console = new TestConsole(factory);
		this.shared = new HashMap<>();

		var typeWrappers = factory.getTypeWrappers();
		typeWrappers.register(TestMaterial.class, TestMaterial::get);
		typeWrappers.registerNew(WithContext.class, WithContext::of);
		typeWrappers.registerNew(Holder.class, Holder::of);
	}

	public static void addToScope(Context cx, Scriptable scope, String name, Object value) {
		if (value instanceof Class<?> c) {
			ScriptableObject.putProperty(scope, name, new NativeJavaClass(cx, scope, c));
		} else {
			ScriptableObject.putProperty(scope, name, Context.javaToJS(cx, value, scope));
		}
	}

	public void test(String name, String script, String match) {
		try {
			val cx = (TestContext) factory.enterContext();
			val rootScope = cx.initStandardObjects();
			addToScope(cx, rootScope, "console", console);
			addToScope(cx, rootScope, "shared", shared);
			addToScope(cx, rootScope, "EventBus", new EventBus(console));
			cx.testName = name;
			cx.evaluateString(rootScope, script, testName + "/" + name, 1, null);
		} catch (Exception ex) {
			ex.printStackTrace();
			console.info("Error: " + ex.getMessage());
			// ex.printStackTrace();
		}

		Assertions.assertEquals(match.trim(), console.getConsoleOutput().trim());
	}
}
