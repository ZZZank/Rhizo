package dev.latvian.mods.rhino.mod.core.mixin.common;

import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.nbt.NumericTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NumericTag.class)
public abstract class NumericTagMixin implements SpecialEquality {
	@Shadow
	public abstract byte getAsByte();

	@Shadow
	public abstract double getAsDouble();

	@Override
	public boolean specialEquals(Object o, boolean shallow) {
		if (o instanceof Boolean) {
		    Boolean b = (Boolean) o;
			return b == (getAsByte() != 0);
		} else if (o instanceof Number) {
     		Number n1 = (Number) o;
			return getAsDouble() == n1.doubleValue();
		} else if (!shallow && o instanceof NumericTag) {
     		NumericTag n1 = (NumericTag) o;
			return getAsDouble() == n1.getAsDouble();
		}

		return equals(o);
	}
}
