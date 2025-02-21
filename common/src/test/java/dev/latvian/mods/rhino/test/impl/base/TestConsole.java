package dev.latvian.mods.rhino.test.impl.base;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.ContextFactory;
import dev.latvian.mods.rhino.ScriptRuntime;
import dev.latvian.mods.rhino.test.impl.WithContext;
import dev.latvian.mods.rhino.test.impl.Holder;
import dev.latvian.mods.rhino.test.impl.TestMaterial;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import lombok.val;

import java.util.*;
import java.util.stream.Collectors;

@RemapPrefixForJS("test1$")
@RemapPrefixForJS("test2$")
public class TestConsole {
	private final ContextFactory factory;
	private TestConsoleTheme theme;
	public StringBuilder consoleOutput = new StringBuilder();

	public TestConsole(ContextFactory factory) {
		this.factory = factory;
	}

	public void log(Object... objects) {
		info(Arrays.stream(objects).map(ScriptRuntime::toString).collect(Collectors.joining(" ")));
	}

	public static String formatException(String errorMessage) {
		return "Error: " + errorMessage;
	}

	public void info(Object o) {
		val s = ScriptRuntime.toString(/*factory.enter(), */o);

		val builder = new StringBuilder();

		val lineP = new int[]{0};
		val lineS = Context.getSourcePositionFromStack(factory.enterContext(), lineP);

		if (lineP[0] > 0) {
			if (lineS != null) {
				builder.append(lineS);
			}

			builder.append(':');
			builder.append(lineP[0]);
			builder.append(": ");
		}

		builder.append(s);

		System.out.println(builder);

		if (consoleOutput.length() > 0) {
			consoleOutput.append('\n');
		}

		consoleOutput.append(s);
	}

	public String getConsoleOutput() {
		String s = consoleOutput.toString();
		consoleOutput.setLength(0);
		return s;
	}

	public void freeze(Object... objects) {
		System.out.println("Freezing " + Arrays.toString(objects));
	}

	public String[] getTestArray() {
		return new String[]{"abc", "def", "ghi"};
	}

	public List<String> getTestList() {
		return new ArrayList<>(Arrays.asList(getTestArray()));
	}

	public Map<String, String> getTestMap() {
		return Collections.singletonMap("test", "10.5");
	}

	public void test1$setTheme(TestConsoleTheme t) {
		info("Set theme to " + t);
		theme = t;
	}

	public TestConsoleTheme test2$getTheme() {
		return theme;
	}

	public void printMaterial(TestMaterial material) {
		info(String.format("%s#%08x", material.name(), material.hashCode()));
	}

	public void genericsArrayArg(WithContext<String>[] arg) {
		info("Generics array:");
		info(arg);
	}

	public void genericsListArg(List<WithContext<String>> arg) {
		info("Generics list:");
		info(arg);
	}

	public void genericsSetArg(Set<WithContext<String>> arg) {
		info("Generics set:");
		info(arg);
	}

	public void genericsMapArg(Map<WithContext<TestMaterial>, Integer> arg) {
		info("Generics map:");
		info(arg);
	}

	public void registerMaterial(Holder<TestMaterial> holder) {
		info("Registered material: " + holder.value().name());
	}
}
