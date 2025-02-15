package dev.latvian.mods.rhino.test.impl.event;

import com.github.bsideup.jabel.Desugar;
import dev.latvian.mods.rhino.test.impl.TestConsole;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Desugar
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
		listen(
			EventPriority.NORMAL,
			false,
			(Class) Class.forName(eventType),
			consumer
		);
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

	public static @NotNull String format(EventPriority priority, Class<?> eventType) {
		return String.format("event: '%s', priority: '%s'", eventType, priority);
	}

	public <T extends TestEvent> void callback(Consumer<T> consumer) throws Exception {
		consumer.accept(null);
	}
}
