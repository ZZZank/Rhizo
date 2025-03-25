package dev.latvian.mods.rhino.test.impl.event;

import dev.latvian.mods.rhino.test.impl.base.TestConsole;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record EventBus(TestConsole console) {

	public <T extends TestEvent> void listen(
		Class<T> eventType,
		Consumer<T> consumer
	) throws Exception {
		listen(EventPriority.NORMAL, false, eventType, consumer);
	}

	public void listen(
		String eventType,
		Consumer<? extends TestEvent> consumer
	) throws Exception {
		listen(loadClass(eventType), consumer);
	}

	public <T extends TestEvent> void listen(
		EventPriority priority,
		boolean receiveCanceled,
		Class<T> eventType,
		Consumer<T> consumer
	) throws Exception {
		console.info(format(priority, eventType));
		consumer.accept(eventType.newInstance());
	}

	public void listen(
		EventPriority priority,
		boolean receiveCanceled,
		String eventType,
		Consumer<? extends TestEvent> consumer
	) throws Exception {
		listen(priority, receiveCanceled, loadClass(eventType), consumer);
	}

	public static @NotNull String format(EventPriority priority, Class<?> eventType) {
		return String.format("event: '%s', priority: '%s'", eventType, priority);
	}

	private static <T> Class<T> loadClass(String name) {
        try {
            return (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

	public <T extends TestEvent> void callback(Consumer<T> consumer) throws Exception {
		consumer.accept(null);
	}
}
