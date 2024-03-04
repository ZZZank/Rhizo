package dev.latvian.mods.rhino.util;

import java.lang.reflect.Member;

public class FallbackRemapper implements Remapper {
    private final Remapper main;
    private final Remapper fallback;
    public FallbackRemapper(Remapper main, Remapper fallback) {
        this.main = main;
        this.fallback = fallback;
    }
    public Remapper main() {
        return this.main;
    }
    public Remapper fallback() {
        return this.fallback;
    }
    @Override
    public String toString() {
        return String.format("FallbackRemapper {main = %s, fallback = %s}", main, fallback);
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FallbackRemapper)) {
            return false;
        }
        FallbackRemapper other = (FallbackRemapper) obj;
        return
            this.main.equals(other.main) &&
            this.fallback.equals(other.fallback);
    }
    @Override
    public int hashCode() {
        return java.util.Objects.hash(main, fallback);
    }
	@Override
	public String remap(Class<?> from, Member member) {
		String s = main.remap(from, member);
		return s.isEmpty() ? fallback.remap(from, member) : s;
	}
}
