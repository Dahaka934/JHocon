package com.github.dahaka934.jhocon;

import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonWriterStub;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Using for write object to specific structure instead of json.
 */
public class JHoconWriter extends JsonWriterStub {
    private Node curr = new Node();

    /**
     * @return writing output
     */
    public Object output() {
        return curr.getValue();
    }

    /**
     * Set comment to current node.
     */
    public void setComment(String comment) {
        curr.comment = comment;
    }

    private static class Node {
        private Object value;
        private String comment = null;
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
            this.value = convert(value);
        }

        Object convert(Object value) {
            return ConfigValueFactory.fromAnyRef(value, comment);
        }

        Node beginArray() throws IOException {
            return new NodeArray(this);
        }

        Node endArray() throws IOException {
            prev.put(getValue());
            return prev;
        }

        Node beginObject() throws IOException {
            return new NodeObject(this);
        }

        Node endObject() throws IOException {
            prev.put(getValue());
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
            ((HashMap<String, Object>) getValue()).put(name, convert(value));
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
            ((ArrayList<Object>) getValue()).add(convert(value));
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
