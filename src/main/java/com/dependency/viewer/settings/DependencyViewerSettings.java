package com.dependency.viewer.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DependencyViewerSettings implements Configurable {
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Python Dependency Viewer";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return new JPanel();  // TODO: Add settings UI
    }

    @Override
    public boolean isModified() {
        return false;  // TODO: Implement settings modification check
    }

    @Override
    public void apply() {
        // TODO: Apply settings
    }
} 