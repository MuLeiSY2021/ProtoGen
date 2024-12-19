package org.protogen.cmd;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.protogen.loader.JarLoader;
import org.protogen.Generator;

import java.net.URLClassLoader;

public class Main {

    public static void main(String[] args) {
        CommandLineOptions options = new CommandLineOptions();
        JCommander commander = JCommander.newBuilder()
                .addObject(options)
                .build();

        try {
            // 解析命令行参数
            commander.parse(args);

            // 获取参数值
            String jarPath = options.getJarPath();
            String className = options.getClassName();
            String outputPath = options.getProtoPath() == null ? "./" : options.getProtoPath();

            // 动态加载 JAR 包并获取类
            try {
//                JarLoader.loadAllJarClazz(jarPath);
                Class<?> clazz;

                try(URLClassLoader classLoader = new URLClassLoader(JarLoader.getAllJarURLs(jarPath));) {
                    // 生成 Proto 代码
                    try {
                        clazz = classLoader.loadClass(className);
                        System.out.println("\n正在生成Proto代码\n");
                        Generator.generateMessage(clazz, outputPath);
                    } catch (ClassNotFoundException e) {
                        System.err.println("ERROR:未找到类: " + className);
                        return;
                    }
                }

                // 输出最终生成的 Proto 代码
                System.out.println("\nINFO:Proto 代码已生成: " + outputPath + clazz.getSimpleName() + ".proto\n");


            } catch (Exception e) {
                System.err.println("FATAL:" + e.getMessage());
                e.printStackTrace();
            }

        } catch (ParameterException e) {
            // 错误处理：显示用法信息
            System.err.println("FATAL:参数错误: " + e.getMessage());
            commander.usage();
        }
    }
}