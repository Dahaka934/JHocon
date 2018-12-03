package com.github.dahaka934.jhocon;

import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonWriterStub;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Using for write object to specific structure instead of json.
 */
public class JHoconWriter extends JsonWriterStub {
    private Node curr = new Node();

    public Object output() {
        return curr.getValue();
    }

    private static class Node {
        private Object value;
        private Node prev = this;

        Node() {}

        Node(Node prev) {
            this.prev = prev;
        }

        Object getValue() {
            return value;
        }

        void setValue(Object value) {
            this.value = value;
        }

        void put(Object value) {
            this.value = value;
        }

        Node beginArray() throws IOException {
            Node next = new NodeArray(this);
            put(next.getValue());
            return next;
        }

        Node endArray() throws IOException {
            return prev;
        }

        Node beginObject() throws IOException {
            Node next = new NodeObject(this);
            put(next.getValue());
            return next;
        }

        Node endObject() throws IOException {
            return prev;
        }

        void name(String name) throws IOException {
            throw new IOException("Root node haven't name property");
        }

        void value(Object value) throws IOException {
            put(value);
        }

        void jsonValue(String value) throws IOException {
            try {
                Config config = ConfigFactory.parseString(value);
                put(config.root().unwrapped());
            } catch (Exception e) {
                put(null);
            }
        }

        void nullValue() throws IOException {
            put(null);
        }
    }

    private static class NodeObject extends Node {
        private String name = "unnamed";

        NodeObject(Node prev) {
            super(prev);
            setValue(new HashMap<String, Object>());
        }

        @Override
        @SuppressWarnings("unchecked")
        void put(Object value) {
            ((HashMap<String, Object>) getValue()).put(name, value);
        }

        @Override
        void name(String name) throws IOException {
            this.name = name;
        }
    }

    private static class NodeArray extends Node {
        NodeArray(Node prev) {
            super(prev);
            setValue(new ArrayList<>());
        }

        @Override
        @SuppressWarnings("unchecked")
        void put(Object value) {
            ((ArrayList<Object>) getValue()).add(value);
        }

        @Override
        void name(String name) throws IOException {
            throw new IOException("Array node haven't name property");
        }
    }

    @Override
    public JsonWriter beginArray() throws IOException {
        curr = curr.beginArray();
        return this;
    }

    @Override
    public JsonWriter endArray() throws IOException {
        curr = curr.endArray();
        return this;
    }

    @Override
    public JsonWriter beginObject() throws IOException {
        curr = curr.beginObject();
        return this;
    }

    @Override
    public JsonWriter endObject() throws IOException {
        curr = curr.endObject();
        return this;
    }

    @Override
    public JsonWriter name(String name) throws IOException {
        curr.name(name);
        return this;
    }

    @Override
    public JsonWriter value(String value) throws IOException {
        curr.value(value);
        return this;
    }

    @Override
    public JsonWriter jsonValue(String value) throws IOException {
        curr.jsonValue(value);
        return this;
    }

    @Override
    public JsonWriter nullValue() throws IOException {
        curr.nullValue();
        return this;
    }

    @Override
    public JsonWriter value(boolean value) throws IOException {
        curr.value(value);
        return this;
    }

    @Override
    public JsonWriter value(Boolean value) throws IOException {
        curr.value(value);
        return this;
    }

    @Override
    public JsonWriter value(double value) throws IOException {
        curr.value(value);
        return this;
    }

    @Override
    public JsonWriter value(long value) throws IOException {
        curr.value(value);
        return this;
    }

    @Override
    public JsonWriter value(Number value) throws IOException {
        curr.value(value);
        return this;
    }

    @Override
    public boolean isLenient() {
        return true;
    }

    @Override
    public void flush() throws IOException {}

    @Override
    public void close() throws IOException {}
}
