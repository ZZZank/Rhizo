package dev.latvian.mods.rhino.test;

import dev.latvian.mods.rhino.*;
import dev.latvian.mods.rhino.test.impl.*;
import dev.latvian.mods.rhino.test.impl.event.EventBus;
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
		typeWrappers.register(WithContext.class, WithContext::of);
		typeWrappers.register(Holder.class, Holder::of);
	}

	public static void addToScope(Context cx, Scriptable scope, String name, Object value) {
		if (value instanceof Class<?> c) {
			ScriptableObject.putProperty(scope, name, new NativeJavaClass(cx, scope, c));
		} else {
			ScriptableObject.putProperty(scope, name, Context.javaToJS(cx, value, scope));
		}
	}

	public void test(String name, String script, String match) {
		test(name, script, match, false);
		test(name, script, match, true);
	}

	private void test(String name, String script, String match, boolean compile) {
		try {
			val cx = (TestContext) factory.enterContext();
			val rootScope = cx.initStandardObjects();
			addToScope(cx, rootScope, "console", console);
			addToScope(cx, rootScope, "shared", shared);
			addToScope(cx, rootScope, "EventBus", new EventBus(console));
			cx.testName = name;
			if (compile) {
				cx.setOptimizationLevel(9);
			}
			cx.evaluateString(rootScope, script, testName + "/" + name, 1, null);
		} catch (Exception ex) {
			ex.printStackTrace();
			console.info(String.format("Error(compile=%s): %s", compile, ex.getMessage()));
		} finally {
			Context.exit();
		}

		Assertions.assertEquals(match.trim(), console.getConsoleOutput().trim());
	}
}
