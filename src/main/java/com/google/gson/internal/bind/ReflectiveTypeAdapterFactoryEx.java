package com.google.gson.internal.bind;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.*;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.Adapter;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.BoundField;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Hack.
 * This {@link TypeAdapterFactory} analog of {@link ReflectiveTypeAdapterFactory}.
 * Provides several public methods for custom field handling (by annotation).
 */
public abstract class ReflectiveTypeAdapterFactoryEx implements TypeAdapterFactory {

    protected Gson gson;
    protected ConstructorConstructor constructorConstructor;
    protected FieldNamingStrategy fieldNamingPolicy;
    protected Excluder excluder;
    protected JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;

    private boolean isInited = false;

    @SuppressWarnings("unchecked")
    private <I, T> T getFieldValue(I instance, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(instance);
    }

    private void init(Gson gson) {
        try {
            this.gson = gson;
            List<TypeAdapterFactory> factories = getFieldValue(gson, "factories");
            ReflectiveTypeAdapterFactory factory = (ReflectiveTypeAdapterFactory) factories.get(factories.size() - 1);
            constructorConstructor = getFieldValue(factory, "constructorConstructor");
            fieldNamingPolicy = getFieldValue(factory, "fieldNamingPolicy");
            excluder = getFieldValue(factory, "excluder");
            jsonAdapterFactory = getFieldValue(factory, "jsonAdapterFactory");
        } catch (Exception e) {
            throw new Error("Used unsupported Gson version");
        }
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!isInited) {
            init(gson);
            isInited = true;
        }

        Class<? super T> raw = type.getRawType();

        if (!Object.class.isAssignableFrom(raw)) {
            return null; // it's a primitive!
        }

        TypeAdapter<T> another = gson.getDelegateAdapter(this, type);
        if (!(another instanceof Adapter)) {
            return another;
        }

        ObjectConstructor<T> constructor = constructorConstructor.get(type);
        return new Adapter<>(constructor, getBoundFields(gson, type, raw));
    }

    public boolean excludeField(Field f, boolean serialize) {
        return ReflectiveTypeAdapterFactory.excludeField(f, serialize, excluder);
    }

    public abstract void writeField(JsonWriter writer, TypeAdapter adapter, Field field, Object value)
        throws IOException;

    public abstract Object readField(JsonReader reader, TypeAdapter adapter, Field field)
        throws IOException;

    private List<String> getFieldNames(Field f) {
        SerializedName annotation = f.getAnnotation(SerializedName.class);
        if (annotation == null) {
            String name = fieldNamingPolicy.translateName(f);
            return Collections.singletonList(name);
        }

        String serializedName = annotation.value();
        String[] alternates = annotation.alternate();
        if (alternates.length == 0) {
            return Collections.singletonList(serializedName);
        }

        List<String> fieldNames = new ArrayList<>(alternates.length + 1);
        fieldNames.add(serializedName);
        fieldNames.addAll(Arrays.asList(alternates));
        return fieldNames;
    }

    @SuppressWarnings("unchecked")
    private ReflectiveTypeAdapterFactory.BoundField createBoundField(
        Gson context, Field field, String name,
        TypeToken<?> fieldType, boolean serialize, boolean deserialize) {
        boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());
        // special casing primitives here saves ~5% on Android...
        JsonAdapter annotation = field.getAnnotation(JsonAdapter.class);
        TypeAdapter<?> mapped = null;
        if (annotation != null) {
            mapped = jsonAdapterFactory.getTypeAdapter(constructorConstructor, context, fieldType, annotation);
        }
        boolean jsonAdapterPresent = mapped != null;
        if (mapped == null) {
            mapped = context.getAdapter(fieldType);
        }

        TypeAdapter<?> typeAdapter = mapped;
        return new ReflectiveTypeAdapterFactory.BoundField(name, serialize, deserialize) {
            // the type adapter and field type always agree
            @Override
            void write(JsonWriter writer, Object value)
                throws IOException, IllegalAccessException {
                Object fieldValue = field.get(value);
                TypeAdapter t = jsonAdapterPresent ? typeAdapter
                    : new TypeAdapterRuntimeTypeWrapper(context, typeAdapter, fieldType.getType());
                ReflectiveTypeAdapterFactoryEx.this.writeField(writer, t, field, fieldValue);
            }

            @Override
            void read(JsonReader reader, Object value)
                throws IOException, IllegalAccessException {
                Object fieldValue = readField(reader, typeAdapter, field);
                if (fieldValue != null || !isPrimitive) {
                    field.set(value, fieldValue);
                }
            }

            @Override
            public boolean writeField(Object value) throws IOException, IllegalAccessException {
                if (!serialized) {
                    return false;
                }
                Object fieldValue = field.get(value);
                return fieldValue != value; // avoid recursion for example for Throwable.cause
            }
        };
    }

    private Map<String, BoundField> getBoundFields(Gson context, TypeToken<?> type, Class<?> raw) {
        Map<String, BoundField> result = new LinkedHashMap<>();
        if (raw.isInterface()) {
            return result;
        }

        Type declaredType = type.getType();
        while (raw != Object.class) {
            Field[] fields = raw.getDeclaredFields();
            for (Field field : fields) {
                boolean serialize = excludeField(field, true);
                boolean deserialize = excludeField(field, false);
                if (!serialize && !deserialize) {
                    continue;
                }
                field.setAccessible(true);
                Type fieldType = $Gson$Types.resolve(type.getType(), raw, field.getGenericType());
                List<String> fieldNames = getFieldNames(field);
                BoundField previous = null;
                for (int i = 0, size = fieldNames.size(); i < size; ++i) {
                    String name = fieldNames.get(i);
                    if (i != 0) {
                        serialize = false; // only serialize the default name
                    }
                    BoundField boundField = createBoundField(context, field, name,
                        TypeToken.get(fieldType), serialize, deserialize);
                    BoundField replaced = result.put(name, boundField);
                    if (previous == null) {
                        previous = replaced;
                    }
                }
                if (previous != null) {
                    throw new IllegalArgumentException(declaredType
                        + " declares multiple JSON fields named " + previous.name);
                }
            }
            type = TypeToken.get($Gson$Types.resolve(type.getType(), raw, raw.getGenericSuperclass()));
            raw = type.getRawType();
        }
        return result;
    }
}
