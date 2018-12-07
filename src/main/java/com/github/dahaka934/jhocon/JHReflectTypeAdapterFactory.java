package com.github.dahaka934.jhocon;

import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactoryEx;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Field;

public class JHReflectTypeAdapterFactory extends ReflectiveTypeAdapterFactoryEx {
    @Override
    @SuppressWarnings("unchecked")
    public void writeField(JsonWriter writer, TypeAdapter adapter, Field field, Object value) throws IOException {
        adapter.write(writer, value);
    }

    @Override
    public Object readField(JsonReader reader, TypeAdapter adapter, Field field) throws IOException {
        return adapter.read(reader);
    }
}
