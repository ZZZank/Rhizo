package dev.latvian.mods.rhino.mod.util;

import java.util.Objects;

import dev.latvian.mods.rhino.NativeJavaList;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.SharedContextData;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.Tag;

public class CollectionTagWrapper implements CustomJavaToJsWrapper {

    private final CollectionTag<?> tag;
    public CollectionTagWrapper(CollectionTag<?> tag) {
        this.tag = tag;
    }
    public CollectionTag<?> tag() {
        return this.tag;
    }
    @Override
    public String toString() {
        return String.format("CollectionTagWrapper {%s}", tag);
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CollectionTagWrapper)) {
            return false;
        }
        CollectionTagWrapper other = (CollectionTagWrapper) obj;
        return
            this.tag == other.tag;
    }
    @Override
    public int hashCode() {
        return Objects.hash(tag);
    }
	@Override
	public Scriptable convertJavaToJs(SharedContextData data, Scriptable scope, Class<?> staticType) {
		return new NativeJavaList(data, scope, tag, tag, Tag.class, NBTUtils.VALUE_UNWRAPPER);
	}
}
