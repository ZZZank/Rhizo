package dev.latvian.mods.rhino.mod.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.util.ValueUnwrapper;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.EncoderException;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.nbt.TagType;
import net.minecraft.nbt.TagTypes;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public interface NBTUtils {
	
	ValueUnwrapper VALUE_UNWRAPPER = (contextData, scope, value) -> value instanceof Tag ? fromTag((Tag) value) : value;

	@Nullable
	static Object fromTag(@Nullable Tag t) {
		if (t == null || t == EndTag.INSTANCE) {
			return null;
		} else if (t instanceof StringTag) {
			return t.getAsString();
		} else if (t instanceof NumericTag) {
		    NumericTag num = (NumericTag) t;
			return num.getAsNumber();
		}

		return t;
	}

	@Nullable
	static Tag toTag(@Nullable Object v) {
		if (v == null || v instanceof EndTag) {
			return null;
		} else if (v instanceof Tag) {
		    Tag tag = (Tag) v;
			return tag;
		} else if (v instanceof NBTSerializable) {
		    NBTSerializable s = (NBTSerializable) v;
			return s.toNBT();
		} else if (v instanceof CharSequence || v instanceof Character) {
			return StringTag.valueOf(v.toString());
		} else if (v instanceof Boolean) {
		    Boolean b = (Boolean) v;
			return ByteTag.valueOf(b);
		} else if (v instanceof Number) {
		    Number number = (Number) v;
			if (number instanceof Byte) {
				return ByteTag.valueOf(number.byteValue());
			} else if (number instanceof Short) {
				return ShortTag.valueOf(number.shortValue());
			} else if (number instanceof Integer) {
				return IntTag.valueOf(number.intValue());
			} else if (number instanceof Long) {
				return LongTag.valueOf(number.longValue());
			} else if (number instanceof Float) {
				return FloatTag.valueOf(number.floatValue());
			}

			return DoubleTag.valueOf(number.doubleValue());
		} else if (v instanceof JsonPrimitive) {
		    JsonPrimitive json = (JsonPrimitive) v;
			if (json.isNumber()) {
				return toTag(json.getAsNumber());
			} else if (json.isBoolean()) {
				return ByteTag.valueOf(json.getAsBoolean());
			} else {
				return StringTag.valueOf(json.getAsString());
			}
		} else if (v instanceof Map<?, ?>) {
		    Map<?, ?> map = (Map<?, ?>) v;
			CompoundTag tag = new OrderedCompoundTag();

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				Tag nbt1 = toTag(entry.getValue());

				if (nbt1 != null) {
					tag.put(String.valueOf(entry.getKey()), nbt1);
				}
			}

			return tag;
		} else if (v instanceof JsonObject) {
		    JsonObject json = (JsonObject) v;
			CompoundTag tag = new OrderedCompoundTag();

			for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
				Tag nbt1 = toTag(entry.getValue());

				if (nbt1 != null) {
					tag.put(entry.getKey(), nbt1);
				}
			}

			return tag;
		} else if (v instanceof Collection<?>) {
		    Collection<?> c = (Collection<?>) v;
			return toTagCollection(c);
		} else if (v instanceof JsonArray) {
		    JsonArray array = (JsonArray) v;
			List<Tag> list = new ArrayList<>(array.size());

			for (JsonElement element : array) {
				list.add(toTag(element));
			}

			return toTagCollection(list);
		}

		return null;
	}

	static boolean isTagCompound(Object o) {
		return o == null || Undefined.isUndefined(o) || o instanceof CompoundTag || o instanceof CharSequence || o instanceof Map || o instanceof JsonElement;
	}

	@Nullable
	static CompoundTag toTagCompound(@Nullable Object v) {
		if (v instanceof CompoundTag) {
		    CompoundTag nbt = (CompoundTag) v;
			return nbt;
		} else if (v instanceof CharSequence) {
			try {
				return TagParser.parseTag(v.toString());
			} catch (Exception ex) {
				return null;
			}
		} else if (v instanceof JsonPrimitive) {
		    JsonPrimitive json = (JsonPrimitive) v;
			try {
				return TagParser.parseTag(json.getAsString());
			} catch (Exception ex) {
				return null;
			}
		} else if (v instanceof JsonObject) {
		    JsonObject json = (JsonObject) v;
			try {
				return TagParser.parseTag(json.toString());
			} catch (Exception ex) {
				return null;
			}
		}

		Tag tag = toTag(v);
		return tag instanceof CompoundTag ? (CompoundTag) tag : null;
	}

	static boolean isTagCollection(Object o) {
		return o == null || Undefined.isUndefined(o) || o instanceof CharSequence || o instanceof Collection<?> || o instanceof JsonArray;
	}

	@Nullable
	static CollectionTag<?> toTagCollection(@Nullable Object v) {
		if (v instanceof CollectionTag) {
		    CollectionTag tag = (CollectionTag) v;
			return tag;
		} else if (v instanceof CharSequence) {
			try {
				return (CollectionTag<?>) TagParser.parseTag("{a:" + v + "}").get("a");
			} catch (Exception ex) {
				return null;
			}
		} else if (v instanceof JsonArray) {
		    JsonArray array = (JsonArray) v;
			List<Tag> list = new ArrayList<>(array.size());

			for (JsonElement element : array) {
				list.add(toTag(element));
			}

			return toTagCollection(list);
		}

		return v == null ? null : toTagCollection((Collection<?>) v);
	}

	@Nullable
	static ListTag toTagList(@Nullable Object list) {
		return (ListTag) toTagCollection(list);
	}

	static CollectionTag<?> toTagCollection(Collection<?> c) {
		if (c.isEmpty()) {
			return new ListTag();
		}

		Tag[] values = new Tag[c.size()];
		int s = 0;
		byte commmonId = -1;

		for (Object o : c) {
			values[s] = toTag(o);

			if (values[s] != null) {
				if (commmonId == -1) {
					commmonId = values[s].getId();
				} else if (commmonId != values[s].getId()) {
					commmonId = 0;
				}

				s++;
			}
		}

		if (commmonId == NbtType.INT) {
			int[] array = new int[s];

			for (int i = 0; i < s; i++) {
				array[i] = ((NumericTag) values[i]).getAsInt();
			}

			return new IntArrayTag(array);
		} else if (commmonId == NbtType.BYTE) {
			byte[] array = new byte[s];

			for (int i = 0; i < s; i++) {
				array[i] = ((NumericTag) values[i]).getAsByte();
			}

			return new ByteArrayTag(array);
		} else if (commmonId == NbtType.LONG) {
			long[] array = new long[s];

			for (int i = 0; i < s; i++) {
				array[i] = ((NumericTag) values[i]).getAsLong();
			}

			return new LongArrayTag(array);
		} else if (commmonId == 0 || commmonId == -1) {
			return new ListTag();
		}

		ListTag nbt = new ListTag();

		for (Tag nbt1 : values) {
			if (nbt1 == null) {
				return nbt;
			}

			nbt.add(nbt1);
		}

		return nbt;
	}

	static Tag compoundTag() {
		return new OrderedCompoundTag();
	}

	static Tag compoundTag(Map<?, ?> map) {
		OrderedCompoundTag tag = new OrderedCompoundTag();

		for (Entry<?, ?> entry : map.entrySet()) {
			Tag tag1 = toTag(entry.getValue());

			if (tag1 != null) {
				tag.put(String.valueOf(entry.getKey()), tag1);
			}
		}

		return tag;
	}

	static Tag listTag() {
		return new ListTag();
	}

	static Tag listTag(List<?> list) {
		ListTag tag = new ListTag();

		for (Object v : list) {
			tag.add(toTag(v));
		}

		return tag;
	}

	static Tag byteTag(byte v) {
		return ByteTag.valueOf(v);
	}

	static Tag b(byte v) {
		return ByteTag.valueOf(v);
	}

	static Tag shortTag(short v) {
		return ShortTag.valueOf(v);
	}

	static Tag s(short v) {
		return ShortTag.valueOf(v);
	}

	static Tag intTag(int v) {
		return IntTag.valueOf(v);
	}

	static Tag i(int v) {
		return IntTag.valueOf(v);
	}

	static Tag longTag(long v) {
		return LongTag.valueOf(v);
	}

	static Tag l(long v) {
		return LongTag.valueOf(v);
	}

	static Tag floatTag(float v) {
		return FloatTag.valueOf(v);
	}

	static Tag f(float v) {
		return FloatTag.valueOf(v);
	}

	static Tag doubleTag(double v) {
		return DoubleTag.valueOf(v);
	}

	static Tag d(double v) {
		return DoubleTag.valueOf(v);
	}

	static Tag stringTag(String v) {
		return StringTag.valueOf(v);
	}

	static Tag intArrayTag(int[] v) {
		return new IntArrayTag(v);
	}

	static Tag ia(int[] v) {
		return new IntArrayTag(v);
	}

	static Tag longArrayTag(long[] v) {
		return new LongArrayTag(v);
	}

	static Tag la(long[] v) {
		return new LongArrayTag(v);
	}

	static Tag byteArrayTag(byte[] v) {
		return new ByteArrayTag(v);
	}

	static Tag ba(byte[] v) {
		return new ByteArrayTag(v);
	}

	static void quoteAndEscapeForJS(StringBuilder stringBuilder, String string) {
		int start = stringBuilder.length();
		stringBuilder.append(' ');
		char c = 0;

		for (int i = 0; i < string.length(); ++i) {
			char d = string.charAt(i);
			if (d == '\\') {
				stringBuilder.append('\\');
			} else if (d == '"' || d == '\'') {
				if (c == 0) {
					c = d == '\'' ? '"' : '\'';
				}

				if (c == d) {
					stringBuilder.append('\\');
				}
			}

			stringBuilder.append(d);
		}

		if (c == 0) {
			c = '\'';
		}

		stringBuilder.setCharAt(start, c);
		stringBuilder.append(c);
	}

	static TagType<?> convertType(TagType<?> tagType) {
		return tagType == CompoundTag.TYPE ? COMPOUND_TYPE : tagType == ListTag.TYPE ? LIST_TYPE : tagType;
	}

	@Nullable
	static OrderedCompoundTag read(FriendlyByteBuf buf) {
		int i = buf.readerIndex();
		byte b = buf.readByte();
		if (b == 0) {
			return null;
		} else {
			buf.readerIndex(i);

			try {
				DataInputStream stream = new DataInputStream(new ByteBufInputStream(buf));

				byte b1 = stream.readByte();
				if (b1 == 0) {
					stream.close();
					return null;
				} else {
					stream.readUTF();
					TagType<?> tagType = convertType(TagTypes.getType(b1));

					if (tagType != COMPOUND_TYPE) {
						stream.close();
						return null;
					}

					return COMPOUND_TYPE.load(stream, 0, NbtAccounter.UNLIMITED);
				}
			} catch (IOException var5) {
				throw new EncoderException(var5);
			}
		}
	}

	TagType<OrderedCompoundTag> COMPOUND_TYPE = new TagType<OrderedCompoundTag>() {
		@Override
		public OrderedCompoundTag load(DataInput dataInput, int i, NbtAccounter nbtAccounter) throws IOException {
			nbtAccounter.accountBits(384L);
			if (i > 512) {
				throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
			} else {
				Map<String, Tag> map = new LinkedHashMap<>();

				byte typeId;
				while ((typeId = dataInput.readByte()) != 0) {
					String key = dataInput.readUTF();
					nbtAccounter.accountBits(224L + 16L * key.length());
					TagType<?> valueType = convertType(TagTypes.getType(typeId));
					Tag value = valueType.load(dataInput, i + 1, nbtAccounter);

					if (map.put(key, value) != null) {
						nbtAccounter.accountBits(288L);
					}
				}

				return new OrderedCompoundTag(map);
			}
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

	static Map<String, Tag> accessTagMap(CompoundTag tag) {
		return WrappedCompoundTag.ofWrapped(tag).entries();
	}

	TagType<ListTag> LIST_TYPE = new TagType<ListTag>() {
		@Override
		public ListTag load(DataInput dataInput, int i, NbtAccounter nbtAccounter) throws IOException {
			nbtAccounter.accountBits(296L);
			if (i > 512) {
				throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
			} else {
				byte typeId = dataInput.readByte();
				int size = dataInput.readInt();
				if (typeId == 0 && size > 0) {
					throw new RuntimeException("Missing type on ListTag");
				} else {
					nbtAccounter.accountBits(32L * (long) size);
					TagType<?> valueType = convertType(TagTypes.getType(typeId));
					ListTag list = new ListTag();

					for (int k = 0; k < size; ++k) {
						list.add(valueType.load(dataInput, i + 1, nbtAccounter));
					}

					return list;
				}
			}
		}

		public String getName() {
			return "LIST";
		}

		public String getPrettyName() {
			return "TAG_List";
		}
	};

}