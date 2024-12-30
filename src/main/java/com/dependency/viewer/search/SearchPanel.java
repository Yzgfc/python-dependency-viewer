package com.dependency.viewer.search;

import com.dependency.viewer.DependencyCanvas;
import com.intellij.ui.SearchTextField;
import com.intellij.util.ui.JBUI;
import com.dependency.viewer.util.MessageBundle;

import javax.swing.*;
import java.awt.*;

public class SearchPanel extends JPanel {
    private final SearchTextField searchField;
    private final DependencyCanvas canvas;
    
    public SearchPanel(DependencyCanvas canvas) {
        this.canvas = canvas;
        this.setLayout(new BorderLayout());
        
        searchField = new SearchTextField();
        searchField.getTextEditor().setToolTipText(MessageBundle.message("search.placeholder"));
        searchField.addDocumentListener(new SearchListener());
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBorder(JBUI.Borders.empty(5));
        controlPanel.add(new JLabel(MessageBundle.message("search.label")));
        controlPanel.add(searchField);
        
        this.add(controlPanel, BorderLayout.CENTER);
    }
    
    private class SearchListener implements javax.swing.event.DocumentListener {
        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            updateSearch();
        }
        
        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            updateSearch();
        }
        
        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            updateSearch();
        }
        
        private void updateSearch() {
            String searchText = searchField.getText().toLowerCase();
            canvas.highlightNodes(searchText);
        }
    }
} 