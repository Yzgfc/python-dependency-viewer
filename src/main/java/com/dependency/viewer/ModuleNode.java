package com.dependency.viewer;

import java.util.HashSet;
import java.util.Set;

public class ModuleNode {
    private final String name;
    private final String path;
    private final Set<String> references;
    private double x;
    private double y;

    public ModuleNode(String name, String path) {
        this.name = name;
        this.path = path;
        this.references = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void addReference(String reference) {
        references.add(reference);
    }

    public Set<String> getReferences() {
        return new HashSet<>(references);
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleNode that = (ModuleNode) o;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }
} 