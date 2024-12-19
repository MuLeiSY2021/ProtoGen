package org.protogen.cmd;

import com.beust.jcommander.Parameter;
import lombok.Getter;

@Getter
public class CommandLineOptions {

    @Parameter(names = {"-j","--jar"}, description = "指定需要导入的 JAR 包路径 (大文件需要拆成分包形式导入依赖)", required = true)
    private String jarPath;

    @Parameter(names = {"-c","--class"}, description = "指定需要解析的类名", required = true)
    private String className;

    @Parameter(names = {"-o","--output"}, description = "指定输出proto文件的位置")
    private String protoPath;


}

