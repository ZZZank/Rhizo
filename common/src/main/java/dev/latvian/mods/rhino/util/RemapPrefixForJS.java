package dev.latvian.mods.rhino.util;

import java.lang.annotation.*;

/**
 * Allows you to change field or method name on class scale with prefix
 *
 * @author ZZZank
 */
@Documented
@Repeatable(RemapPrefixForJSRep.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RemapPrefixForJS {
    String value();
}
