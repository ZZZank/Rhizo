package dev.latvian.mods.rhino.util;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

public record CustomJavaToJsWrapperProviderHolder<T>(Predicate<T> predicate, CustomJavaToJsWrapperProvider<T> provider) {
	public static class PredicateFromClass<T> implements Predicate<T> {
		private final Class<T> type;
		public PredicateFromClass(Class<T> type){
			this.type = type;
		}
		public Class<T> type() {
			return type;
		}

		@Override
		public String toString() {
			return String.format("MemberDef {%s}",type);
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof PredicateFromClass) {
				PredicateFromClass other = (PredicateFromClass) obj;
				return type.equals(other.type);
			}
			return false;
		}
		@Override
		public int hashCode() {
			return Objects.hash(type);
		}

		@Override
		public boolean test(T object) {
			return type.isAssignableFrom(object.getClass());
		}
	}

	@Nullable
	public CustomJavaToJsWrapperProvider<T> create(T object) {
		if (predicate.test(object)) {
			return provider;
		}

		return null;
	}
}
