package com.github.dahaka934.jhocon.annotations;

import com.github.dahaka934.jhocon.JHReflectTypeAdapterFactory;
import com.google.gson.GsonBuilder;

import java.lang.annotation.*;

/**
 * This is annotation used for setting a comment above the field.<br/>
 * Used only if {@link JHReflectTypeAdapterFactory} is registered in {@link GsonBuilder}.<br/>
 * You can use '$value' for inline default field value to comment line.<br/>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Comment {
    String value() default "default value: $value";
}
