package com.github.dahaka934.jhocon;

import com.github.dahaka934.jhocon.fieldlhandler.FieldHandlerComment;
import com.github.dahaka934.jhocon.fieldlhandler.FieldHandlerValidator;
import com.github.dahaka934.jhocon.fieldlhandler.validator.FieldValidatorCustomAnnotation;
import com.github.dahaka934.jhocon.fieldlhandler.validator.FieldValidatorList;
import com.github.dahaka934.jhocon.fieldlhandler.validator.FieldValidatorRange;
import com.github.dahaka934.jhocon.writer.JHoconWriter;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;

import java.util.Arrays;

public final class JHoconHelper {
    private JHoconHelper() {
        throw new UnsupportedOperationException();
    }

    /**
     * Register JHocon features in GsonBuilder.
     */
    public static GsonBuilder initBuilder(GsonBuilder builder) {
        FieldHandlerValidator validator = new FieldHandlerValidator(false);
        validator.register(new FieldValidatorCustomAnnotation());
        validator.register(new FieldValidatorRange());
        validator.register(new FieldValidatorList());

        JHReflectTypeAdapterFactory factory = new JHReflectTypeAdapterFactory();
        factory.register(new FieldHandlerComment());
        factory.register(validator);

        builder.registerTypeAdapterFactory(factory);
        return builder;
    }

    /**
     * Renders the config value to a string, using the provided options.
     */
    public static String renderConfig(ConfigValue configValue, ConfigRenderOptions opts) throws JsonSyntaxException {
        ConfigRenderOptions options = opts != null
            ? opts
            : ConfigRenderOptions.defaults().setJson(false).setOriginComments(true);

        String hocon;
        try {
            hocon = configValue.render(options);
        } catch (Throwable throwable) {
            throw new JsonSyntaxException(throwable);
        }

        // Config print '#  ' for empty comment. Try fix it.
        hocon = hocon.replaceAll("[ ]+#\n", "");

        return hocon;
    }

    /**
     * Safe hook for insert comment to writer.
     */
    public static void comment(JsonWriter writer, String comment) {
        if (writer instanceof JHoconWriter) {
            ((JHoconWriter) writer).comment(comment);
        }
    }

    /**
     * Convert object to string.
     */
    public static String objectToString(Object obj) {
        if (obj == null) {
            return "(null)";
        } else if (!obj.getClass().isArray()) {
            return obj.toString();
        } else if (obj instanceof Object[]) {
            return Arrays.toString((Object[]) obj);
        } else if (obj instanceof boolean[]) {
            return Arrays.toString((boolean[]) obj);
        } else if (obj instanceof byte[]) {
            return Arrays.toString((byte[]) obj);
        } else if (obj instanceof char[]) {
            return Arrays.toString((char[]) obj);
        } else if (obj instanceof short[]) {
            return Arrays.toString((short[]) obj);
        } else if (obj instanceof int[]) {
            return Arrays.toString((int[]) obj);
        } else if (obj instanceof float[]) {
            return Arrays.toString((float[]) obj);
        } else if (obj instanceof double[]) {
            return Arrays.toString((double[]) obj);
        } else {
            return "";
        }
    }
}
