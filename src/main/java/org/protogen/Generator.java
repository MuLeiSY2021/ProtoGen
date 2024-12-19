package org.protogen;

import org.protogen.naming.Specification;
import org.protogen.type.TypePrefix;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class Generator {

    // 用于存放待解析的类
    private static Set<Class<?>> classMap = new HashSet<>();


    private static Set<Class<?>> classProtoMap = new HashSet<>();

    private static Set<Class<?>> tmpMap = new HashSet<>();

    // 递归生成 Proto 代码
    public static void generateMessage(Class<?> clazz, String outputPath) throws IOException {
        StringBuilder className = new StringBuilder(clazz.getSimpleName());
        File file = new File(outputPath+ className);
        int r = 1;
        while (file.exists()) {
            className.append("_").append(r++);
            file = new File(outputPath + className);
        }
        try(FileWriter writer = new FileWriter(outputPath + className + ".proto")) {


            writer.write("syntax = \"proto3\";\n\n");

            classProtoMap.add(clazz);
            generateMessage(writer);
            writer.flush();
        } catch (Exception e) {
            file.delete();
            throw e;
        }
    }

    public static void generateMessage(FileWriter writer) throws IOException {
        while (!classProtoMap.isEmpty()) {

            // 遍历待解析的类
            for (Class<?> clazz :classProtoMap) {
                int fieldIndex = 1; // Proto 字段编号从 1 开始

                // 类名前面的小写
                String messageName = Specification.toMessageName(clazz.getSimpleName());

                System.out.println("开始解析类: " + clazz.getName()); // 输出当前正在解析的类
                writer.write("message " + messageName + " {\n");

                // 解析字段
                for (Field field : clazz.getDeclaredFields()) {
                    // 跳过静态字段
                    if (Modifier.isStatic(field.getModifiers())) continue;

                    String fieldName = field.getName();
                    String protoFieldName = Specification.toSnakeCase(fieldName); // 转换为蛇形命名
                    Class<?> fieldType = field.getType(); // 获取字段类型的简单名称
                    System.out.println("字段名: " + fieldName + ", 类型: " + fieldType); // 输出字段名和类型

                    writer.write("    ");
                    TypePrefix.prefix(writer,fieldType, field);
                    writer.write(protoFieldName + " = " + fieldIndex + ";\n");
                    System.out.println(protoFieldName + " = " + fieldIndex); // 输出生成的 Proto 字段

                    fieldIndex++;
                }

                writer.write("}\n");

            }

            // 更新待解析的类队列
            classProtoMap = tmpMap;
            tmpMap = new HashSet<>();
            System.out.println("待解析的类队列已更新，当前待解析类数: " + classProtoMap.size()); // 输出待解析队列大小
        }
    }


    public static void addClassProto(Class<?> clazz) {
        if(!classMap.contains(clazz)) Generator.tmpMap.add(clazz);
        if(!classMap.contains(clazz)) Generator.classMap.add(clazz);

    }
}
