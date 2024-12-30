package com.dependency.viewer.layout;

import com.dependency.viewer.ModuleNode;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ForceDirectedLayout {
    private static final double REPULSION = 500;
    private static final double ATTRACTION = 0.1;
    private static final int ITERATIONS = 100;
    private static final Random RANDOM = new Random();

    public void layout(Collection<ModuleNode> nodes) {
        if (nodes.isEmpty()) return;

        // Initialize positions randomly if not set
        for (ModuleNode node : nodes) {
            if (node.getX() == 0 && node.getY() == 0) {
                node.setPosition(RANDOM.nextDouble() * 1000, RANDOM.nextDouble() * 1000);
            }
        }

        // Create graph
        Graph<ModuleNode, DefaultEdge> graph = createGraph(nodes);

        // Run force-directed algorithm
        for (int i = 0; i < ITERATIONS; i++) {
            Map<ModuleNode, double[]> forces = new HashMap<>();
            
            // Initialize forces
            for (ModuleNode node : nodes) {
                forces.put(node, new double[2]);
            }

            // Calculate repulsion forces
            for (ModuleNode node1 : nodes) {
                for (ModuleNode node2 : nodes) {
                    if (node1 != node2) {
                        calculateRepulsion(node1, node2, forces);
                    }
                }
            }

            // Calculate attraction forces
            for (DefaultEdge edge : graph.edgeSet()) {
                ModuleNode source = graph.getEdgeSource(edge);
                ModuleNode target = graph.getEdgeTarget(edge);
                calculateAttraction(source, target, forces);
            }

            // Apply forces
            for (ModuleNode node : nodes) {
                double[] force = forces.get(node);
                node.setPosition(
                    node.getX() + force[0],
                    node.getY() + force[1]
                );
            }
        }
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

    private void calculateRepulsion(ModuleNode node1, ModuleNode node2, Map<ModuleNode, double[]> forces) {
        double dx = node2.getX() - node1.getX();
        double dy = node2.getY() - node1.getY();
        double distance = Math.sqrt(dx * dx + dy * dy) + 0.1;
        double force = REPULSION / (distance * distance);

        double fx = (dx / distance) * force;
        double fy = (dy / distance) * force;

        forces.get(node1)[0] -= fx;
        forces.get(node1)[1] -= fy;
        forces.get(node2)[0] += fx;
        forces.get(node2)[1] += fy;
    }

    private void calculateAttraction(ModuleNode node1, ModuleNode node2, Map<ModuleNode, double[]> forces) {
        double dx = node2.getX() - node1.getX();
        double dy = node2.getY() - node1.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        double fx = dx * ATTRACTION;
        double fy = dy * ATTRACTION;

        forces.get(node1)[0] += fx;
        forces.get(node1)[1] += fy;
        forces.get(node2)[0] -= fx;
        forces.get(node2)[1] -= fy;
    }
} 