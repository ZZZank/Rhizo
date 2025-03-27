package dev.latvian.mods.rhino.test.modular;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.ContextFactory;
import dev.latvian.mods.rhino.Scriptable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author ZZZank
 */
public class TestBuilder {
    public static TestBuilder make(Consumer<TestBuilder> modifier) {
        var testBuilder = new TestBuilder();
        modifier.accept(testBuilder);
        return testBuilder;
    }

    public Supplier<ContextFactory> factoryProvider = ContextFactory::new;
    public List<ContextFactoryModifier> factoryModifiers = new ArrayList<>();
    public Function<ContextFactory, Context> contextProvider = ContextFactory::enterContext;
    public List<ContextModifier> contextModifiers = new ArrayList<>();
    public Function<Context, Scriptable> scopeProvider = Context::initStandardObjects;
    public List<ScopeModifier> scopeModifiers = new ArrayList<>();
    public ResultValidator resultValidator = ResultValidator.noError();

    public String testName = "test";
    public String script = null;
    public int lineNo = 1;
    public Object securityDomain = null;

    public void test() {
        var snapshot = new SnapshotImpl();

        snapshot.factory = factoryProvider.get();
        snapshot.accept(factoryModifiers);

        snapshot.context = contextProvider.apply(snapshot.factory);
        snapshot.accept(contextModifiers);

        snapshot.scope = scopeProvider.apply(snapshot.context);
        snapshot.accept(scopeModifiers);

        try {
            snapshot.result = snapshot.context.evaluateString(
                snapshot.scope,
                script,
                testName,
                lineNo,
                securityDomain
            );
            snapshot.error = null;
        } catch (Exception e) {
            snapshot.result = null;
            snapshot.error = e;
        }
        resultValidator.modify(snapshot);
    }

    private static class SnapshotImpl implements Snapshot {
        ContextFactory factory;
        Context context;
        Scriptable scope;
        Object result;
        Exception error;

        @Override
        public ContextFactory factory() {
            return factory;
        }

        @Override
        public Context context() {
            return context;
        }

        @Override
        public Scriptable scope() {
            return scope;
        }

        @Override
        public Object result() {
            return result;
        }

        @Override
        public Exception error() {
            return error;
        }
    }
}
