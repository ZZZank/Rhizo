package dev.latvian.mods.rhino.mod.util;

import net.minecraft.nbt.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrderedCompoundTag extends CompoundTag {

	public static final TagType<OrderedCompoundTag> ORDERED_TYPE;

    public final Map<String, Tag> tagMap;

	public OrderedCompoundTag(Map<String, Tag> map) {
		super(map);
		tagMap = map;
	}

	public OrderedCompoundTag() {
		this(new LinkedHashMap<>());
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		for (Map.Entry<String, Tag> entry : tagMap.entrySet()) {
			Tag tag = entry.getValue();
			dataOutput.writeByte(tag.getId());

			if (tag.getId() != 0) {
				dataOutput.writeUTF(entry.getKey());
				tag.write(dataOutput);
			}
		}

		dataOutput.writeByte(0);
	}

	static {
		ORDERED_TYPE = new TagType<>() {
			@Override
			public OrderedCompoundTag load(DataInput dataInput, int i, NbtAccounter nbtAccounter) throws IOException {
				nbtAccounter.accountBits(384L);
				if (i > 512) {
					throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
				}
				Map<String, Tag> map = new LinkedHashMap<>();

				byte b;
				while ((b = dataInput.readByte()) != 0) {
					String string = dataInput.readUTF();
					nbtAccounter.accountBits(224L + 16L * string.length());
					TagType<?> tagType = NBTUtils.convertType(TagTypes.getType(b));
					Tag tag = tagType.load(dataInput, i + 1, nbtAccounter);

					if (map.put(string, tag) != null) {
						nbtAccounter.accountBits(288L);
					}
				}

				return new OrderedCompoundTag(map);
			}

			@Override
			public String getName() {
				return "COMPOUND";
			}

			@Override
			public String getPrettyName() {
				return "TAG_Compound";
			}
		};
	}
}
