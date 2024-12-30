package com.dependency.viewer.layout;

import com.dependency.viewer.ModuleNode;
import java.awt.Rectangle;
import java.awt.Point;
import java.util.*;

public class ForceDirectedLayout {
    private static final double REPULSION = 50000;
    private static final double ATTRACTION = 0.005;
    private static final double DAMPING = 0.95;
    private static final int ITERATIONS = 100;
    private static final int NODE_WIDTH = 120;
    private static final int NODE_HEIGHT = 40;
    
    private class Node {
        ModuleNode module;
        double x, y;
        double dx, dy;
        
        Node(ModuleNode module) {
            this.module = module;
            this.x = Math.random() * 1000;
            this.y = Math.random() * 1000;
            this.dx = 0;
            this.dy = 0;
        }
    }
    
    public Map<ModuleNode, Rectangle> layout(Collection<ModuleNode> modules, int width, int height) {
        List<Node> nodes = modules.stream()
            .map(Node::new)
            .collect(Collectors.toList());
        
        // 运行力导向算法
        for (int i = 0; i < ITERATIONS; i++) {
            calculateForces(nodes);
            updatePositions(nodes);
        }
        
        // 归一化坐标
        normalizePositions(nodes, width, height);
        
        // 转换为矩形坐标
        Map<ModuleNode, Rectangle> positions = new HashMap<>();
        for (Node node : nodes) {
            positions.put(node.module, new Rectangle(
                (int) node.x,
                (int) node.y,
                NODE_WIDTH,
                NODE_HEIGHT
            ));
        }
        
        return positions;
    }
    
    private void calculateForces(List<Node> nodes) {
        // 重置力
        for (Node node : nodes) {
            node.dx = 0;
            node.dy = 0;
        }
        
        // 计算排斥力
        for (int i = 0; i < nodes.size(); i++) {
            Node n1 = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                Node n2 = nodes.get(j);
                double dx = n2.x - n1.x;
                double dy = n2.y - n1.y;
                double distance = Math.sqrt(dx * dx + dy * dy) + 0.1;
                double force = REPULSION / (distance * distance);
                
                double fx = (dx / distance) * force;
                double fy = (dy / distance) * force;
                
                n1.dx -= fx;
                n1.dy -= fy;
                n2.dx += fx;
                n2.dy += fy;
            }
        }
        
        // 计算吸引力
        for (Node node : nodes) {
            for (ModuleNode dep : node.module.getDependencies()) {
                Node target = nodes.stream()
                    .filter(n -> n.module == dep)
                    .findFirst()
                    .orElse(null);
                
                if (target != null) {
                    double dx = target.x - node.x;
                    double dy = target.y - node.y;
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    
                    node.dx += dx * ATTRACTION;
                    node.dy += dy * ATTRACTION;
                    target.dx -= dx * ATTRACTION;
                    target.dy -= dy * ATTRACTION;
                }
            }
        }
    }
    
    private void updatePositions(List<Node> nodes) {
        for (Node node : nodes) {
            node.dx *= DAMPING;
            node.dy *= DAMPING;
            
            node.x += node.dx;
            node.y += node.dy;
        }
    }
    
    private void normalizePositions(List<Node> nodes, int width, int height) {
        double minX = nodes.stream().mapToDouble(n -> n.x).min().orElse(0);
        double maxX = nodes.stream().mapToDouble(n -> n.x).max().orElse(width);
        double minY = nodes.stream().mapToDouble(n -> n.y).min().orElse(0);
        double maxY = nodes.stream().mapToDouble(n -> n.y).max().orElse(height);
        
        double scaleX = (width - NODE_WIDTH * 2) / (maxX - minX);
        double scaleY = (height - NODE_HEIGHT * 2) / (maxY - minY);
        double scale = Math.min(scaleX, scaleY);
        
        for (Node node : nodes) {
            node.x = (node.x - minX) * scale + NODE_WIDTH;
            node.y = (node.y - minY) * scale + NODE_HEIGHT;
        }
    }
} 