package com.github.dahaka934.jhocon.annotations;

import com.github.dahaka934.jhocon.JHReflectTypeAdapterFactory;
import com.github.dahaka934.jhocon.fieldlhandler.validator.FieldValidator;
import com.google.gson.GsonBuilder;

import java.lang.annotation.*;

/**
 * This is annotation used for setting custom {@link FieldValidator}.
 * Used only if {@link JHReflectTypeAdapterFactory} is registered in {@link GsonBuilder}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidatorCustom {
    Class<? extends FieldValidator> value();
}
