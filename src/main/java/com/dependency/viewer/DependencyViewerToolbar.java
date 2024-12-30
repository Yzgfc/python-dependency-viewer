package com.dependency.viewer;

import com.dependency.viewer.actions.*;
import com.dependency.viewer.ui.SearchPanel;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;

public class DependencyViewerToolbar extends JBPanel<DependencyViewerToolbar> {
    public DependencyViewerToolbar(DependencyCanvas canvas) {
        super(new BorderLayout());
        
        // Create search panel
        SearchPanel searchPanel = new SearchPanel(canvas);
        
        // Create action group
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new RefreshAction(canvas));
        group.add(new FitToScreenAction(canvas));
        group.add(new ExportAction(canvas));
        group.add(new ToggleLayoutAction(canvas));
        
        // Create toolbar
        ActionToolbar toolbar = ActionManager.getInstance()
            .createActionToolbar("DependencyViewer", group, true);
        toolbar.setTargetComponent(canvas);
        
        // Layout components
        add(toolbar.getComponent(), BorderLayout.WEST);
        add(searchPanel, BorderLayout.CENTER);
    }
} 