package com.github.dahaka934.jhocon;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;

import java.lang.reflect.Type;

/**
 * Wrapper of {@link Gson}.<br/>
 * Provides some useful method for convert objects to hocon files, and vice versa.<br/>
 * JHocon instances are thread-safe, because {@link Gson} is thread is thread-safe.<br/>
 */
public final class JHocon {
    public final Gson gson;

    public JHocon(Gson gson) {
        this.gson = gson;
    }

    /**
     * Analog of {@link Gson#toJson(Object, Type)}.
     *
     * @param src the generic object for which object tree representation is to be created.
     * @param typeOfSrc The specific genericized type of {@code src}.
     * @return object tree representation of {@code src}.
     * @throws JsonIOException if there was a problem writing to the writer.
     */
    public Object toObjectTree(Object src, Type typeOfSrc) throws JsonIOException {
        JHoconWriter writer = new JHoconWriter();
        gson.toJson(src, typeOfSrc, writer);
        return writer.output();
    }

    /**
     * Analog of {@link Gson#toJson(Object)}.
     *
     * @param src the non-generic object for which object tree representation is to be created.
     * @return object tree representation of {@code src}.
     * @throws JsonIOException if there was a problem writing to the writer.
     */
    public Object toObjectTree(Object src) throws JsonIOException {
        src = safeObject(src);
        return toObjectTree(src, src.getClass());
    }

    /**
     * Analog of {@link Gson#toJson(Object, Type)}.
     *
     * @param src the generic object for which {@link ConfigValue} representation is to be created.
     * @param typeOfSrc The specific genericized type of {@code src}.
     * @return {@link ConfigValue} representation of {@code src}.
     * @throws JsonIOException if there was a problem writing to the writer.
     */
    public ConfigValue toConfigValue(Object src, Type typeOfSrc) throws JsonIOException {
        Object view = toObjectTree(src, typeOfSrc);
        try {
            return ConfigValueFactory.fromAnyRef(view);
        } catch (Exception e) {
            throw new JsonIOException(e);
        }
    }

    /**
     * Analog of {@link Gson#toJson(Object)}.
     *
     * @param src the non-generic object for which {@link ConfigValue} representation is to be created.
     * @return {@link ConfigValue} representation of {@code src}.
     * @throws JsonIOException if there was a problem writing to the writer.
     */
    public ConfigValue toConfigValue(Object src) throws JsonIOException {
        src = safeObject(src);
        return toConfigValue(src, src.getClass());
    }

    private static Object safeObject(Object obj) {
        return obj != null ? obj : JsonNull.INSTANCE;
    }
}
