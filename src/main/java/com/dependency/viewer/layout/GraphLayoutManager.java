package com.dependency.viewer.layout;

import com.dependency.viewer.ModuleNode;
import java.awt.Rectangle;
import java.util.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import java.util.Collection;

public class GraphLayoutManager {
    private final Collection<ModuleNode> nodes;
    private static final int LAYER_VERTICAL_GAP = 100;
    private static final int NODE_HORIZONTAL_GAP = 50;
    
    public GraphLayoutManager(Collection<ModuleNode> nodes) {
        this.nodes = nodes;
    }
    
    public Map<ModuleNode, Rectangle> layout(Collection<ModuleNode> nodes, int width, int height) {
        Map<ModuleNode, Rectangle> positions = new HashMap<>();
        Map<ModuleNode, Integer> layers = assignLayers(nodes);
        Map<Integer, List<ModuleNode>> nodesByLayer = groupByLayers(nodes, layers);
        
        int maxLayer = layers.values().stream().max(Integer::compareTo).orElse(0);
        int layerHeight = height / (maxLayer + 2);
        
        for (Map.Entry<Integer, List<ModuleNode>> entry : nodesByLayer.entrySet()) {
            int layer = entry.getKey();
            List<ModuleNode> layerNodes = entry.getValue();
            int y = layer * layerHeight + LAYER_VERTICAL_GAP;
            
            layoutNodesInLayer(layerNodes, positions, width, y);
        }
        
        return positions;
    }
    
    private Map<ModuleNode, Integer> assignLayers(Collection<ModuleNode> nodes) {
        Map<ModuleNode, Integer> layers = new HashMap<>();
        Set<ModuleNode> visited = new HashSet<>();
        
        for (ModuleNode node : nodes) {
            if (!visited.contains(node)) {
                assignLayer(node, 0, layers, visited);
            }
        }
        
        return layers;
    }
    
    private void assignLayer(ModuleNode node, int layer, Map<ModuleNode, Integer> layers, Set<ModuleNode> visited) {
        visited.add(node);
        layers.put(node, Math.max(layer, layers.getOrDefault(node, 0)));
        
        for (String ref : node.getReferences()) {
            for (ModuleNode dep : nodes) {
                if (dep.getName().equals(ref)) {
                    if (!visited.contains(dep)) {
                        assignLayer(dep, layer + 1, layers, visited);
                    }
                    break;
                }
            }
        }
    }
    
    private Map<Integer, List<ModuleNode>> groupByLayers(Collection<ModuleNode> nodes, Map<ModuleNode, Integer> layers) {
        Map<Integer, List<ModuleNode>> nodesByLayer = new HashMap<>();
        
        for (ModuleNode node : nodes) {
            int layer = layers.get(node);
            nodesByLayer.computeIfAbsent(layer, k -> new ArrayList<>()).add(node);
        }
        
        return nodesByLayer;
    }
    
    private void layoutNodesInLayer(List<ModuleNode> nodes, Map<ModuleNode, Rectangle> positions, int width, int y) {
        int nodeWidth = (width - (nodes.size() + 1) * NODE_HORIZONTAL_GAP) / nodes.size();
        nodeWidth = Math.min(nodeWidth, 200);
        int x = NODE_HORIZONTAL_GAP;
        
        for (ModuleNode node : nodes) {
            positions.put(node, new Rectangle(x, y, nodeWidth, 40));
            x += nodeWidth + NODE_HORIZONTAL_GAP;
        }
    }
    
    private void addEdges(Graph<ModuleNode, DefaultEdge> graph) {
        for (ModuleNode node : nodes) {
            for (String ref : node.getReferences()) {
                for (ModuleNode target : nodes) {
                    if (target.getName().equals(ref)) {
                        graph.addEdge(node, target);
                        break;
                    }
                }
            }
        }
    }
} 