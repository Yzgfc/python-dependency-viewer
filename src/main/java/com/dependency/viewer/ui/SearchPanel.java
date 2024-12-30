package com.dependency.viewer.ui;

import com.dependency.viewer.DependencyCanvas;
import com.intellij.ui.SearchTextField;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public class SearchPanel extends JPanel {
    private final SearchTextField searchField;
    private final DependencyCanvas canvas;

    public SearchPanel(DependencyCanvas canvas) {
        super(new BorderLayout());
        this.canvas = canvas;
        
        searchField = new SearchTextField();
        searchField.addDocumentListener(new com.intellij.ui.DocumentAdapter() {
            @Override
            protected void textChanged(javax.swing.event.DocumentEvent e) {
                updateSearch();
            }
        });
        
        setBorder(JBUI.Borders.empty(2, 5));
        add(searchField, BorderLayout.CENTER);
    }

    private void updateSearch() {
        String searchText = searchField.getText();
        canvas.highlightNodes(searchText);
    }
} 