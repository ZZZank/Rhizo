package dev.latvian.mods.rhino.test;

import org.junit.jupiter.api.Test;

/**
 * @author ZZZank
 */
public class EventTest {
    private static final RhinoTest TEST = new RhinoTest("event_bus");

    @Test
    void simple() {
        TEST.test("simple", """
            EventBus.listen("dev.latvian.mods.rhino.test.impl.event.TestEvent", (e) => {
                console.log(e.value)
            })
            """, """
            event: 'class dev.latvian.mods.rhino.test.impl.event.TestEvent', priority: 'NORMAL'
            TestEvent::value""");
    }

    @Test
    void full() {
        TEST.test("full", """
            EventBus.listen(
                "higH",
                false,
                "dev.latvian.mods.rhino.test.impl.event.TestEvent", (e) => {
                console.log(e.value)
            })
            """, """
            """);
    }
}
