package com.dependency.viewer.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.application.ApplicationManager;

@State(
    name = "DependencyViewerSettings",
    storages = @Storage("dependencyViewerSettings.xml")
)
public class DependencyViewerSettingsState implements PersistentStateComponent<DependencyViewerSettingsState> {
    private String language = "English";

    public static DependencyViewerSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(DependencyViewerSettingsState.class);
    }

    @Nullable
    @Override
    public DependencyViewerSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull DependencyViewerSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
} 