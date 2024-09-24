package dev.latvian.mods.rhino.natived;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * handling method overloads
 *
 * @author ZZZank
 */
public class JMethods {
    private final String name;
    private final JMethod[] all;

    public JMethods(String name, JMethod[] all) {
        this.name = name;
        this.all = all;
    }

    public String name() {
        return name;
    }

    public List<JMethod> getAll() {
        return Collections.unmodifiableList(Arrays.asList(all));
    }
}
