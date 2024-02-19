package dev.latvian.mods.rhino.mod.util;

import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import dev.latvian.mods.rhino.mod.util.MojangMappings.TypeDef;
import dev.latvian.mods.rhino.util.BackportUtil;

import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MojangMappings {
	private final String mcVersion;
	private final Map<String, ClassDef> classes;
	private final Map<String, ClassDef> classesMM;
	private final Map<TypeDef, TypeDef> allTypes;
	private final Map<MethodDefSignature, MethodDefSignature> methodSignatures;

	private final ClassDef VOID = new ClassDef(this, "void").descriptor("V");
	private final ClassDef BOOLEAN = new ClassDef(this, "boolean").descriptor("Z");
	private final ClassDef CHAR = new ClassDef(this, "char").descriptor("C");
	private final ClassDef BYTE = new ClassDef(this, "byte").descriptor("B");
	private final ClassDef SHORT = new ClassDef(this, "short").descriptor("S");
	private final ClassDef INT = new ClassDef(this, "int").descriptor("I");
	private final ClassDef LONG = new ClassDef(this, "long").descriptor("J");
	private final ClassDef FLOAT = new ClassDef(this, "float").descriptor("F");
	private final ClassDef DOUBLE = new ClassDef(this, "double").descriptor("D");

	private final MethodDefSignature SIG_EMPTY = new MethodDefSignature();

	private MojangMappings(String mc) {
		mcVersion = mc;
		classes = new HashMap<>();
		classesMM = new HashMap<>();
		allTypes = new HashMap<>();
		methodSignatures = new HashMap<>();

		for (ClassDef c : new ClassDef[]{
				VOID,
				BOOLEAN,
				CHAR,
				BYTE,
				SHORT,
				INT,
				LONG,
				FLOAT,
				DOUBLE,
		}) {
			classes.put(c.rawName, c);
			allTypes.put(c.noArrayType, c.noArrayType);
		}
	}

	public MethodDefSignature getSignature(TypeDef[] types) {
		if (types.length == 0) {
			return SIG_EMPTY;
		} else if (types.length == 1) {
			return types[0].getSingleArgumentSignature();
		}

		MethodDefSignature sig = new MethodDefSignature(types);
		MethodDefSignature cached = methodSignatures.get(sig);

		if (cached != null) {
			return cached;
		}

		methodSignatures.put(sig, sig);
		return sig;
	}

	public MethodDefSignature readSignatureFromDescriptor(String descriptor) throws Exception {
		if (descriptor.length() >= 2 && descriptor.charAt(0) == '(' && descriptor.charAt(1) == ')') {
			return SIG_EMPTY;
		}

		StringReader reader = new StringReader(descriptor);
		List<TypeDef> types = new ArrayList<TypeDef>(2);

		while (true) {
			int c = reader.read();

			if (c == '(') {
				continue;
			}

			int array = 0;

			while (c == '[') {
				array++;
				c = reader.read();
			}

			if (c == -1 || c == ')') {
				break;
			}

			if (c == 'L') {
				StringBuilder sb = new StringBuilder();

				while (true) {
					c = reader.read();

					if (c == -1) {
						throw new RemapperException("Invalid descriptor: " + descriptor);
					} else if (c == ';') {
						break;
					} else if (c == '/') {
						sb.append('.');
					} else {
						sb.append((char) c);
					}
				}

				types.add(getType(sb.toString()).parent.array(array));
			} else if (c == 'Z') {
				types.add(BOOLEAN.array(array));
			} else if (c == 'C') {
				types.add(CHAR.array(array));
			} else if (c == 'B') {
				types.add(BYTE.array(array));
			} else if (c == 'S') {
				types.add(SHORT.array(array));
			} else if (c == 'I') {
				types.add(INT.array(array));
			} else if (c == 'J') {
				types.add(LONG.array(array));
			} else if (c == 'F') {
				types.add(FLOAT.array(array));
			} else if (c == 'D') {
				types.add(DOUBLE.array(array));
			} else if (c == 'V') {
				types.add(VOID.array(array));
			} else {
				throw new RemapperException("Invalid descriptor: " + descriptor);
			}
		}

		if (types.isEmpty()) {
			return SIG_EMPTY;
		} else if (types.size() == 1) {
			return types.get(0).getSingleArgumentSignature();
		}

		return getSignature(types.toArray(new TypeDef[0]));
	}

	@Nullable
	public ClassDef getClass(String name) {
		ClassDef mmc = classesMM.get(name);
		return mmc != null ? mmc : classes.get(name);
	}

	public TypeDef getType(String string) {
		int array = 0;

		while (string.endsWith("[]")) {
			array++;
			string = string.substring(0, string.length() - 2);
		}

		while (string.startsWith("[")) {
			array++;
			string = string.substring(1);
		}

		ClassDef c = getClass(string);

		if (c != null) {
			return c.array(array);
		}

		c = new ClassDef(this, string);
		classes.put(string, c);
		allTypes.put(c.noArrayType, c.noArrayType);
		return c.array(array);
	}

	private static boolean invalidLine(String s) {
		return s.trim().isEmpty() || s.startsWith("#") || s.endsWith("init>") || s.contains(".package-info ");
	}

	private void parse0(List<String> lines) {
		lines.removeIf(MojangMappings::invalidLine);

		for (String line : lines) {
			if (line.charAt(line.length() - 1) == ':') {
				String[] s = line.split(" -> ", 2); // replace with faster, last index of space check
				ClassDef c = new ClassDef(this, s[1].substring(0, s[1].length() - 1), s[0], new HashMap<>(0), new HashSet<>(0));
				c.mapped = true;
				classes.put(c.rawName, c);
				classesMM.put(c.mmName, c);
				allTypes.put(c.noArrayType, c.noArrayType);
			}
		}

		ClassDef currentClassDef = null;

		for (String line : lines) {
			if (line.charAt(0) == ' ') {
				if (currentClassDef == null) {
					throw new RemapperException("Field or method without class! " + line);
				}

				line = line.substring(Math.max(4, line.lastIndexOf(':') + 1));
				int typeSpace = line.indexOf(' ');
				TypeDef type = getType(line.substring(0, typeSpace));
				line = line.substring(typeSpace + 1);
				String rawName = line.substring(line.lastIndexOf(' ') + 1);
				line = line.substring(0, line.indexOf(' '));
				String name;
				MethodDefSignature sig;

				if (line.charAt(line.length() - 1) == ')') {
					int lp = line.indexOf('(');
					name = line.substring(0, lp);
					line = line.substring(lp + 1, line.length() - 1);

					if (line.isEmpty()) {
						sig = SIG_EMPTY;
					} else {
						String[] sclasses = line.split(",");
						TypeDef[] types = new TypeDef[sclasses.length];
						for (int i = 0; i < sclasses.length; i++) {
							types[i] = getType(sclasses[i]);
						}

						sig = getSignature(types);
					}
				} else {
					name = line;
					sig = null;
				}

				NamedSignature rawNameSig = new NamedSignature(rawName, sig);

				if (name.startsWith("lambda$") || name.startsWith("access$") || line.startsWith("val$") || line.startsWith("this$")) {
					currentClassDef.ignoredMembers.add(rawNameSig);
					continue;
				}

				MemberDef m = new MemberDef(currentClassDef, rawNameSig, name, type, new MutableObject<>(""));
				currentClassDef.members.put(rawNameSig, m);
			} else if (line.charAt(line.length() - 1) == ':') {
				currentClassDef = classes.get(line.substring(line.lastIndexOf(' ') + 1, line.length() - 1));
			}
		}
	}

	public void cleanup() {
		classes.values().removeIf(ClassDef::cleanup);
	}

	public void updateOccurrences() {
		for (TypeDef c : allTypes.values()) {
			c.occurrences = 0;
		}

		for (MethodDefSignature m : methodSignatures.values()) {
			m.occurrences = 0;
		}

		for (ClassDef c : classes.values()) {
			for (MemberDef m : c.members.values()) {
				if (m.rawName.signature != null && m.rawName.signature.types.length > 0) {
					m.rawName.signature.occurrences++;

					for (TypeDef t : m.rawName.signature.types) {
						t.occurrences++;
					}
				}
			}
		}
	}

	private static void writeVarInt(OutputStream stream, int value) throws Exception {
		RemappingHelper.writeVarInt(stream, value);
	}

	private static void writeUtf(OutputStream stream, String value) throws Exception {
		RemappingHelper.writeUtf(stream, value);
	}

	public void write(OutputStream stream) throws Exception {
		cleanup();
		updateOccurrences();

		List<TypeDef> typeDefList = new ArrayList<>(allTypes.values());
		typeDefList.sort(TypeDef::compareTo);

		List<TypeDef> unmappedTypes = new ArrayList<TypeDef>();
		List<TypeDef> mappedTypes = new ArrayList<TypeDef>();
		List<TypeDef> arrayTypes = new ArrayList<TypeDef>();

		for (int i = 0; i < typeDefList.size(); i++) {
			TypeDef c = typeDefList.get(i);
			c.index = i;

			if (c.array > 0) {
				arrayTypes.add(c);
			} else if (c.parent.mapped) {
				mappedTypes.add(c);
			} else {
				unmappedTypes.add(c);
			}
		}

		List<MethodDefSignature> sigList = new ArrayList<>(methodSignatures.values());
		sigList.sort(MethodDefSignature::compareTo);

		for (int i = 0; i < sigList.size(); i++) {
			sigList.get(i).index = i;
		}

		RemappingHelper.LOGGER.info("Total Types: " + typeDefList.size());
		RemappingHelper.LOGGER.info("Total Signatures: " + sigList.size());
		RemappingHelper.LOGGER.info("Unmapped Types: " + unmappedTypes.size());
		RemappingHelper.LOGGER.info("Mapped Types: " + mappedTypes.size());
		RemappingHelper.LOGGER.info("Array Types: " + arrayTypes.size());

		stream.write(0); // Binary indicator
		stream.write(1); // Version
		writeUtf(stream, mcVersion);

		writeVarInt(stream, unmappedTypes.size());
		writeVarInt(stream, mappedTypes.size());
		writeVarInt(stream, arrayTypes.size());

		for (TypeDef c : unmappedTypes) {
			writeVarInt(stream, c.index);
			writeUtf(stream, c.parent.rawName);
		}

		for (TypeDef c : mappedTypes) {
			writeVarInt(stream, c.index);
			writeUtf(stream, c.parent.unmappedName.getValue());
			writeUtf(stream, c.parent.mmName);
		}

		for (TypeDef c : arrayTypes) {
			writeVarInt(stream, c.index);
			writeVarInt(stream, c.parent.noArrayType.index);
			writeVarInt(stream, c.array);
		}

		writeVarInt(stream, sigList.size());

		for (MethodDefSignature s : sigList) {
			writeVarInt(stream, s.types.length);

			for (TypeDef c : s.types) {
				writeVarInt(stream, c.index);
			}
		}

		for (TypeDef c : mappedTypes) {
			List<MemberDef> fields = new ArrayList<MemberDef>();
			List<MemberDef> arg0methods = new ArrayList<MemberDef>();
			List<MemberDef> argNmethods = new ArrayList<MemberDef>();

			for (MemberDef f : c.parent.members.values()) {
				if (f.rawName.signature == null) {
					fields.add(f);
				} else if (f.rawName.signature.types.length == 0) {
					arg0methods.add(f);
				} else {
					argNmethods.add(f);
				}
			}

			writeVarInt(stream, fields.size());
			writeVarInt(stream, arg0methods.size());
			writeVarInt(stream, argNmethods.size());

			for (MemberDef m : fields) {
				writeUtf(stream, m.unmappedName.getValue());
				writeUtf(stream, m.mmName);
			}

			for (MemberDef m : arg0methods) {
				writeUtf(stream, m.unmappedName.getValue());
				writeUtf(stream, m.mmName);
			}

			for (MemberDef m : argNmethods) {
				writeUtf(stream, m.unmappedName.getValue());
				writeUtf(stream, m.mmName);
				writeVarInt(stream, m.rawName.signature.index);
			}
		}
	}

	public static MojangMappings parse(String mcVersion, List<String> lines) throws Exception {
		MojangMappings mappings = new MojangMappings(mcVersion);
		mappings.parse0(lines);
		return mappings;
	}

	public static class MethodDefSignature {
		public final TypeDef[] types;
		public int occurrences;
		public int index;

		public MethodDefSignature(TypeDef... types) {
			this.types = types;
			this.occurrences = 0;
			this.index = -1;
		}

		@Override
		public boolean equals(Object o) {
			return this == o || o instanceof MethodDefSignature && Arrays.equals(types, ((MethodDefSignature) o).types);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(types);
		}

		@Override
		public String toString() {
			if (types.length == 0) {
				return "";
			} else if (types.length == 1) {
				return types[0].toString();
			}

			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < types.length; i++) {
				if (i > 0) {
					sb.append(',');
				}

				sb.append(types[i]);
			}

			return sb.toString();
		}

		public int compareTo(MethodDefSignature other) {
			return Integer.compare(other.occurrences, occurrences);
		}
	}

	public static class NamedSignature {
		private final String name;
		@Nullable private final MethodDefSignature signature;
		public NamedSignature(String name, @Nullable MethodDefSignature signature) {
			this.name = name;
			this.signature = signature;
		}
		public String name() {
			return this.name;
		}
		public MethodDefSignature signature() {
			return this.signature;
		}

		@Override
		public String toString() {
			if (signature == null) {
				return name;
			}
			return name + "(" + signature + ")";
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof NamedSignature) {
				NamedSignature other = (NamedSignature) obj;
				return name.equals(other.name) && Objects.equals(signature, other.signature);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return name.hashCode() * 31 + (signature == null ? 0 : signature.hashCode());
		}
	}

	public static final class ClassDef {
		public final MojangMappings mappings;
		public final String rawName;
		public final String mmName;
		public final String displayName;
		public final Map<NamedSignature, MemberDef> members;
		public final Set<NamedSignature> ignoredMembers;
		private final MutableObject<String> unmappedName;
		public boolean mapped;

		public TypeDef noArrayType;
		public String rawDescriptor;

		public ClassDef(MojangMappings mappings, String rawName, String mmName, Map<NamedSignature, MemberDef> members, Set<NamedSignature> ignoredMembers) {
			this.mappings = mappings;
			this.rawName = rawName;
			this.mmName = mmName;
			String dn = mmName.isEmpty() ? rawName : mmName;
			int dni = dn.lastIndexOf('.');
			this.displayName = dni == -1 ? dn : dn.substring(dni + 1);
			this.members = members;
			this.ignoredMembers = ignoredMembers;
			this.unmappedName = new MutableObject<>("");
			this.mapped = false;
			this.noArrayType = new TypeDef(this, 0);
		}

		public ClassDef(MojangMappings mappings, String name) {
			this(mappings, name, "", new HashMap<>(), new HashSet<>());
		}

		public ClassDef descriptor(String s) {
			this.rawDescriptor = s;
			return this;
		}

		private TypeDef array(int a) {
			if (a == 0) {
				return noArrayType;
			}

			TypeDef t = new TypeDef(this, a);
			TypeDef t1 = mappings.allTypes.get(t);

			if (t1 == null) {
				mappings.allTypes.put(t, t);
				return t;
			}

			return t1;
		}

		@Override
		public int hashCode() {
			return rawName.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof ClassDef) {
				ClassDef other = (ClassDef) obj;
				return rawName.equals(other.rawName);
			}
			return false;
		}

		@Override
		public String toString() {
			return displayName;
		}

		public MutableObject<String> unmappedName() {
			return unmappedName;
		}

		public String getRawDescriptor() {
			if (rawDescriptor == null) {
				rawDescriptor = 'L' + rawName.replace('.', '/') + ';';
			}

			return rawDescriptor;
		}

		public boolean cleanup() {
			if (mapped) {
				members.values().removeIf(MemberDef::cleanup);
				return members.isEmpty() && unmappedName.getValue().isEmpty();
			}

			return false;
		}
	}

	public static final class TypeDef {
		public final ClassDef parent;
		public final int array;
		public int occurrences;
		private MethodDefSignature singleArgumentSignature;
		private String rawDescriptor;
		public int index;

		public TypeDef(ClassDef parent, int array) {
			this.parent = parent;
			this.array = array;
			this.occurrences = 0;
			this.singleArgumentSignature = null;
			this.rawDescriptor = null;
			this.index = -1;
		}

		public String getRawDescriptor() {
			if (rawDescriptor == null) {
				if (array > 0) {
					rawDescriptor = BackportUtil.repeat("[", array) + parent.getRawDescriptor();
				} else {
					rawDescriptor = parent.getRawDescriptor();
				}
			}

			return rawDescriptor;
		}

		public MethodDefSignature getSingleArgumentSignature() {
			if (singleArgumentSignature == null) {
				singleArgumentSignature = new MethodDefSignature(this);
				parent.mappings.methodSignatures.put(singleArgumentSignature, singleArgumentSignature);
			}

			return singleArgumentSignature;
		}

		public int compareTo(TypeDef other) {
			return Integer.compare(other.occurrences, occurrences);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof TypeDef) {
				TypeDef t = (TypeDef) obj;
				return parent.equals(t.parent) && array == t.array;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(parent, array);
		}

		@Override
		public String toString() {
			return getRawDescriptor();
		}

	}

	public class MemberDef {

		private final ClassDef parent;
		private final NamedSignature rawName;
		private final String mmName;
		private final TypeDef type;
		private final MutableObject<String> unmappedName;
		public ClassDef parent() {
			return this.parent;
		}
		public NamedSignature rawName() {
			return this.rawName;
		}
		public String mmName() {
			return this.mmName;
		}
		public TypeDef type() {
			return this.type;
		}
		public MutableObject<String> unmappedName() {
			return this.unmappedName;
		}
		MemberDef(ClassDef parent, NamedSignature rawName, String mmName, TypeDef type, MutableObject<String> unmappedName) {
			this.parent = parent;
			this.rawName = rawName;
			this.mmName = mmName;
			this.type = type;
			this.unmappedName = unmappedName;
		}
		
		@Override
		public String toString() {
			return String.format("MemberDef {%s, %s, %s, %s, %s}", parent, rawName, mmName, type,unmappedName);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof MemberDef) {
				MemberDef other = (MemberDef) obj;
				return parent == other.parent &&
					rawName == other.rawName &&
					mmName == other.mmName &&
					type == other.type &&
					unmappedName == other.unmappedName;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(parent, rawName, mmName, type, unmappedName);
		}


		public boolean cleanup() {
			return unmappedName.getValue().isEmpty();
		}
	}
}
