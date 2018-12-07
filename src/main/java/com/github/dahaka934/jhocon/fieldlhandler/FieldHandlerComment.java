package com.github.dahaka934.jhocon.fieldlhandler;

import com.github.dahaka934.jhocon.JHoconHelper;
import com.github.dahaka934.jhocon.annotations.Comment;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.lang.reflect.Field;

public class FieldHandlerComment implements FieldHandler {
    @Override
    public void onWrite(JsonWriter writer, Field field, Object value) {
        Comment comment = field.getAnnotation(Comment.class);
        if (comment != null) {
            String line = comment.value();
            if (line.contains("$value")) {
                line = line.replace("$value", JHoconHelper.objectToString(value));
            }
            JHoconHelper.comment(writer, line);
        }
    }

    @Override
    public Object onRead(JsonReader reader, Field field, Object value) {
        return value;
    }
}
