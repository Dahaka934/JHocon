package com.github.dahaka934.jhocon.annotations;

import com.github.dahaka934.jhocon.JHReflectTypeAdapterFactory;
import com.google.gson.GsonBuilder;

import java.lang.annotation.*;

/**
 * Annotation for setting a comment above the field.
 * Used only if {@link JHReflectTypeAdapterFactory} is registered in {@link GsonBuilder}.
 * You can use '$value' for inline current field's value to comment line.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Comment {
    String value() default "";
}
