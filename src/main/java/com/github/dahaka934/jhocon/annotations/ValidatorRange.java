package com.github.dahaka934.jhocon.annotations;

import com.github.dahaka934.jhocon.JHReflectTypeAdapterFactory;
import com.google.gson.GsonBuilder;

import java.lang.annotation.*;

/**
 * This is annotation used for setting special validation range for field.<br/>
 * Used only if {@link JHReflectTypeAdapterFactory} is registered in {@link GsonBuilder}.<br/>
 * Used only for {@code Number}.<br/>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidatorRange {
    long min() default Long.MIN_VALUE;

    long max() default Long.MAX_VALUE;
}
