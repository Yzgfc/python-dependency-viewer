package com.dependency.viewer;

import com.dependency.viewer.layout.ForceDirectedLayout;
import com.dependency.viewer.layout.HierarchicalLayout;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;

public class DependencyCanvas extends JPanel {
    private final Set<ModuleNode> nodes = new HashSet<>();
    private final Set<ModuleNode> highlightedNodes = new HashSet<>();
    private final ForceDirectedLayout forceLayout;
    private final HierarchicalLayout hierarchicalLayout;
    private boolean isHierarchicalLayout = true;
    
    private double scale = 1.0;
    private double translateX = 0;
    private double translateY = 0;
    private Point lastMousePos;

    public DependencyCanvas() {
        forceLayout = new ForceDirectedLayout();
        hierarchicalLayout = new HierarchicalLayout();
        setupMouseListeners();
        setBackground(JBColor.background());
    }

    private void setupMouseListeners() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePos = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastMousePos != null) {
                    translateX += (e.getX() - lastMousePos.x) / scale;
                    translateY += (e.getY() - lastMousePos.y) / scale;
                    lastMousePos = e.getPoint();
                    repaint();
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown()) {
                    double zoom = e.getWheelRotation() < 0 ? 1.1 : 0.9;
                    Point2D p = e.getPoint();
                    double dx = (p.getX() - translateX);
                    double dy = (p.getY() - translateY);
                    scale *= zoom;
                    translateX = p.getX() - dx * zoom;
                    translateY = p.getY() - dy * zoom;
                    repaint();
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);
    }

    public void setNodes(@NotNull Collection<ModuleNode> nodes) {
        this.nodes.clear();
        this.nodes.addAll(nodes);
        updateLayout();
    }

    public void highlightNodes(String searchText) {
        highlightedNodes.clear();
        if (!searchText.isEmpty()) {
            String lowercaseSearch = searchText.toLowerCase();
            for (ModuleNode node : nodes) {
                if (node.getName().toLowerCase().contains(lowercaseSearch)) {
                    highlightedNodes.add(node);
                }
            }
        }
        repaint();
    }

    public void setLayoutType(boolean hierarchical) {
        isHierarchicalLayout = hierarchical;
        updateLayout();
    }

    private void updateLayout() {
        if (isHierarchicalLayout) {
            hierarchicalLayout.layout(nodes);
        } else {
            forceLayout.layout(nodes);
        }
        repaint();
    }

    public void refresh() {
        updateLayout();
    }

    public void fitToScreen() {
        if (nodes.isEmpty()) return;
        
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
        
        for (ModuleNode node : nodes) {
            minX = Math.min(minX, node.getX());
            minY = Math.min(minY, node.getY());
            maxX = Math.max(maxX, node.getX());
            maxY = Math.max(maxY, node.getY());
        }
        
        double width = maxX - minX;
        double height = maxY - minY;
        
        if (width == 0 || height == 0) return;
        
        double scaleX = getWidth() / (width + 100);
        double scaleY = getHeight() / (height + 100);
        scale = Math.min(scaleX, scaleY);
        
        translateX = -minX * scale + 50;
        translateY = -minY * scale + 50;
        
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        AffineTransform transform = new AffineTransform();
        transform.translate(translateX, translateY);
        transform.scale(scale, scale);
        g2.transform(transform);
        
        // Draw edges
        g2.setStroke(new BasicStroke(1.0f));
        g2.setColor(JBColor.GRAY);
        for (ModuleNode node : nodes) {
            Point2D.Double from = new Point2D.Double(node.getX(), node.getY());
            for (String ref : node.getReferences()) {
                // Draw edges logic here
            }
        }
        
        // Draw nodes
        for (ModuleNode node : nodes) {
            if (highlightedNodes.contains(node)) {
                g2.setColor(JBColor.YELLOW);
            } else {
                g2.setColor(JBColor.background());
            }
            g2.fillOval((int)node.getX() - 5, (int)node.getY() - 5, 10, 10);
            g2.setColor(JBColor.foreground());
            g2.drawString(node.getName(), (int)node.getX() + 10, (int)node.getY() + 5);
        }
        
        g2.dispose();
    }

    public BufferedImage createImage() {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        paint(g2);
        g2.dispose();
        return image;
    }
} 