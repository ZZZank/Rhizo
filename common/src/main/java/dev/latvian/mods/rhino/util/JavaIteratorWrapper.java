package dev.latvian.mods.rhino.util;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.IdEnumerationIterator;
import dev.latvian.mods.rhino.JavaScriptException;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

public class JavaIteratorWrapper implements IdEnumerationIterator {
	private final Iterator<?> parent;
	public JavaIteratorWrapper(Iterator<?> parent) {
		this.parent = parent;
	}
	public Iterator<?> parent() {
		return this.parent;
	}
	@Override
	public String toString() {
		return String.format("MemberDef {%s}", parent);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof JavaIteratorWrapper) {
			JavaIteratorWrapper other = (JavaIteratorWrapper) obj;
			return parent.equals(other.parent);
		}
		return false;
	}
	@Override
	public int hashCode() {
		return Objects.hash(parent);
	}

	@Override
	public boolean enumerationIteratorHasNext(Context cx, Consumer<Object> callback) {
		if (parent.hasNext()) {
			callback.accept(parent.next());
			return true;
		}

		return false;
	}

	@Override
	public boolean enumerationIteratorNext(Context cx, Consumer<Object> callback) throws JavaScriptException {
		if (parent.hasNext()) {
			callback.accept(parent.next());
			return true;
		}

		return false;
	}
}
