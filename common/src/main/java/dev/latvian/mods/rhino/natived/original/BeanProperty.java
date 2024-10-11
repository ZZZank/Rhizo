package dev.latvian.mods.rhino.natived.original;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BeanProperty {
    public final MemberBox getter;
    public final MemberBox setter;
    public final NativeJavaMethod setters;
}
