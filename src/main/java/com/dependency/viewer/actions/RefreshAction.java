package com.dependency.viewer.actions;

import com.dependency.viewer.DependencyCanvas;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class RefreshAction extends AnAction {
    private final DependencyCanvas canvas;

    public RefreshAction(DependencyCanvas canvas) {
        super("Refresh", "Refresh dependency graph", AllIcons.Actions.Refresh);
        this.canvas = canvas;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        canvas.refresh();
    }
} 