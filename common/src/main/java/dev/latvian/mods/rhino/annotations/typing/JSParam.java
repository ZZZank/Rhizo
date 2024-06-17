package dev.latvian.mods.rhino.annotations.typing;

import java.lang.annotation.*;

/**
 * @author ZZZank
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JSParam {
    String rename() default "";
    String type();
}
