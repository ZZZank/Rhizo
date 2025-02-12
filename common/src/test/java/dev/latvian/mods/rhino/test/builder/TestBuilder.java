package dev.latvian.mods.rhino.test.builder;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.ContextFactory;
import dev.latvian.mods.rhino.Scriptable;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author ZZZank
 */
@Setter
@Accessors(chain = true)
public class TestBuilder {

    @NotNull
    public String script;
    @NotNull
    public String name = "test";
    public int lineNumber = 0;

    @NotNull
    public Supplier<ContextFactory> factory = ContextFactory::new;
    @Nullable
    public Consumer<ContextFactory> factoryModifier;
    @Nullable
    public Object securityDomain;

    @NotNull
    public Function<ContextFactory, Context> context = ContextFactory::enterContext;
    public final List<ContextModifier> contextModifiers = new ArrayList<>();

    @NotNull
    public Function<Context, Scriptable> scope = Context::initStandardObjects;
    @Nullable
    public BiConsumer<Context, Scriptable> scopeModifier;

    public static TestBuilder of() {
        return new TestBuilder();
    }

    public static TestBuilder script(String script) {
        return new TestBuilder().setScript(script);
    }

    public void run() {
        val factory = this.factory.get();
        if (factoryModifier != null) {
            factoryModifier.accept(factory);
        }

        val context = this.context.apply(factory);
        if (contextModifiers != null) {
            for (val contextModifier : contextModifiers) {
                contextModifier.accept(factory, context);
            }
        }

        val scope = this.scope.apply(context);
        if (scopeModifier != null) {
            scopeModifier.accept(context, scope);
        }

        context.evaluateString(scope, script, name, lineNumber, securityDomain);
    }

    public void addContextModifier(@Nullable ContextModifier... contextModifiers) {
        this.contextModifiers.addAll(Arrays.asList(contextModifiers));
    }
}
