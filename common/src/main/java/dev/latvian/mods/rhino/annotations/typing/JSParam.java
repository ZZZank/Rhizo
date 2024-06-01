package dev.latvian.mods.rhino.annotations.typing;

/**
 * @author ZZZank
 */
public @interface JSParam {
    String rename() default "";
    String type();
}
