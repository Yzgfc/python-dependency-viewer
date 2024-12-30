package com.dependency.viewer.layout;

import com.dependency.viewer.ModuleNode;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.*;

public class HierarchicalLayout {
    private static final double VERTICAL_SPACING = 100;
    private static final double HORIZONTAL_SPACING = 150;

    public void layout(Collection<ModuleNode> nodes) {
        if (nodes.isEmpty()) return;

        // Create graph
        Graph<ModuleNode, DefaultEdge> graph = createGraph(nodes);

        // Get layers through topological sort
        Map<ModuleNode, Integer> layers = assignLayers(graph);

        // Calculate positions
        Map<Integer, List<ModuleNode>> nodesByLayer = groupByLayers(layers);
        assignPositions(nodesByLayer);
    }

    private Graph<ModuleNode, DefaultEdge> createGraph(Collection<ModuleNode> nodes) {
        Graph<ModuleNode, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        
        // Add vertices
        for (ModuleNode node : nodes) {
            graph.addVertex(node);
        }

        // Add edges
        for (ModuleNode source : nodes) {
            for (String ref : source.getReferences()) {
                for (ModuleNode target : nodes) {
                    if (ref.contains(target.getName())) {
                        graph.addEdge(source, target);
                    }
                }
            }
        }

        return graph;
    }

    private Map<ModuleNode, Integer> assignLayers(Graph<ModuleNode, DefaultEdge> graph) {
        Map<ModuleNode, Integer> layers = new HashMap<>();
        TopologicalOrderIterator<ModuleNode, DefaultEdge> iterator = 
            new TopologicalOrderIterator<>(graph);

        while (iterator.hasNext()) {
            ModuleNode node = iterator.next();
            int maxPredecessorLayer = -1;
            
            for (DefaultEdge edge : graph.incomingEdgesOf(node)) {
                ModuleNode source = graph.getEdgeSource(edge);
                maxPredecessorLayer = Math.max(maxPredecessorLayer, 
                    layers.getOrDefault(source, 0));
            }
            
            layers.put(node, maxPredecessorLayer + 1);
        }

        return layers;
    }

    private Map<Integer, List<ModuleNode>> groupByLayers(Map<ModuleNode, Integer> layers) {
        Map<Integer, List<ModuleNode>> nodesByLayer = new HashMap<>();
        
        for (Map.Entry<ModuleNode, Integer> entry : layers.entrySet()) {
            nodesByLayer.computeIfAbsent(entry.getValue(), k -> new ArrayList<>())
                       .add(entry.getKey());
        }

        return nodesByLayer;
    }

    private void assignPositions(Map<Integer, List<ModuleNode>> nodesByLayer) {
        int maxNodesInLayer = nodesByLayer.values().stream()
            .mapToInt(List::size)
            .max()
            .orElse(1);

        for (Map.Entry<Integer, List<ModuleNode>> entry : nodesByLayer.entrySet()) {
            int layer = entry.getKey();
            List<ModuleNode> layerNodes = entry.getValue();
            
            double y = layer * VERTICAL_SPACING;
            double totalWidth = (layerNodes.size() - 1) * HORIZONTAL_SPACING;
            double startX = -(totalWidth / 2);

            for (int i = 0; i < layerNodes.size(); i++) {
                ModuleNode node = layerNodes.get(i);
                node.setPosition(startX + (i * HORIZONTAL_SPACING), y);
            }
        }
    }
} 