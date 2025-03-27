package dev.latvian.mods.rhino.test;

import dev.latvian.mods.rhino.ContextFactory;
import dev.latvian.mods.rhino.test.impl.base.TestContextFactory;
import dev.latvian.mods.rhino.test.modular.TestsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author ZZZank
 */
public class TestsBuilderTest {

    @Test
    public void tes() {
        var arg1 = List.of("yes", "no", "3rd");
        var arg2 = List.<Object>of("sec1", 234.5, false);
        var testsBuilder = new TestsBuilder();
        testsBuilder.factoryProviderChoice = arg1.stream()
            .map(s -> (Supplier<ContextFactory>) () -> new TestContextFactory(s))
            .toList();
        testsBuilder.securityDomainChoice = arg2;

        var expected = TestsBuilder.descartes(List.of(List.copyOf(arg1), arg2))
            .map(l -> {
                var iter = l.iterator();
                var first = iter.next().toString();
                return first + ':' + iter.next();
            })
            .collect(Collectors.toSet());

        var result = testsBuilder.getBuilders()
            .map(b -> b.factoryProvider.get().toString() + ':' + b.securityDomain)
            .collect(Collectors.toSet());

        print(result);
        Assertions.assertEquals(expected, result);
    }

    public static void print(Object o) {
        System.out.println(o);
    }
}
