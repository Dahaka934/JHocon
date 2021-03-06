package com.google.gson.stream;

import com.google.gson.internal.reflect.ReflectionAccessor;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class JsonReaderStub extends JsonReader {
    private static final Reader STUB_READER = new Reader() {
        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            return 0;
        }

        @Override
        public void close() throws IOException { }
    };

    static {
        try {
            // Some hacks for java 11
            Method m = JsonReaderStub.class.getDeclaredMethod("doPeek");
            Field f = JsonReader.class.getDeclaredField("peeked");
            ReflectionAccessor.getInstance().makeAccessible(m);
            ReflectionAccessor.getInstance().makeAccessible(f);
        } catch (NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public JsonReaderStub() {
        super(STUB_READER);
    }

    protected int getPeeked() {
        return peeked;
    }

    protected void setPeeked(int value) {
        peeked = value;
    }

    @Override
    protected int doPeek() {
        return peeked;
    }
}
