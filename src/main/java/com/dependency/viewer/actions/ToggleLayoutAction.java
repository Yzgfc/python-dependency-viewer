package com.dependency.viewer.actions;

import com.dependency.viewer.DependencyCanvas;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;
import com.dependency.viewer.util.MessageBundle;

public class ToggleLayoutAction extends AnAction implements DumbAware {
    private final DependencyCanvas canvas;
    private boolean isForceLayout = true;
    
    public ToggleLayoutAction(DependencyCanvas canvas) {
        super(MessageBundle.message("toolbar.toggle.layout"),
              MessageBundle.message("toolbar.toggle.layout.description"),
              null);
        this.canvas = canvas;
    }
    
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        isForceLayout = !isForceLayout;
        canvas.toggleLayoutMode();
        e.getPresentation().setText(isForceLayout ? 
            MessageBundle.message("toolbar.toggle.layout.force") : 
            MessageBundle.message("toolbar.toggle.layout.hierarchical"));
    }
} 