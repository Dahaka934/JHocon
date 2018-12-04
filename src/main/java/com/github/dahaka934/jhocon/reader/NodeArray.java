package com.github.dahaka934.jhocon.reader;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

class NodeArray extends Node {
    private Iterator<Object> iteratorValues;

    @SuppressWarnings("unchecked")
    NodeArray(Node prev, Object value) {
        super(prev, null);
        iteratorValues = ((List<Object>) value).iterator();
        setCursor(iteratorValues.hasNext() ? iteratorValues.next() : null);
    }

    @Override
    Type getType() { return Type.ARRAY; }

    @Override
    void nextElement() throws IOException {
        setCursor(iteratorValues.hasNext() ? iteratorValues.next() : null);
    }

    String nextName() throws IOException {
        throw new IOException("Array node haven't name property");
    }
}
