package dev.latvian.mods.rhino.annotations.typing;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@JSInfo("Provide list of generic types when runtime erases them, usually for method params/return type")
public @interface Generics {
	Class<?>[] value();

	Class<?> base() default Object.class;
}
