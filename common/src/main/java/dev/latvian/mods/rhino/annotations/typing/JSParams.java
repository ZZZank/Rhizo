package dev.latvian.mods.rhino.annotations.typing;

import java.lang.annotation.*;

/**
 * note: ordinal
 * <p>
 * use null to skip a param
 * @author ZZZank
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@JSInfo("used to provide extra, optional information for parameters in a method ")
public @interface JSParams {

    @JSInfo("""
        Each `JSParam` in this array represents information for a parameter.
        
        `JSParam`s should be mapped to parameters in ordinal,
        that is, the first `JSParam` should be mapped to the first parameter, the second mapped to the second, and so on.
        
        Use `null` to skip a parameter.
        
        If this array holds more `JSParam`s than parameters, extra `JSParam`s should be ignored.
        """)
    JSParam[] params();
}
