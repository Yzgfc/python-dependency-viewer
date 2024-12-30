package com.dependency.viewer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.jetbrains.python.psi.*;

import java.util.*;

public class DependencyAnalyzer {
    private final Project project;
    private final Map<String, ModuleNode> moduleNodes;
    
    public DependencyAnalyzer(Project project) {
        this.project = project;
        this.moduleNodes = new HashMap<>();
    }
    
    public Map<String, ModuleNode> analyzeDependencies() {
        moduleNodes.clear();
        
        // 获取项目中所有的 Python 文件
        Collection<VirtualFile> pythonFiles = FilenameIndex.getFilesByExtension(project, "py", GlobalSearchScope.projectScope(project));
        
        for (VirtualFile file : pythonFiles) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
            if (psiFile instanceof PyFile) {
                analyzePyFile((PyFile) psiFile);
            }
        }
        
        return moduleNodes;
    }
    
    private void analyzePyFile(PyFile pyFile) {
        String moduleName = pyFile.getName().replace(".py", "");
        ModuleNode moduleNode = new ModuleNode(moduleName, pyFile.getVirtualFile().getPath());
        moduleNodes.put(moduleName, moduleNode);
        
        // 分析导入语句
        for (PyFromImportStatement importStatement : pyFile.getFromImports()) {
            String importedFrom = importStatement.getImportSourceQName().toString();
            if (importedFrom != null) {
                ModuleNode dependency = moduleNodes.computeIfAbsent(
                    importedFrom,
                    k -> new ModuleNode(importedFrom, "")
                );
                moduleNode.addReference(dependency.getPath());
            }
        }
        
        for (PyImportStatement importStatement : pyFile.getImports()) {
            for (PyImportElement element : importStatement.getImportElements()) {
                String importedName = element.getVisibleName();
                if (importedName != null) {
                    ModuleNode dependency = moduleNodes.computeIfAbsent(
                        importedName,
                        k -> new ModuleNode(importedName, "")
                    );
                    moduleNode.addReference(dependency.getPath());
                }
            }
        }
    }
} 