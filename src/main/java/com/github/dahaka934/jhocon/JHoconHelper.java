package com.github.dahaka934.jhocon;

import com.github.dahaka934.jhocon.writer.JHoconWriter;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;

public final class JHoconHelper {
    private JHoconHelper() {
        throw new UnsupportedOperationException();
    }

    /**
     * Renders the config value to a string, using the provided options.
     */
    public static String renderConfig(ConfigValue configValue, ConfigRenderOptions opts) throws JsonSyntaxException {
        ConfigRenderOptions options = opts != null
            ? opts
            : ConfigRenderOptions.defaults().setJson(false).setOriginComments(false);
        try {
            return configValue.render(options);
        } catch (Throwable throwable) {
            throw new JsonSyntaxException(throwable);
        }
    }

    /**
     * Safe hook for insert comment to writer.
     */
    public static void comment(JsonWriter writer, String comment) {
        if (writer instanceof JHoconWriter) {
            ((JHoconWriter) writer).comment(comment);
        }
    }
}
