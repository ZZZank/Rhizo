package dev.latvian.mods.rhino.test.impl;

import dev.latvian.mods.rhino.test.impl.base.TestConsole;

import java.util.function.Consumer;

/**
 * @author ZZZank
 */
public class MethodOverloads {
    private final TestConsole console;

    public MethodOverloads(TestConsole console) {
        this.console = console;
    }

    public void f1(String s) {
        console.info("f1.string");
    }

    public void f1(Consumer<String> s) {
        console.info("f1.function");
    }
}
