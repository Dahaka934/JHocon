package com.github.dahaka934.jhocon;

import com.google.gson.stream.JsonReaderStub;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Using for read object from specific structure instead of json.
 */
public class JHoconReader extends JsonReaderStub {
    private Node curr;

    public JHoconReader(Object value) {
        curr = new Node(null, value);
    }

    private enum Type {
        ROOT, OBJECT, ARRAY
    }

    private static class Node {
        private Object cursor;
        private Node prev;
        private boolean isEnd = false;

        Node(Node prev, Object value) {
            this.prev = prev;
            cursor = value;
        }

        Type getType() { return Type.ROOT; }

        Object getCursor() {
            return cursor;
        }

        void setCursor(Object cursor) {
            this.cursor = cursor;
        }

        void nextElement() throws IOException {
            isEnd = true;
            setCursor(null);
        }

        void signalReadMap() {}

        void assertEnd() throws IOException {
            if (isEnd) {
                throw new IOException("End of file");
            }
        }

        Node beginArray() throws IOException {
            assertEnd();
            return new NodeArray(this, getCursor());
        }

        Node endArray() throws IOException {
            isEnd = true;
            setCursor(null);
            prev.nextElement();
            return prev;
        }

        Node beginObject() throws IOException {
            assertEnd();
            return new NodeObject(this, getCursor());
        }

        Node endObject() throws IOException {
            isEnd = true;
            setCursor(null);
            prev.nextElement();
            return prev;
        }

        boolean hasNext() throws IOException {
            return !isEnd && getCursor() != null;
        }

        JsonToken peek() throws IOException {
            if (isEnd) {
                switch (getType()) {
                    case OBJECT:
                        return JsonToken.END_OBJECT;
                    case ARRAY:
                        return JsonToken.END_ARRAY;
                    case ROOT:
                        return JsonToken.END_DOCUMENT;
                }
            }

            Object cursor = getCursor();
            if (cursor == null) {
                return JsonToken.NULL;
            } else if (cursor instanceof String) {
                return JsonToken.STRING;
            } else if (cursor instanceof Boolean) {
                return JsonToken.BOOLEAN;
            } else if (cursor instanceof Number) {
                return JsonToken.NUMBER;
            } else if (cursor instanceof Map) {
                return JsonToken.BEGIN_OBJECT;
            } else if (cursor instanceof List) {
                return JsonToken.BEGIN_ARRAY;
            }
            return JsonToken.NULL;
        }

        String nextName() throws IOException {
            throw new IOException("Root node haven't name property");
        }

        void nextNull() throws IOException {
            assertEnd();
            nextElement();
        }

        String nextString() throws IOException {
            assertEnd();
            String ret = getCursor().toString();
            nextElement();
            return ret;
        }

        boolean nextBoolean() throws IOException {
            assertEnd();
            Object cursor = getCursor();
            boolean ret = false;
            if (cursor instanceof Boolean) {
                ret = (Boolean) cursor;
            } else if (cursor instanceof String) {
                ret = Boolean.valueOf(cursor.toString());
            }
            nextElement();
            return ret;
        }

        double nextDouble() throws IOException {
            assertEnd();
            Object cursor = getCursor();
            double ret = 0.0;
            if (cursor instanceof Number) {
                ret = ((Number) cursor).doubleValue();
            } else if (cursor instanceof String) {
                ret = Double.valueOf(cursor.toString());
            }
            nextElement();
            return ret;
        }

        long nextLong() throws IOException {
            assertEnd();
            Object cursor = getCursor();
            long ret = 0L;
            if (cursor instanceof Number) {
                ret = ((Number) cursor).longValue();
            } else if (cursor instanceof String) {
                ret = Long.valueOf(cursor.toString());
            }
            nextElement();
            return ret;
        }

        int nextInt() throws IOException {
            assertEnd();
            Object cursor = getCursor();
            int ret = 0;
            if (cursor instanceof Number) {
                ret = ((Number) cursor).intValue();
            } else if (cursor instanceof String) {
                ret = Integer.valueOf(cursor.toString());
            }
            nextElement();
            return ret;
        }

        String getPath() {
            throw new RuntimeException("Unsupported operation");
        }
    }

    private static class NodeObject extends Node {
        private Iterator<String> iteratorKeys;
        private Iterator<Object> iteratorValues;

        private Object keyCursor;
        private boolean isKeyCursor = false;

        @SuppressWarnings("unchecked")
        NodeObject(Node prev, Object value) {
            super(prev, null);
            Map<String, Object> map = (Map<String, Object>) value;
            iteratorKeys = map.keySet().iterator();
            iteratorValues = map.values().iterator();
            setCursor(iteratorValues.hasNext() ? iteratorValues.next() : null);
        }

        @Override
        Type getType() { return Type.OBJECT; }

        @Override
        void signalReadMap() {
            isKeyCursor = true;
            keyCursor = iteratorKeys.hasNext() ? iteratorKeys.next() : null;
        }

        @Override
        Object getCursor() {
            return (isKeyCursor) ? keyCursor : super.getCursor();
        }

        @Override
        void nextElement() throws IOException {
            if (isKeyCursor) {
                isKeyCursor = false;
            } else {
                setCursor(iteratorValues.hasNext() ? iteratorValues.next() : null);
            }
        }

        @Override
        String nextName() throws IOException {
            return (iteratorKeys.hasNext()) ? (String) iteratorKeys.next() : "unnamed";
        }
    }

    private static class NodeArray extends Node {
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

    @Override
    public void beginArray() throws IOException {
        curr = curr.beginArray();
    }

    @Override
    public void endArray() throws IOException {
        curr = curr.endArray();
    }

    @Override
    public void beginObject() throws IOException {
        curr = curr.beginObject();
    }

    @Override
    public void endObject() throws IOException {
        curr = curr.endObject();
    }

    @Override
    public boolean hasNext() throws IOException {
        return curr.hasNext();
    }

    @Override
    public JsonToken peek() throws IOException {
        setPeeked(0);
        return curr.peek();
    }

    @Override
    public String nextName() throws IOException {
        return curr.nextName();
    }

    @Override
    public String nextString() throws IOException {
        return curr.nextString();
    }

    @Override
    public boolean nextBoolean() throws IOException {
        return curr.nextBoolean();
    }

    @Override
    public void nextNull() throws IOException {
        curr.nextNull();
    }

    @Override
    public double nextDouble() throws IOException {
        return curr.nextDouble();
    }

    @Override
    public long nextLong() throws IOException {
        return curr.nextLong();
    }

    @Override
    public int nextInt() throws IOException {
        return curr.nextInt();
    }

    @Override
    public String getPath() {
        return curr.getPath();
    }

    @Override
    public void close() throws IOException {}

    @Override
    public void skipValue() throws IOException { }

    @Override
    public String toString() {
        return curr.getCursor().toString();
    }

    @Override
    protected int doPeek() {
        // Hack
        // Signal from JsonReaderInternalAccess.INSTANCE.promoteNameToValue
        // Start reading map
        curr.signalReadMap();
        return 13; // PEEKED_DOUBLE_QUOTED_NAME
    }
}
