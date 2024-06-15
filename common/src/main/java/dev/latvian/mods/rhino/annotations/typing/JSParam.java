package dev.latvian.mods.rhino.annotations.typing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author ZZZank
 */
@Target(ElementType.METHOD)
public @interface JSParam {
    String rename() default "";
    String type();
}
