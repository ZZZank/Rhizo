class ParamInfo:
    type: str
    name: str

    def __init__(self, type: str, name: str):
        self.name = name
        self.type = type

    def format(self) -> str:
        return f"{self.type} {self.name}"

    def formatGetter(self, tab: int, stepIndent: int) -> str:
        tab = tab * " "
        step = stepIndent * " "
        lines = [
            f"public {self.type} {self.name}() " + "{",
            step + f"return this.{self.name};",
            "}",
        ]
        return "\n".join([tab + line for line in lines])


class ClassInfo:
    modifier: str
    type: str
    fields: list[ParamInfo]
    additional: str

    def __init__(self) -> None:
        pass

    # public record FunctionUnitToken(String name, List<UnitToken> args) implements UnitToken {
    def fromStr(line: str):
        idxAccess = line.index(" record ")
        idxRecord = idxAccess + len("record ")
        idxFnName = line.index("(", idxRecord + 1)
        idxParams = line.index(")", idxFnName + 1)
        idxBack = len(line) - 1

        info = ClassInfo()
        info.modifier = line[0:idxAccess]
        info.type = line[idxRecord:idxFnName].strip()
        info.additional = line[idxParams + 1 : idxBack].strip()
        info.fields = []

        for param in line[idxFnName + 1 : idxParams].split(", "):
            i = param.rindex(" ")
            info.fields.append(ParamInfo(param[0:i], param[i + 1 :]))

        return info

    def format(self):
        lines = [f"{self.modifier} class {self.type} {self.additional} " + "{"]
        lines.append("")
        tab = " " * 4
        # fields
        for field in self.fields:
            lines.append(tab + f"private final {field.type} {field.name};")
        # constructor
        lines.extend(self.formatConstructor(4, 4))
        # getters
        for field in self.fields:
            lines.append(field.formatGetter(4, 4))
        # misc
        lines.extend(self.formatToString(4, 4))
        lines.extend(self.formatEquals(4, 4))
        lines.extend(self.formatHashCode(4, 4))
        return lines

    def formatConstructor(self, indent: int, stepIndent: int):
        tab = " " * indent
        step = stepIndent * " "
        paramsConstructor = ", ".join([field.format() for field in self.fields])
        lines = [f"public {self.type}({paramsConstructor}) " + "{"]
        for field in self.fields:
            lines.append(step + f"this.{field.name} = {field.name};")
        lines.append("}")
        return [tab + line for line in lines]

    def formatToString(self, indent: int, stepIndent: int):
        tab = indent * " "
        step = stepIndent * " "
        fmtTarget = (
            self.type + " {" + ", ".join(["%s" for _ in range(len(self.fields))]) + "}"
        )
        lines = [
            "@Override",
            f"public String toString() " + "{",
            step
            + f'return String.format("{fmtTarget}", {", ".join([field.name for field in self.fields])});',
            "}",
        ]
        return [tab + line for line in lines]

    def formatEquals(self, indent: int, stepIndent: int):
        tab = indent * " "
        step = stepIndent * " "
        cmpTarget = " &&\n".join(
            [
                tab + step * 2 + f"this.{field.name} == other.{field.name}"
                for field in self.fields
            ]
        )
        lines = [
            "@Override",
            f"public boolean equals(Object obj) " + "{",
            step + f"if (!(obj instanceof {self.type})) " + "{",
            step + step + "return false;",
            step + "}",
            step + f"{self.type} other = ({self.type}) obj;",
            step + f"return\n{cmpTarget};",
            "}",
        ]
        return [tab + line for line in lines]

    def formatHashCode(self, indent, stepIndent):
        tab = indent * " "
        step = stepIndent * " "
        hashTarget = ", ".join([field.name for field in self.fields])
        lines = [
            "@Override",
            "public int hashCode() {",
            step + f"return Objects.hash({hashTarget});",
            "}",
        ]
        return [tab + line for line in lines]


line = input()
info: ClassInfo = ClassInfo.fromStr(line)
print('---')
for line in info.format():
    print(line)
