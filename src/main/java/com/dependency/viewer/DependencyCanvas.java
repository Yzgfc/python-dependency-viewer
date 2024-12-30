package com.dependency.viewer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import com.dependency.viewer.layout.GraphLayoutManager;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.util.Set;
import java.util.stream.Collectors;
import com.dependency.viewer.util.MessageBundle;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DependencyCanvas extends JPanel {
    private final Project project;
    private final Map<Rectangle, ModuleNode> nodeMap = new HashMap<>();
    private final DependencyAnalyzer analyzer;
    private Map<String, ModuleNode> modules;
    
    private static final int NODE_WIDTH = 120;
    private static final int NODE_HEIGHT = 40;
    private static final int HORIZONTAL_GAP = 100;
    private static final int VERTICAL_GAP = 60;
    
    private final GraphLayoutManager layoutManager;
    private double scale = 1.0;
    private Point dragStart;
    private Point viewPosition = new Point(0, 0);
    private Set<ModuleNode> highlightedNodes = new HashSet<>();
    private boolean useForceLayout = true;
    private ForceDirectedLayout forceLayout;
    
    public DependencyCanvas(Project project) {
        this.project = project;
        this.analyzer = new DependencyAnalyzer(project);
        this.layoutManager = new GraphLayoutManager();
        setBackground(Color.WHITE);
        
        setupMouseListeners();
        setPreferredSize(new Dimension(2000, 1000));
        forceLayout = new ForceDirectedLayout();
    }
    
    private void setupMouseListeners() {
        addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                double oldScale = scale;
                scale *= (e.getWheelRotation() < 0) ? 1.1 : 0.9;
                scale = Math.max(0.1, Math.min(scale, 5.0));
                
                Point mouse = e.getPoint();
                int dx = (int) ((mouse.x - viewPosition.x) * (scale / oldScale - 1));
                int dy = (int) ((mouse.y - viewPosition.y) * (scale / oldScale - 1));
                viewPosition.translate(-dx, -dy);
                
                repaint();
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
            }
        });
        
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart != null) {
                    Point current = e.getPoint();
                    viewPosition.translate(
                        (int)((current.x - dragStart.x) / scale),
                        (int)((current.y - dragStart.y) / scale)
                    );
                    dragStart = current;
                    repaint();
                }
            }
        });
    }
    
    public void updateDependencies() {
        modules = analyzer.analyzeDependencies();
        nodeMap.clear();
        layoutNodes();
        repaint();
    }
    
    private void layoutNodes() {
        int x = 50;
        int y = 50;
        int maxHeight = 0;
        
        for (ModuleNode node : modules.values()) {
            Rectangle bounds = new Rectangle(x, y, NODE_WIDTH, NODE_HEIGHT);
            nodeMap.put(bounds, node);
            
            x += NODE_WIDTH + HORIZONTAL_GAP;
            if (x > getWidth() - NODE_WIDTH) {
                x = 50;
                y += maxHeight + VERTICAL_GAP;
                maxHeight = 0;
            }
            maxHeight = Math.max(maxHeight, NODE_HEIGHT);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // 保存原始变换
        AffineTransform originalTransform = g2d.getTransform();
        
        // 应用缩放和平移
        g2d.translate(viewPosition.x, viewPosition.y);
        g2d.scale(scale, scale);
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 使用布局管理器计算节点位置
        Map<ModuleNode, Rectangle> layout;
        if (useForceLayout) {
            layout = forceLayout.layout(modules.values(), getWidth(), getHeight());
        } else {
            layout = layoutManager.layout(modules.values(), getWidth(), getHeight());
        }
        
        // 更新节点映射
        nodeMap.clear();
        layout.forEach((node, bounds) -> nodeMap.put(bounds, node));
        
        // 绘制依赖关系线
        drawDependencies(g2d);
        
        // 绘制节点
        drawNodes(g2d);
        
        // 恢复原始变换
        g2d.setTransform(originalTransform);
    }
    
    private void drawDependencies(Graphics2D g2d) {
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(1.5f));
        
        for (Map.Entry<Rectangle, ModuleNode> entry : nodeMap.entrySet()) {
            Rectangle bounds = entry.getKey();
            ModuleNode node = entry.getValue();
            
            for (ModuleNode dependency : node.getDependencies()) {
                Rectangle depBounds = findBounds(dependency);
                if (depBounds != null) {
                    drawArrow(g2d, bounds, depBounds);
                }
            }
        }
    }
    
    private void drawNodes(Graphics2D g2d) {
        for (Map.Entry<Rectangle, ModuleNode> entry : nodeMap.entrySet()) {
            Rectangle bounds = entry.getKey();
            ModuleNode node = entry.getValue();
            
            // 根据搜索结果设置节点颜色
            if (highlightedNodes.contains(node)) {
                g2d.setColor(new Color(255, 240, 200));
            } else if (!highlightedNodes.isEmpty()) {
                g2d.setColor(new Color(220, 220, 220));
            } else {
                g2d.setColor(new Color(240, 240, 255));
            }
            
            // 绘制节点背景
            g2d.fill(bounds);
            g2d.setColor(new Color(100, 100, 200));
            g2d.draw(bounds);
            
            // 绘制模块名称
            g2d.setColor(Color.BLACK);
            FontMetrics fm = g2d.getFontMetrics();
            String name = node.getModuleName();
            int textX = bounds.x + (bounds.width - fm.stringWidth(name)) / 2;
            int textY = bounds.y + (bounds.height + fm.getAscent()) / 2;
            g2d.drawString(name, textX, textY);
        }
    }
    
    private Rectangle findBounds(ModuleNode node) {
        for (Map.Entry<Rectangle, ModuleNode> entry : nodeMap.entrySet()) {
            if (entry.getValue().equals(node)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    private void drawArrow(Graphics2D g2d, Rectangle from, Rectangle to) {
        int x1 = from.x + from.width / 2;
        int y1 = from.y + from.height / 2;
        int x2 = to.x + to.width / 2;
        int y2 = to.y + to.height / 2;
        
        g2d.drawLine(x1, y1, x2, y2);
        
        // 绘制箭头
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowLength = 10;
        int arrowAngle = 30;
        
        int x3 = (int) (x2 - arrowLength * Math.cos(angle - Math.toRadians(arrowAngle)));
        int y3 = (int) (y2 - arrowLength * Math.sin(angle - Math.toRadians(arrowAngle)));
        int x4 = (int) (x2 - arrowLength * Math.cos(angle + Math.toRadians(arrowAngle)));
        int y4 = (int) (y2 - arrowLength * Math.sin(angle + Math.toRadians(arrowAngle)));
        
        g2d.drawLine(x2, y2, x3, y3);
        g2d.drawLine(x2, y2, x4, y4);
    }
    
    private void handleClick(Point point) {
        for (Map.Entry<Rectangle, ModuleNode> entry : nodeMap.entrySet()) {
            if (entry.getKey().contains(point)) {
                ModuleNode node = entry.getValue();
                showReferences(node);
                break;
            }
        }
    }
    
    private void showReferences(ModuleNode node) {
        List<String> references = node.getReferences();
        if (references.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                MessageBundle.message("dialog.no.references"));
            return;
        }
        
        String[] options = references.toArray(new String[0]);
        String selected = (String) JOptionPane.showInputDialog(
            this,
            MessageBundle.message("dialog.references.message"),
            MessageBundle.message("dialog.references.title"),
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (selected != null) {
            navigateToReference(node.getFilePath(), Integer.parseInt(selected.split(":")[0]));
        }
    }
    
    private void navigateToReference(String filePath, int offset) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
        if (virtualFile != null) {
            new OpenFileDescriptor(project, virtualFile, offset).navigate(true);
        }
    }
    
    public void highlightModules(String searchText) {
        if (searchText.isEmpty()) {
            highlightedNodes.clear();
        } else {
            highlightedNodes = modules.values().stream()
                .filter(node -> node.getModuleName().toLowerCase().contains(searchText))
                .collect(Collectors.toSet());
        }
        repaint();
    }
    
    public void toggleLayoutMode() {
        useForceLayout = !useForceLayout;
        updateDependencies();
    }
    
    public void fitToScreen() {
        if (modules == null || modules.isEmpty()) return;
        
        // 重置视图位置
        viewPosition = new Point(0, 0);
        
        // 计算合适的缩放比例
        Rectangle bounds = null;
        for (Rectangle rect : nodeMap.keySet()) {
            if (bounds == null) {
                bounds = new Rectangle(rect);
            } else {
                bounds.add(rect);
            }
        }
        
        if (bounds != null) {
            double scaleX = (double) getWidth() / (bounds.width + 100);
            double scaleY = (double) getHeight() / (bounds.height + 100);
            scale = Math.min(scaleX, scaleY);
            scale = Math.max(0.1, Math.min(scale, 2.0));
        }
        
        repaint();
    }
    
    public void exportImage(File file) {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // 绘制白色背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // 绘制内容
        paint(g2d);
        g2d.dispose();
        
        try {
            ImageIO.write(image, "PNG", file);
            JOptionPane.showMessageDialog(this,
                MessageBundle.message("dialog.export.success"),
                MessageBundle.message("dialog.export.title"),
                JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                MessageBundle.message("dialog.export.error", ex.getMessage()),
                MessageBundle.message("dialog.error.title"),
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 