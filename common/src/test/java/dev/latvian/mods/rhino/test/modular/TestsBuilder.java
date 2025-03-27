package dev.latvian.mods.rhino.test.modular;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.ContextFactory;
import dev.latvian.mods.rhino.Scriptable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author ZZZank
 */
public class TestsBuilder {
    /* translate TestBuilder field to TestsBuilder field
    from: public ([\w<>,]+) (\w+) = (.+);
    to: public List<$1> $2Choice = ofList($3);
     */

    public List<Supplier<ContextFactory>> factoryProviderChoice = ofList(ContextFactory::new);
    public List<List<ContextFactoryModifier>> factoryModifiersChoice = ofList(new ArrayList<>());
    public List<Function<ContextFactory, Context>> contextProviderChoice = ofList(ContextFactory::enterContext);
    public List<List<ContextModifier>> contextModifiersChoice = ofList(new ArrayList<>());
    public List<Function<Context, Scriptable>> scopeProviderChoice = ofList(Context::initStandardObjects);
    public List<List<ScopeModifier>> scopeModifiersChoice = ofList(new ArrayList<>());
    public List<ResultValidator> resultValidatorChoice = ofList(ResultValidator.noError());

    public String testName = "test";
    public String script = null;
    public int lineNo = 1;
    public List<Object> securityDomainChoice = ofList(null);

    public Stream<TestBuilder> getBuilders() {
        return descartesArr(
            mapList(factoryProviderChoice, fp -> modifier(b -> b.factoryProvider = fp)),
            mapList(factoryModifiersChoice, fm -> modifier(b -> b.factoryModifiers = fm)),
            mapList(contextProviderChoice, cp -> modifier(b -> b.contextProvider = cp)),
            mapList(contextModifiersChoice, cm -> modifier(b -> b.contextModifiers = cm)),
            mapList(scopeProviderChoice, sp -> modifier(b -> b.scopeProvider = sp)),
            mapList(scopeModifiersChoice, sm -> modifier(b -> b.scopeModifiers = sm)),
            mapList(resultValidatorChoice, rv -> modifier(b -> b.resultValidator = rv)),
            List.of(modifier(b -> b.testName = testName)),
            List.of(modifier(b -> b.script = script)),
            List.of(modifier(b -> b.lineNo = lineNo)),
            mapList(securityDomainChoice, sd -> modifier(b -> b.securityDomain = sd))
        )
            .map(modifiers -> {
                var testBuilder = new TestBuilder();
                for (var modifier : modifiers) {
                    modifier.accept(testBuilder);
                }
                return testBuilder;
            });
    }

    public void test() {
        // convert to list before testing to simplify stack trace
        for (TestBuilder builder : getBuilders().toList()) {
            builder.test();
        }
    }

    private static Consumer<TestBuilder> modifier(Consumer<TestBuilder> modifier) {
        return modifier;
    }

    private static <I, O> List<O> mapList(Collection<I> inputs, Function<I, O> mapper) {
        var result = new ArrayList<O>(inputs.size());
        for (I input : inputs) {
            result.add(mapper.apply(input));
        }
        return result;
    }

    private static <T> List<T> ofList(T element) {
        return new ArrayList<>(Collections.singletonList(element));
    }

    @SafeVarargs
    public static <T> Stream<Collection<T>> descartesArr(Collection<T>... layers) {
        return descartes(Arrays.asList(layers));
    }

    @SafeVarargs
    public static <T> List<Collection<T>> descartesArr2List(Collection<T>... layers) {
        return descartesArr(layers).toList();
    }

    /**
     * example input: [[1, 2, 3], [4, 5, 6]]
     * example output: [[1, 4], [1, 5], [1, 6], [2, 4], [2, 5], [2, 6], [3, 4], [3, 5], [3, 6]]
     */
    public static <T> Stream<Collection<T>> descartes(Iterable<? extends Collection<T>> layers) {
        if (!layers.iterator().hasNext()) {
            return Stream.empty();
        }

        var result = Stream.<Collection<T>>of(List.of());
        for (var layer : layers) {
            result = result.flatMap(last -> layer.stream()
                .map(t -> copyAppend(last, t)));
        }
        return result;
    }

    private static <T> List<T> copyAppend(Collection<T> list, T element) {
        var merged = new ArrayList<T>(list.size() + 1);
        merged.addAll(list);
        merged.add(element);
        return merged;
    }
}
