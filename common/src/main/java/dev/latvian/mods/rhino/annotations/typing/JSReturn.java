package dev.latvian.mods.rhino.annotations.typing;

import java.lang.annotation.*;

/**
 * When applied to a method, it should be targeting its return type.
 * <p>
 * When applied to a field, it should be targeting its type
 * @author ZZZank
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface JSReturn {
    String type();
}
