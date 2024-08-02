package dev.latvian.mods.rhino.annotations.typing;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
@JSInfo("""
    should only be used within {@link JSParams}
    
    @author ZZZank
    """)
public @interface JSParam {
    @JSInfo("used for adding descriptions, just like {@link JSInfo}")
    String value();

    @JSInfo("optional, used for renaming a param, left empty to not rename")
    String rename() default "";
}
