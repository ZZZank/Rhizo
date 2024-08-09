package dev.latvian.mods.rhino.mod.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.rhino.util.HideFromJS;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.EncoderException;
import lombok.val;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author LatvianModder
 */
public class NBTUtils {

	@Nullable
	public static Tag toNBT(@Nullable Object o) {
		//already resolved
		if (o == null) {
			return null;
		} else if (o instanceof Tag tag) {
			return tag;
		} else if (o instanceof NBTSerializable serializable) {
			//this includes two tag wrappers
			return serializable.toNBT();
		}
		//primitive
		else if (o instanceof CharSequence || o instanceof Character) {
			return StringTag.valueOf(o.toString());
		} else if (o instanceof Boolean b) {
			return ByteTag.valueOf(b);
		} else if (o instanceof Number number) {
            if (number instanceof Byte b) {
                return ByteTag.valueOf(b);
            } else if (number instanceof Short i) {
                return ShortTag.valueOf(i);
            } else if (number instanceof Integer i) {
                return IntTag.valueOf(i);
            } else if (number instanceof Long l) {
                return LongTag.valueOf(l);
            } else if (number instanceof Float v) {
                return FloatTag.valueOf(v);
            }
            return DoubleTag.valueOf(number.doubleValue());
        }
		//native json
		else if (o instanceof JsonPrimitive json) {
			if (json.isNumber()) {
				return toNBT(json.getAsNumber());
			} else if (json.isBoolean()) {
				return ByteTag.valueOf(json.getAsBoolean());
			}
            return StringTag.valueOf(json.getAsString());
        } else if (o instanceof JsonObject json) {
			val tag = new OrderedCompoundTag();
			for (val entry : json.entrySet()) {
				tag.put(entry.getKey(), toNBT(entry.getValue()));
			}
			return tag;
		} else if (o instanceof JsonArray array) {
			List<Tag> list = new ArrayList<>(array.size());
			for (val element : array) {
				list.add(toNBT(element));
			}
			return toNBT(list);
		}
		//java collections
		else if (o instanceof Map<?,?> map) {
			val tag = new OrderedCompoundTag();
			for (val entry : map.entrySet()) {
				val valueNbt = NBTUtils.toNBT(entry.getValue());
				if (valueNbt != null) {
					tag.put(String.valueOf(entry.getKey()), valueNbt);
				}
			}
			return tag;
		} else if (o instanceof Collection<?> c) {
			return toNBT(c);
		}

		return null;
	}

	@HideFromJS
	public static CollectionTag<?> toNBT(Collection<?> c) {
		if (c.isEmpty()) {
			return new ListTag();
		}

		Tag[] values = new Tag[c.size()];
		int s = 0;
		byte commmonId = -1;

		for (Object o : c) {
			values[s] = toNBT(o);

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

	public static void quoteAndEscapeForJS(StringBuilder stringBuilder, String string) {
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
		return tagType == CompoundTag.TYPE ? OrderedCompoundTag.ORDERED_TYPE
			: tagType == ListTag.TYPE ? LIST_TYPE
				: tagType;
	}

	private static final TagType<ListTag> LIST_TYPE = new TagType<>() {
        @Override
        public ListTag load(DataInput dataInput, int i, NbtAccounter nbtAccounter) throws IOException {
            nbtAccounter.accountBits(296L);
            if (i > 512) {
                throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
            }
            byte b = dataInput.readByte();
            int j = dataInput.readInt();
            if (b == 0 && j > 0) {
                throw new RuntimeException("Missing type on ListTag");
            } else {
                nbtAccounter.accountBits(32L * (long) j);
                TagType<?> tagType = convertType(TagTypes.getType(b));
                ListTag list = new ListTag();

                for (int k = 0; k < j; ++k) {
                    list.add(tagType.load(dataInput, i + 1, nbtAccounter));
                }

                return list;
            }
        }

        @Override
        public String getName() {
            return "LIST";
        }

        @Override
        public String getPrettyName() {
            return "TAG_List";
        }
    };

	@Nullable
	public static OrderedCompoundTag read(FriendlyByteBuf buf) {
		int i = buf.readerIndex();
		byte b = buf.readByte();
		if (b == 0) {
			return null;
		}
		buf.readerIndex(i);
		try {
			DataInputStream stream = new DataInputStream(new ByteBufInputStream(buf));
			byte b1 = stream.readByte();
			if (b1 == 0) {
				stream.close();
				return null;
			}
			stream.readUTF();
			TagType<?> tagType = convertType(TagTypes.getType(b1));
			if (tagType != OrderedCompoundTag.ORDERED_TYPE) {
				stream.close();
				return null;
			}
			return OrderedCompoundTag.ORDERED_TYPE.load(stream, 0, NbtAccounter.UNLIMITED);
		} catch (IOException var5) {
			throw new EncoderException(var5);
		}
	}
}