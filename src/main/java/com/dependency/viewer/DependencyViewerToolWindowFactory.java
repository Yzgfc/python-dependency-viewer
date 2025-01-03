package com.dependency.viewer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class DependencyViewerToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        DependencyViewerPanel viewerPanel = new DependencyViewerPanel(project);
        Content content = ContentFactory.getInstance().createContent(viewerPanel, "", false);
        toolWindow.getContentManager().addContent(content);
    }
} 