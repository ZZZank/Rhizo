package dev.latvian.mods.rhino.test;

import dev.latvian.mods.rhino.test.impl.base.RhinoTest;
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
            event: 'class dev.latvian.mods.rhino.test.impl.event.TestEvent', priority: 'HIGH'
            TestEvent::value
            """);
    }

    @Test
    public void callback() {
        TEST.test("callback", """
			EventBus.callback((event) => { console.info('hi') })
			""", """
			hi
			""");
    }
}
