package com.dependency.viewer;

import com.dependency.viewer.analysis.PythonDependencyAnalyzer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DependencyViewerPanel extends JPanel {
    private final Project project;
    private final DependencyCanvas canvas;
    private final PythonDependencyAnalyzer analyzer;

    public DependencyViewerPanel(Project project) {
        super(new BorderLayout());
        this.project = project;
        this.analyzer = new PythonDependencyAnalyzer(project);
        
        // Create canvas
        canvas = new DependencyCanvas();
        JBScrollPane scrollPane = new JBScrollPane(canvas);
        scrollPane.setBorder(JBUI.Borders.empty());
        
        // Create toolbar
        DependencyViewerToolbar toolbar = new DependencyViewerToolbar(canvas);
        
        // Layout components
        add(toolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Initial analysis
        analyzeProject();
    }

    private void analyzeProject() {
        List<ModuleNode> nodes = new ArrayList<>();
        VirtualFile projectDir = project.getBasePath() != null ? 
            VirtualFileManager.getInstance().findFileByUrl("file://" + project.getBasePath()) : null;
        
        if (projectDir != null) {
            analyzePythonFiles(projectDir, nodes);
            canvas.setNodes(nodes);
            canvas.fitToScreen();
        }
    }

    private void analyzePythonFiles(VirtualFile dir, List<ModuleNode> nodes) {
        if (!dir.isValid() || !dir.isDirectory()) {
            return;
        }
        
        for (VirtualFile file : dir.getChildren()) {
            if (file.isDirectory()) {
                analyzePythonFiles(file, nodes);
            } else if (file.isValid() && "py".equals(file.getExtension())) {
                ModuleNode node = analyzer.analyzeFile(file);
                if (node != null) {
                    nodes.add(node);
                }
            }
        }
    }
} 