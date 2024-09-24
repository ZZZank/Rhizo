package dev.latvian.mods.rhino.natived.original;

public class BeanProperty {
    public final MemberBox getter;
    public final MemberBox setter;
    public final NativeJavaMethod setters;
    BeanProperty(MemberBox getter, MemberBox setter, NativeJavaMethod setters) {
        this.getter = getter;
        this.setter = setter;
        this.setters = setters;
    }
}
