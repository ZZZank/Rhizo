package dev.latvian.mods.rhino.mod.util;

import net.minecraft.nbt.Tag;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface NBTSerializable {
	Tag toNBT();
}