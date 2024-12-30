package com.dependency.viewer;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.dependency.viewer.util.MessageBundle;

import javax.swing.*;
import java.awt.*;

public class DependencyViewerPanel extends JPanel {
    private final Project project;
    private final DependencyCanvas canvas;

    public DependencyViewerPanel(Project project) {
        this.project = project;
        this.setLayout(new BorderLayout());
        
        canvas = new DependencyCanvas(project);
        JBScrollPane scrollPane = new JBScrollPane(canvas);
        this.add(scrollPane, BorderLayout.CENTER);
        
        // 添加刷新按钮
        JButton refreshButton = new JButton(MessageBundle.message("toolbar.refresh"));
        refreshButton.addActionListener(e -> refreshDependencies());
        this.add(refreshButton, BorderLayout.NORTH);
    }

    private void refreshDependencies() {
        canvas.updateDependencies();
    }
} 