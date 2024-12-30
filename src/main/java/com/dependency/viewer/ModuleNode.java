package com.dependency.viewer;

import java.util.ArrayList;
import java.util.List;

public class ModuleNode {
    private final String moduleName;
    private final String filePath;
    private final List<ModuleNode> dependencies;
    private final List<String> references;
    
    public ModuleNode(String moduleName, String filePath) {
        this.moduleName = moduleName;
        this.filePath = filePath;
        this.dependencies = new ArrayList<>();
        this.references = new ArrayList<>();
    }
    
    public void addDependency(ModuleNode node) {
        if (!dependencies.contains(node)) {
            dependencies.add(node);
        }
    }
    
    public void addReference(String reference) {
        references.add(reference);
    }
    
    // Getters
    public String getModuleName() { return moduleName; }
    public String getFilePath() { return filePath; }
    public List<ModuleNode> getDependencies() { return dependencies; }
    public List<String> getReferences() { return references; }
} 