package dev.latvian.mods.rhino.util;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class Possible<T> {

	private final @Nullable Object value;
	Possible(Object value) {
		this.value = value;
	}
	Object value() {
		return this.value;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Possible) {
			Possible other = (Possible) obj;
			return this.value.equals(other.value);
		}
		return false;
	}
	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	public static final Possible<?> EMPTY = new Possible<>(null);
	public static final Possible<?> NULL = new Possible<>(null);

	public static <T> Possible<T> of(@Nullable T o) {
		return o == null ? (Possible<T>) NULL : new Possible<>(o);
	}


	public static <T> Possible<T> absent() {
		return (Possible<T>) EMPTY;
	}

	public boolean isSet() {
		return this != EMPTY;
	}

	public boolean isEmpty() {
		return this == EMPTY;
	}

	@Override
	public String toString() {
		return this == EMPTY ? "EMPTY" : String.valueOf(value);
	}

	public <C> Possible<C> cast(Class<C> type) {
		return (Possible<C>) this;
	}
}
