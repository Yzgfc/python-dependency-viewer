package com.dependency.viewer;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.dependency.viewer.util.MessageBundle;

import javax.swing.*;
import java.awt.*;

public class DependencyViewerToolbar extends SimpleToolWindowPanel {
    private final DependencyCanvas canvas;
    
    public DependencyViewerToolbar(Project project) {
        super(true, true);
        
        canvas = new DependencyCanvas(project);
        setContent(new JScrollPane(canvas));
        
        SearchPanel searchPanel = new SearchPanel(canvas);
        
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new RefreshAction(canvas));
        group.add(new FitToScreenAction(canvas));
        group.add(new ExportAction(canvas));
        group.add(new ToggleLayoutAction(canvas));
        
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(
            "DependencyViewer",
            group,
            true
        );
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(toolbar.getComponent(), BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        
        setToolbar(topPanel);
    }
    
    public DependencyCanvas getCanvas() {
        return canvas;
    }
} 