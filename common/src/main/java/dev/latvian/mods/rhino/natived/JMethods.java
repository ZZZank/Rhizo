package dev.latvian.mods.rhino.natived;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * handling method overloads
 *
 * @author ZZZank
 */
@AllArgsConstructor
public class JMethods {
    private final String name;
    private final JMethod[] all;

    public String name() {
        return name;
    }

    public List<JMethod> getAll() {
        return Collections.unmodifiableList(Arrays.asList(all));
    }
}
