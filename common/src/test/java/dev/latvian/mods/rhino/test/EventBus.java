package dev.latvian.mods.rhino.test;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.test.impl.EventPriority;
import dev.latvian.mods.rhino.test.impl.TestConsole;

import java.util.function.Consumer;

public record EventBus(TestConsole console) {
	public static class Event {
	}

	public static class TestEvent extends Event {
	}

	public <T extends Event> void addListener(
		Context cx,
		EventPriority priority,
		boolean receiveCanceled,
		Class<T> eventType,
		Consumer<T> consumer
	) throws Exception {
		console.info("Listening for " + eventType.getName());
		consumer.accept(eventType.newInstance());
	}

	public <T extends Event> void callback(Consumer<T> consumer) throws Exception {
		consumer.accept(null);
	}
}
