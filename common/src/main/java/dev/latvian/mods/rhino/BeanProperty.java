package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.natived.original.MemberBox;
import dev.latvian.mods.rhino.natived.original.NativeJavaMethod;

public class BeanProperty {
    MemberBox getter;
    MemberBox setter;
    NativeJavaMethod setters;
    BeanProperty(MemberBox getter, MemberBox setter, NativeJavaMethod setters) {
        this.getter = getter;
        this.setter = setter;
        this.setters = setters;
    }
}
