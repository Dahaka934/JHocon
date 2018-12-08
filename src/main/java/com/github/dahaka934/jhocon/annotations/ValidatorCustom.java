package com.github.dahaka934.jhocon.annotations;

import com.github.dahaka934.jhocon.JHReflectTypeAdapterFactory;
import com.github.dahaka934.jhocon.fieldlhandler.FieldHandlerValidator;
import com.github.dahaka934.jhocon.fieldlhandler.validator.FieldValidator;
import com.github.dahaka934.jhocon.fieldlhandler.validator.FieldValidatorCustomAnnotation;
import com.google.gson.GsonBuilder;

import java.lang.annotation.*;

/**
 * This is annotation used for setting custom {@link FieldValidator} for field.<br/>
 * You must register a {@link JHReflectTypeAdapterFactory} in {@link GsonBuilder}.<br/>
 * You must register a {@link FieldHandlerValidator} in {@link JHReflectTypeAdapterFactory}.<br/>
 * You must register implementer ({@link FieldValidator}) of this annotation in {@link FieldHandlerValidator}.<br/>
 * Default implementer is {@link FieldValidatorCustomAnnotation}.<br/>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidatorCustom {
    Class<? extends FieldValidator> value();
}
