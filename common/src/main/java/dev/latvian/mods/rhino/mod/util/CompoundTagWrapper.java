package dev.latvian.mods.rhino.mod.util;

import java.util.Objects;

import dev.latvian.mods.rhino.NativeJavaMap;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.SharedContextData;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class CompoundTagWrapper implements CustomJavaToJsWrapper {

    private final CompoundTag tag;
    public CompoundTagWrapper(CompoundTag tag) {
        this.tag = tag;
    }
    public CompoundTag tag() {
        return this.tag;
    }
    @Override
    public String toString() {
        return String.format("CompoundTagWrapper {%s}", tag);
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CompoundTagWrapper)) {
            return false;
        }
        CompoundTagWrapper other = (CompoundTagWrapper) obj;
        return
            this.tag == other.tag;
    }
    @Override
    public int hashCode() {
        return Objects.hash(tag);
    }
	@Override
	public Scriptable convertJavaToJs(SharedContextData data, Scriptable scope, Class<?> staticType) {
		return new NativeJavaMap(data, scope, tag, NBTUtils.accessTagMap(tag), Tag.class, NBTUtils.VALUE_UNWRAPPER);
	}
}
