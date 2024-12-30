package com.dependency.viewer.actions;

import com.dependency.viewer.DependencyCanvas;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.dependency.viewer.util.MessageBundle;
import org.jetbrains.annotations.NotNull;

public class FitToScreenAction extends AnAction implements DumbAware {
    private final DependencyCanvas canvas;

    public FitToScreenAction(DependencyCanvas canvas) {
        super(MessageBundle.message("toolbar.fit"),
              MessageBundle.message("toolbar.fit.description"),
              null);
        this.canvas = canvas;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        canvas.fitToScreen();
    }
} 