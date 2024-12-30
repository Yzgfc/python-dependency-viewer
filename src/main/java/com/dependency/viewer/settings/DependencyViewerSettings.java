package com.dependency.viewer.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.ComboBox;
import com.dependency.viewer.util.MessageBundle;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class DependencyViewerSettings implements Configurable {
    private JPanel mainPanel;
    private ComboBox<String> languageComboBox;
    private static final String[] SUPPORTED_LANGUAGES = {"English", "中文"};

    @Override
    public String getDisplayName() {
        return MessageBundle.message("plugin.name");
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        languageComboBox = new ComboBox<>(SUPPORTED_LANGUAGES);
        languageComboBox.setSelectedItem(getStoredLanguage());
        
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel(MessageBundle.message("settings.language.label")), c);
        
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        mainPanel.add(languageComboBox, c);
        
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        return !getStoredLanguage().equals(languageComboBox.getSelectedItem());
    }

    @Override
    public void apply() throws ConfigurationException {
        String selectedLanguage = (String) languageComboBox.getSelectedItem();
        DependencyViewerSettingsState.getInstance().setLanguage(selectedLanguage);
    }

    private String getStoredLanguage() {
        return DependencyViewerSettingsState.getInstance().getLanguage();
    }
} 