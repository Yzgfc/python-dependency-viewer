package com.dependency.viewer.actions;

import com.dependency.viewer.DependencyCanvas;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class ToggleLayoutAction extends AnAction {
    private final DependencyCanvas canvas;
    private boolean isHierarchical = true;

    public ToggleLayoutAction(DependencyCanvas canvas) {
        super("Toggle Layout", "Switch between hierarchical and force-directed layouts", AllIcons.Actions.ChangeView);
        this.canvas = canvas;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        isHierarchical = !isHierarchical;
        canvas.setLayoutType(isHierarchical);
    }
} 