package dev.latvian.mods.rhino.test.impl.event;

/**
 * @author ZZZank
 */
public class TestEvent {
    public final String value = "TestEvent::value";

    public static class Example extends TestEvent {
        public final String value = "Example::value";
    }
}
