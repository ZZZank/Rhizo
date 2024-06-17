package dev.latvian.mods.rhino.annotations.typing;

import java.lang.annotation.*;

/**
 * note: ordinal
 * <p>
 * use null to skip a param
 * @author ZZZank
 */
@Documented
@JSInfo("Note: ordinal. Use null to skip a param")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JSParams {
    JSParam[] params();
}
