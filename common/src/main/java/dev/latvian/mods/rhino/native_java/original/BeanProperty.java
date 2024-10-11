package dev.latvian.mods.rhino.native_java.original;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BeanProperty {
    public final MemberBox getter;
    public final MemberBox setter;
    public final NativeJavaMethod setters;
}
