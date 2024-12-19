package org.protogen.type;

import org.protogen.Generator;
import org.protogen.naming.Specification;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class TypePrefix {
    public static void prefix(FileWriter writer, Class<?> fieldType, Field field) throws IOException {
        any2ProtoType(writer,fieldType,field);
    }

    private static void any2ProtoType(FileWriter writer, Class<?> fieldType, Field field) throws IOException {
        if (Map.class.isAssignableFrom(fieldType)) {
            map2ProtoType(writer, fieldType, field);
        }else if (fieldType.isArray() || Collection.class.isAssignableFrom(fieldType)) {
            arr2ProtoType(writer, fieldType,field);
        } else {
            basic2ProtoField(writer, fieldType);
        }
    }

    private static void arr2ProtoType(FileWriter writer, Class<?> fieldType, Field field) throws IOException {
        String protoFieldType = TypeProto.arr2ProtoArr(fieldType);
        System.out.print("数组类型前缀: " + protoFieldType + " "); // 输出生成的 Proto 字段

        writer.write(protoFieldType + " ");
        Class<?> innerType = null;
        if(fieldType.isArray()) {
            innerType = fieldType.getComponentType();
        } else {
            // 获取泛型参数的类型
            innerType = (Class<?>) ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
        }
        if(!TypeProto.isByteType(innerType)) {
            any2ProtoType(writer, innerType,field);
        }
    }

    private static void map2ProtoType(FileWriter writer, Class<?> fieldType, Field field) throws IOException {
        String protoFieldType = TypeProto.map2ProtoMap(fieldType);
        System.out.print("Map 类型前缀: " + protoFieldType + " ");

        // 获取 Map 的 class 对象
        Type genericSuperclass = field.getGenericType();

        // 判断是否为参数化类型（即 Map<K, V>）
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;

            // 获取类型参数
            Type[] types = parameterizedType.getActualTypeArguments();

            writer.write(protoFieldType + "<");
            // 获取泛型类型的泛型参数
            Type keyType = types[0];
            any2ProtoType(writer, (Class<?>) keyType,field);

            writer.write(",");

            Type valueType = types[1].getClass();
            any2ProtoType(writer, (Class<?>) keyType,field);

            writer.write(">");
        }
    }


    private static void basic2ProtoField(FileWriter writer, Class<?> fieldType) throws IOException {
        String protoFieldType;
        // 如果是基础类型，直接生成 Proto 字段
        if (TypeProto.isBasicType(fieldType)) {
            protoFieldType = TypeProto.basicT2ProtoT(fieldType);
            System.out.print("基础类型字段: " + protoFieldType + " "); // 输出生成的 Proto 字段
        } else {
            // 如果是类类型，生成 Proto 字段并将类放入待解析队列
            protoFieldType = Specification.toMessageName(fieldType.getSimpleName());
            System.out.print("类类型字段: " + protoFieldType + " "); // 输出生成的 Proto 字段

            // 将类类型放入待解析队列
            Generator.addClassProto(fieldType);
        }
        writer.write(protoFieldType + " ");

    }
}
