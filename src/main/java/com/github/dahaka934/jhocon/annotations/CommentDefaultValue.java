package com.github.dahaka934.jhocon.annotations;

import com.github.dahaka934.jhocon.JHReflectTypeAdapterFactory;
import com.google.gson.GsonBuilder;

import java.lang.annotation.*;

/**
 * Annotation for setting a comment with default value above the field.
 * Used only if {@link JHReflectTypeAdapterFactory} is registered in {@link GsonBuilder}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CommentDefaultValue {
}
