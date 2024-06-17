package dev.latvian.mods.rhino.annotations.typing;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * comments
 * @author ZZZank
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface JSInfo {
    String value();
}
