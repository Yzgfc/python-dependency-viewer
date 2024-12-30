#!/bin/bash

# 清理旧的构建文件
./gradlew clean

# 构建插件
./gradlew buildPlugin

# 复制构建的插件到项目根目录
cp build/distributions/python-dependency-viewer-*.zip ./

# 重命名为更友好的名称
mv python-dependency-viewer-*.zip python-dependency-viewer-latest.zip

echo "Plugin has been packaged successfully!" 