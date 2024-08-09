package dev.latvian.mods.rhino.mod.wrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * @author LatvianModder
 */
public interface AABBWrapper {
	AABB EMPTY = new AABB(0D, 0D, 0D, 0D, 0D, 0D);
	AABB CUBE = new AABB(0D, 0D, 0D, 1D, 1D, 1D);

	static AABB of(double x0, double y0, double z0, double x1, double y1, double z1) {
		return new AABB(x0, y0, z0, x1, y1, z1);
	}

	static AABB ofBlocks(BlockPos pos1, BlockPos pos2) {
		return of(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX() + 1D, pos2.getY() + 1D, pos2.getZ() + 1D);
	}

	static AABB ofBlocksStrict(BlockPos start, BlockPos end) {
		return new AABB(start, end);
	}

	static AABB ofBounding(BoundingBox box) {
		return AABB.of(box);
	}

	static AABB ofBlock(BlockPos pos) {
		return new AABB(pos);
	}

	static AABB ofSize(double x, double y, double z) {
		return AABB.ofSize(x, y, z);
	}

	static AABB ofVec(Vec3 start, Vec3 end) {
		return new AABB(start, end);
	}

	static AABB wrap(Object o) {
        if (o instanceof AABB aabb) {
            return aabb;
        } else if (o instanceof BlockPos blockPos) {
            return ofBlock(blockPos);
        } else if (o instanceof BoundingBox box) {
            return AABB.of(box);
        } else if (o instanceof double[] d) {
            return switch (d.length) {
                case 3 -> ofSize(d[0], d[1], d[2]);
                case 6 -> of(d[0], d[1], d[2], d[3], d[4], d[5]);
                default -> EMPTY;
            };
        } else if (o instanceof List<?> l) {
            return switch (l.size()) {
                case 3 ->
                    l.get(0) instanceof Number n0 && l.get(1) instanceof Number n1 && l.get(2) instanceof Number n2
                        ? ofSize(n0.doubleValue(), n1.doubleValue(), n2.doubleValue())
                        : EMPTY;
                case 6 ->
                    l.get(0) instanceof Number n0 && l.get(1) instanceof Number n1 && l.get(2) instanceof Number n2
                        && l.get(3) instanceof Number n3 && l.get(4) instanceof Number n4
                        && l.get(5) instanceof Number n5 ? of(
                        n0.doubleValue(),
                        n1.doubleValue(),
                        n2.doubleValue(),
                        n3.doubleValue(),
                        n4.doubleValue(),
                        n5.doubleValue()
                    ) : EMPTY;
                default -> EMPTY;
            };
        }
        return EMPTY;
    }
}