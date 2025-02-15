package dev.latvian.mods.rhino.native_java;

import dev.latvian.mods.rhino.*;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Iterator;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class NativeJavaIterator extends ES6Iterator {
	public static final String TAG = "NativeJavaIterator";

	public static void init(ScriptableObject scope, boolean sealed) {
		init(scope, sealed, new NativeJavaIterator(), TAG);
	}

	private final Iterator<?> raw;

	public static Callable ofGetter(Iterator<?> raw) {
		return (cx, scope, thiz, args) -> new NativeJavaIterator(scope, raw);
	}

	private NativeJavaIterator() {
		raw = null;
	}

    public NativeJavaIterator(Scriptable scope, Iterator<?> raw) {
		super(scope, TAG);
        this.raw = raw;
    }

    public Iterator<?> parent() {
        return raw;
    }

	@Override
	protected boolean isDone(Context cx, Scriptable scope) {
		return !raw.hasNext();
	}

	@Override
	protected Object nextValue(Context cx, Scriptable scope) {
		return raw.next();
	}

	@Override
	public String getClassName() {
		return "JavaIteratorWrapper";
	}
}