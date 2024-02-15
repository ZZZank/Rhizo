package dev.latvian.mods.rhino.mod.core.mixin.common;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

@Mixin(CompoundTag.class)
public class CompoundTagMixin {

    @Shadow
	@Final
	private Map<String, Tag> tags;

    public Map<String, Tag> getTagsRaw() {
        return tags;
    }
}
