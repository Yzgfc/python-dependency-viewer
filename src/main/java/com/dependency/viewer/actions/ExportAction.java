package com.dependency.viewer.actions;

import com.dependency.viewer.DependencyCanvas;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.dependency.viewer.util.MessageBundle;
import org.jetbrains.annotations.NotNull;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class ExportAction extends AnAction implements DumbAware {
    private final DependencyCanvas canvas;

    public ExportAction(DependencyCanvas canvas) {
        super(MessageBundle.message("toolbar.export"),
              MessageBundle.message("toolbar.export.description"),
              null);
        this.canvas = canvas;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(MessageBundle.message("dialog.export.title"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Image", "png"));
        
        if (fileChooser.showSaveDialog(canvas) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getParentFile(), file.getName() + ".png");
            }
            canvas.exportImage(file);
        }
    }
} 