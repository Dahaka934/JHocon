package com.google.gson.stream;

import java.io.IOException;
import java.io.Writer;

public class JsonWriterStub extends JsonWriter {
    private static final Writer STUB_WRITER = new Writer() {
        @Override
        public void write(char[] cbuf, int off, int len) throws IOException { }

        @Override
        public void flush() throws IOException { }

        @Override
        public void close() throws IOException { }
    };

    public JsonWriterStub() {
        super(STUB_WRITER);
    }
}
