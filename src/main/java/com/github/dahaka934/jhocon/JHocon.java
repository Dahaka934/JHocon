package com.github.dahaka934.jhocon;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Primitives;
import com.typesafe.config.*;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

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
     * @param src the generic object for which object tree representation is to be created
     * @param typeOfSrc the specific genericized type of {@code src}
     * @return object tree representation of {@code src}
     * @throws JsonIOException if there was a problem writing to the writer
     */
    public Object toObjectTree(Object src, Type typeOfSrc) throws JsonIOException {
        JHoconWriter writer = new JHoconWriter();
        gson.toJson(src, typeOfSrc, writer);
        return writer.output();
    }

    /**
     * Analog of {@link Gson#toJson(Object)}.
     *
     * @param src the non-generic object for which object tree representation is to be created
     * @return object tree representation of {@code src}
     * @throws JsonIOException if there was a problem writing to the writer
     */
    public Object toObjectTree(Object src) throws JsonIOException {
        src = safeObject(src);
        return toObjectTree(src, src.getClass());
    }

    /**
     * Analog of {@link Gson#toJson(Object, Type)}.
     *
     * @param src the generic object for which {@link ConfigValue} representation is to be created
     * @param typeOfSrc the specific genericized type of {@code src}
     * @return {@link ConfigValue} representation of {@code src}
     * @throws JsonIOException if there was a problem writing to the writer
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
     * @param src the non-generic object for which {@link ConfigValue} representation is to be created
     * @return {@link ConfigValue} representation of {@code src}
     * @throws JsonIOException if there was a problem writing to the writer
     */
    public ConfigValue toConfigValue(Object src) throws JsonIOException {
        src = safeObject(src);
        return toConfigValue(src, src.getClass());
    }

    /**
     * Analog of {@link Gson#toJson(Object, Type)}.
     *
     * @param src the generic object for which {@link Config} representation is to be created
     * @param typeOfSrc the specific genericized type of {@code src}
     * @return {@link Config} representation of {@code src}
     * @throws JsonIOException if there was a problem writing to the writer
     */
    @SuppressWarnings("unchecked")
    public Config toConfig(Object src, Type typeOfSrc) throws JsonIOException {
        Object view = toObjectTree(src, typeOfSrc);
        try {
            if (view instanceof Map) {
                return ConfigFactory.parseMap((Map<String, Object>) view);
            }
        } catch (Exception e) {
            throw new JsonIOException(e);
        }
        throw new JsonIOException("Object tree must be map");
    }

    /**
     * Analog of {@link Gson#toJson(Object)}.
     *
     * @param src the non-generic object for which {@link Config} representation is to be created
     * @return {@link Config} representation of {@code src}
     * @throws JsonIOException if there was a problem writing to the writer
     */
    public Config toConfig(Object src) throws JsonIOException {
        src = safeObject(src);
        return toConfig(src, src.getClass());
    }

    /**
     * Analog of {@link Gson#toJson(Object, Type)}.
     *
     * @param src the generic object for which HOCON representation is to be created
     * @param typeOfSrc the specific genericized type of {@code src}
     * @param opts the specific render options
     * @return HOCON representation of {@code src}
     * @throws JsonIOException if there was a problem writing to the writer
     */
    public String toHocon(Object src, Type typeOfSrc, ConfigRenderOptions opts) throws JsonIOException {
        ConfigValue configValue = toConfigValue(src, typeOfSrc);
        return renderConfig(configValue, opts);
    }

    /**
     * Analog of {@link Gson#toJson(Object)}.
     *
     * @param src the non-generic object for which HOCON representation is to be created
     * @param opts the specific render options
     * @return HOCON representation of {@code src}
     * @throws JsonIOException if there was a problem writing to the writer
     */
    public String toHocon(Object src, ConfigRenderOptions opts) throws JsonIOException {
        src = safeObject(src);
        return toHocon(src, src.getClass(), opts);
    }

    /**
     * @see JHocon#toHocon(Object, Type, ConfigRenderOptions)
     */
    public String toHocon(Object src, Type typeOfSrc) throws JsonIOException {
        return toHocon(src, typeOfSrc, null);
    }

    /**
     * @see JHocon#toHocon(Object, Type, ConfigRenderOptions)
     */
    public String toHocon(Object src) throws JsonIOException {
        return toHocon(src, (ConfigRenderOptions) null);
    }

    /**
     * Analog of {@link Gson#fromJson(Reader, Type)}.
     *
     * @param <T> the type of the desired object
     * @param configValue the hocon parsed value
     * @param typeOfT The specific genericized type of {@code src}
     * @return an object of type T
     * @throws JsonIOException if there was a problem reading from {@link ConfigValue}
     * @throws JsonSyntaxException if {@link ConfigValue} is not a valid representation for an object of type
     */
    public <T> T fromHocon(ConfigValue configValue, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        JHoconReader reader = new JHoconReader(configValue.unwrapped());
        return gson.fromJson(reader, typeOfT);
    }

    /**
     * Analog of {@link Gson#fromJson(Reader, Class)}
     *
     * @param <T> the type of the desired object
     * @param configValue the hocon parsed value
     * @param classOfT the class of T
     * @return an object of type T
     * @throws JsonIOException if there was a problem reading from {@link ConfigValue}
     * @throws JsonSyntaxException if {@link ConfigValue} is not a valid representation for an object of type
     */
    public <T> T fromHocon(ConfigValue configValue, Class<T> classOfT) throws JsonSyntaxException {
        T object = fromHocon(configValue, (Type) classOfT);
        return Primitives.wrap(classOfT).cast(object);
    }

    /**
     * Analog of {@link Gson#fromJson(String, Type)}.
     *
     * @param <T> the type of the desired object
     * @param hocon the hocon string
     * @param typeOfT The specific genericized type of {@code src}
     * @return an object of type T
     * @throws JsonIOException if there was a problem reading from hocon string
     * @throws JsonSyntaxException if hocon string is not a valid representation for an object of type
     */
    public <T> T fromHocon(String hocon, Type typeOfT) throws JsonSyntaxException {
        Config config;
        try {
            config = ConfigFactory.parseString(hocon);
        } catch (Exception e) {
            throw new JsonIOException(e);
        }

        return fromHocon(config.root(), typeOfT);
    }

    /**
     * Analog of {@link Gson#fromJson(String, Class)}
     *
     * @param <T> the type of the desired object
     * @param hocon the hocon string
     * @param classOfT the class of T
     * @return an object of type T
     * @throws JsonIOException if there was a problem reading from hocon string
     * @throws JsonSyntaxException if hocon string is not a valid representation for an object of type
     */
    public <T> T fromHocon(String hocon, Class<T> classOfT) throws JsonSyntaxException {
        T object = fromHocon(hocon, (Type) classOfT);
        return Primitives.wrap(classOfT).cast(object);
    }

    private static Object safeObject(Object obj) {
        return obj != null ? obj : JsonNull.INSTANCE;
    }

    private static String renderConfig(ConfigValue configValue, ConfigRenderOptions opts)
        throws JsonSyntaxException {

        opts = opts != null ? opts : ConfigRenderOptions.defaults().setJson(false).setOriginComments(false);
        try {
            return configValue.render(opts);
        } catch (Throwable throwable) {
            throw new JsonIOException(throwable);
        }
    }
}
