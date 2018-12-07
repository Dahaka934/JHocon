package com.github.dahaka934.jhocon.fieldlhandler;

import com.github.dahaka934.jhocon.JHoconHelper;
import com.github.dahaka934.jhocon.annotations.CommentDefaultValue;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.lang.reflect.Field;

/**
 * Implementation for {@link CommentDefaultValue} annotation.
 *
 * @see CommentDefaultValue
 */
public class FieldHandlerCommentDefaultValue implements FieldHandler {
    @Override
    public void onWrite(JsonWriter writer, Field field, Object value) {
        if (field.getAnnotation(CommentDefaultValue.class) != null) {
            String line = "default value: " + JHoconHelper.objectToString(value);
            JHoconHelper.comment(writer, line);
        }
    }

    @Override
    public Object onRead(JsonReader reader, Field field, Object value) {
        return value;
    }
}
