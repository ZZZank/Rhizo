package dev.latvian.mods.rhino.mod.util;

import java.util.Map;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class WrappedCompoundTag extends CompoundTag {
    protected WrappedCompoundTag(Map<String, Tag> map) {
        super(map);
    }  
	public static WrappedCompoundTag ofWrapped(CompoundTag tag) {
		return (WrappedCompoundTag) tag;
	}
	public Map<String, Tag> entries() {
		return super.entries();
	}
}