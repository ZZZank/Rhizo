package dev.latvian.mods.rhino.annotations.typing;

/**
 * note: ordinal
 * <p>
 * use null to skip a param
 * @author ZZZank
 */
@JSInfo("Note: ordinal. Use null to skip a param")
public @interface JSParams {
    JSParam[] params() default {};
}
