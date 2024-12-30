package com.dependency.viewer.analysis;

import com.dependency.viewer.ModuleNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.jetbrains.python.psi.*;
import com.jetbrains.python.psi.resolve.PyResolveContext;

import java.util.*;

public class PythonDependencyAnalyzer {
    private final Project project;
    private final PsiManager psiManager;
    
    public PythonDependencyAnalyzer(Project project) {
        this.project = project;
        this.psiManager = PsiManager.getInstance(project);
    }
    
    public ModuleNode analyzeFile(VirtualFile file) {
        PyFile pyFile = (PyFile) psiManager.findFile(file);
        if (pyFile == null) return null;
        
        String moduleName = file.getNameWithoutExtension();
        ModuleNode moduleNode = new ModuleNode(moduleName, file.getPath());
        
        analyzeImports(pyFile, moduleNode);
        analyzeFromImports(pyFile, moduleNode);
        
        return moduleNode;
    }
    
    private void analyzeImports(PyFile pyFile, ModuleNode moduleNode) {
        for (PyImportStatement importStatement : pyFile.getImportStatements()) {
            for (PyImportElement element : importStatement.getImportElements()) {
                PyReferenceExpression reference = element.getImportReference();
                if (reference != null) {
                    String refText = reference.getText();
                    moduleNode.addReference(importStatement.getTextOffset() + ":" + refText);
                }
            }
        }
    }
    
    private void analyzeFromImports(PyFile pyFile, ModuleNode moduleNode) {
        for (PyFromImportStatement fromImport : pyFile.getFromImports()) {
            String sourceName = fromImport.getImportSourceQName();
            if (sourceName != null) {
                moduleNode.addReference(fromImport.getTextOffset() + ":" + fromImport.getText());
            }
        }
    }
} 