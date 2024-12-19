package org.protogen.naming;

public class Specification {

    // 将驼峰命名转换为下划线命名
    public static String toSnakeCase(String fieldName) {
        StringBuilder result = new StringBuilder();
        char[] cs = fieldName.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            char c = cs[i];
            if (Character.isUpperCase(c) && !(i != 0 && Character.isUpperCase(cs[i-1]))) {
                result.append("_").append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    // 将类名转换为驼峰命名
    public static String toCamelCase(String className,boolean fstBig) {
        if (className == null || className.isEmpty()) return className;
        StringBuilder result = new StringBuilder(className);
        if(fstBig) result.setCharAt(0, Character.toLowerCase(result.charAt(0)));
        return result.toString();
    }

    // 将类名转换为驼峰命名
    public static String toMessageName(String className) {
        return toCamelCase(className,false);
    }
}
