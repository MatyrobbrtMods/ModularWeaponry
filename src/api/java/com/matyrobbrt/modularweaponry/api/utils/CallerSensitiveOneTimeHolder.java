package com.matyrobbrt.modularweaponry.api.utils;

import java.lang.StackWalker.Option;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CallerSensitiveOneTimeHolder<T> implements Supplier<T>, Consumer<Supplier<T>> {

    private final Class<?> caller;
    private Supplier<T> value = null;

    public CallerSensitiveOneTimeHolder(Class<?> caller) {
        this.caller = caller;
    }

    @Override
    public T get() {
        if (value == null)
            throw new IllegalStateException("Tried to retrieve value before initialization!");
        return value.get();
    }

    @Override
    public void accept(Supplier<T> t) {
        if (value != null)
            throw new IllegalStateException("Tried to set value again after initialization!");
        if (StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE).getCallerClass() != caller)
            throw new IllegalCallerException("Illegal caller trying to set value!");
        value = t;
    }
}
