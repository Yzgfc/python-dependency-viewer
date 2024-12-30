package com.dependency.viewer.actions;

import com.dependency.viewer.DependencyCanvas;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class FitToScreenAction extends AnAction {
    private final DependencyCanvas canvas;

    public FitToScreenAction(DependencyCanvas canvas) {
        super("Fit to Screen", "Fit graph to screen size", AllIcons.General.FitContent);
        this.canvas = canvas;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        canvas.fitToScreen();
    }
} 