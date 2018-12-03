package com.github.dahaka934.jhocon;

import com.google.gson.Gson;

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
}
