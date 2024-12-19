package org.protogen.type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TypeProto {

    private static final Set<Class<?>> BYTE_TYPES = new HashSet<>();
    static {
        BYTE_TYPES.add(Byte.class);
        BYTE_TYPES.add(byte.class);
    }
    public static boolean isByteType(Class<?> type) {
        return BYTE_TYPES.contains(type);
    }
    private static final Set<Class<?>> BASIC_TYPES = new HashSet<>();

    static {
        BASIC_TYPES.add(String.class);
        BASIC_TYPES.add(Integer.class);
        BASIC_TYPES.add(int.class);
        BASIC_TYPES.add(Long.class);
        BASIC_TYPES.add(long.class);
        BASIC_TYPES.add(Boolean.class);
        BASIC_TYPES.add(boolean.class);
        BASIC_TYPES.add(Double.class);
        BASIC_TYPES.add(double.class);
    }

    // 判断是否为基础类型
    public static boolean isBasicType(Class<?> type) {
        return BASIC_TYPES.contains(type);
    }

    private static final Map<Class<?>, String> TYPE_MAP = new HashMap<>();

    static {
        // 初始化类型映射
        TYPE_MAP.put(String.class, "string");
        TYPE_MAP.put(Integer.class, "int32");
        TYPE_MAP.put(int.class, "int32");

        TYPE_MAP.put(Long.class, "int64");
        TYPE_MAP.put(long.class, "int64");

        TYPE_MAP.put(Boolean.class, "bool");
        TYPE_MAP.put(boolean.class, "bool");

        TYPE_MAP.put(Double.class, "double");
        TYPE_MAP.put(double.class, "double");

        TYPE_MAP.put(Float.class, "float");
        TYPE_MAP.put(float.class, "float");
    }

    public static String basicT2ProtoT(Class<?> javaType) {
        // 直接查找映射，若没有找到返回默认值
        String protoType = TYPE_MAP.get(javaType);

        if (protoType == null) {
            System.out.println("WARN 不支持的类型: " + javaType);
            return "string"; // 或者返回 null, 或者抛出异常
        }

        return protoType;
    }

    // 处理数组类型映射到 Proto 类型的 repeated
    public static String arr2ProtoArr(Class<?> javaType) {
        // 获取数组元素类型
        Class<?> componentType = javaType.getComponentType();

        // 处理 Byte 数组类型
        if(isByteType(componentType)) {
            return "bytes";
        }

        // 返回相应的 Proto repeated 类型
        return "repeated";
    }

    public static String map2ProtoMap(Class<?> fieldType) {
        return "map";
    }
}
