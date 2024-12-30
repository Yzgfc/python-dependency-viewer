package com.dependency.viewer.actions;

import com.dependency.viewer.DependencyCanvas;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ExportAction extends AnAction {
    private final DependencyCanvas canvas;

    public ExportAction(DependencyCanvas canvas) {
        super("Export as Image", "Export dependency graph as PNG image", AllIcons.ToolbarDecorator.Export);
        this.canvas = canvas;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        FileSaverDescriptor descriptor = new FileSaverDescriptor("Export Graph", "Choose where to save the image", "png");
        FileSaverDialog dialog = FileChooserFactory.getInstance().createSaveFileDialog(descriptor, e.getProject());
        VirtualFileWrapper wrapper = dialog.save((VirtualFile)null, "dependency-graph.png");
        
        if (wrapper != null) {
            try {
                BufferedImage image = canvas.createImage();
                ImageIO.write(image, "PNG", wrapper.getFile());
            } catch (IOException ex) {
                // Handle error
            }
        }
    }
} 