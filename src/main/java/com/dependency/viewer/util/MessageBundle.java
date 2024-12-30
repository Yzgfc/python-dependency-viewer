package com.dependency.viewer.util;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class MessageBundle extends AbstractBundle {
    private static final String BUNDLE = "messages.plugin";
    private static final MessageBundle INSTANCE = new MessageBundle();

    private MessageBundle() {
        super(BUNDLE);
    }

    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }
} 