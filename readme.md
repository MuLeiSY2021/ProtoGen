# 项目名称 ProtoGenerator

## 用法
```bash
<main class> [options]
```

### 选项说明

- `-c, --class`
    - 指定需要解析的类名。

- `-j, --jar`
    - 指定需要导入的 JAR 包路径。对于大文件，建议拆分成多个包形式导入依赖。
    - 也可以指定一个文件夹路径，批量导入该文件夹内的所有 JAR 包。
    - 注意：在处理大型 JAR 包时，工具的性能可能会受限，推荐使用多包形式以提高性能。

- `-o, --output`
    - 指定输出的 proto 文件位置。

### 注意事项
- 对于大文件的 JAR 包，工具在处理时可能表现不佳，强烈建议将大型 JAR 包拆分为多个小包进行导入。
- 输出的 proto 文件将根据指定路径生成，确保输出目录可写。
- 执行jar包所需的jdk版本必须和需要导入的项目文件的jdk版本一致
