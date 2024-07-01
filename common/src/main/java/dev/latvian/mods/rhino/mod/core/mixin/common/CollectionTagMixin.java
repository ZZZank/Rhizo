package dev.latvian.mods.rhino.mod.core.mixin.common;

import dev.latvian.mods.rhino.mod.util.NBTWrapper;
import dev.latvian.mods.rhino.util.ListLike;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CollectionTag.class)
public abstract class CollectionTagMixin implements ListLike<Object> {

	private CollectionTag rhizo$self() {
		return (CollectionTag) (Object) this;
	}

	@Nullable
	@Override
	public Object getLL(int index) {
		return NBTWrapper.fromTag((Tag) rhizo$self().get(index));
	}

	@Override
	public void setLL(int index, Object value) {
		rhizo$self().set(index, NBTWrapper.toTag(value));
	}

	@Override
	public int sizeLL() {
		return rhizo$self().size();
	}

	@Override
	public void removeLL(int index) {
		rhizo$self().remove(index);
	}

	@Override
	public void clearLL() {
		rhizo$self().clear();
	}
}
