package dev.latvian.mods.rhino.annotations.typing;

/**
 * note: ordinal
 * @author ZZZank
 */
public @interface JSParams {
    String[] renames() default {};
    String[] types();
}
