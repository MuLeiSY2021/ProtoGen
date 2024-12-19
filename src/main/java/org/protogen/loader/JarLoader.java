package org.protogen.loader;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarLoader {
    public static URL[] getAllJarURLs(String jarPath) throws IOException, ClassNotFoundException {
        // 确保 JAR 文件或目录存在
        File jarFile = new File(jarPath);
        ArrayList<URL> urls = new ArrayList<>();
        // 如果是单个 JAR 文件
        if (jarFile.getName().endsWith(".jar")) {
            System.out.println("单个 JAR 文件: " + jarFile.getAbsolutePath());
            urls.add(new URL(jarFile.toURI() + "!/"));
        } else if (jarFile.isDirectory()) {
            // 如果是目录，递归查找所有 JAR 文件
            System.out.println("目录路径: " + jarFile.getAbsolutePath());
            LinkedList<File> otherDirs = new LinkedList<>();
            otherDirs.add(jarFile);
            LinkedList<File> tmpDir = new LinkedList<>();

            // 递归查找所有 JAR 文件
            while (!otherDirs.isEmpty()) {
                System.out.println("当前正在处理的目录: " + otherDirs.size() + " 个");
                for (File dir : otherDirs) {
                    for (File file : dir.listFiles()) {
                        if (file.getName().endsWith(".jar")) {
                            System.out.println("找到 JAR 文件: " + file.getAbsolutePath());
                            urls.add(new URL(file.toURI().toString()));
                        } else if (file.isDirectory()) {
                            System.out.println("进入子目录: " + file.getAbsolutePath());
                            tmpDir.add(file);
                        }
                    }
                }
                // 继续递归处理子目录
                otherDirs = tmpDir;
                tmpDir = new LinkedList<>();
            }
        } else {
            throw new IllegalArgumentException("无效的 JAR 文件或目录: " + jarPath);
        }
        return urls.toArray(new URL[]{});
    }

    /**
     * 递归加载 JAR 包内的所有类到当前程序中
     *
     * @param jarPath JAR 文件路径
     * @throws IOException              如果读取 JAR 文件失败
     * @throws ClassNotFoundException   如果类加载失败
     */
    public static void loadAllJarClazz(String jarPath) throws IOException, ClassNotFoundException {
        // 确保 JAR 文件或目录存在
        File jarFile = new File(jarPath);
        ArrayList<URL> urls = new ArrayList<>();
        ArrayList<File> allFiles = new ArrayList<>();
        // 如果是单个 JAR 文件
        if (jarFile.getName().endsWith(".jar")) {
            System.out.println("单个 JAR 文件: " + jarFile.getAbsolutePath());
            urls.add(new URL(jarFile.toURI() + "!/"));
            allFiles.add(jarFile);
        } else if (jarFile.isDirectory()) {
            // 如果是目录，递归查找所有 JAR 文件
            System.out.println("目录路径: " + jarFile.getAbsolutePath());
            LinkedList<File> otherDirs = new LinkedList<>();
            otherDirs.add(jarFile);
            LinkedList<File> tmpDir = new LinkedList<>();

            // 递归查找所有 JAR 文件
            while (!otherDirs.isEmpty()) {
                System.out.println("当前正在处理的目录: " + otherDirs.size() + " 个");
                for (File dir : otherDirs) {
                    for (File file : dir.listFiles()) {
                        if (file.getName().endsWith(".jar")) {
                            System.out.println("找到 JAR 文件: " + file.getAbsolutePath());
                            urls.add(new URL(file.toURI().toString()));
                            allFiles.add(file);

                        } else if (file.isDirectory()) {
                            System.out.println("进入子目录: " + file.getAbsolutePath());
                            tmpDir.add(file);
                        }
                    }
                }
                // 继续递归处理子目录
                otherDirs = tmpDir;
                tmpDir = new LinkedList<>();
            }
        } else {
            throw new IllegalArgumentException("无效的 JAR 文件或目录: " + jarPath);
        }

        loadAllClazz(urls.toArray(new URL[]{}), allFiles.toArray(new File[]{}));
    }

    public static void loadAllClazz(URL[] urls,File[] files) throws IOException, ClassNotFoundException {
        // 创建 URLClassLoader
        try (URLClassLoader classLoader = new URLClassLoader(urls)) {
            // 读取 JAR 文件
            for (File file : files) {
                try (JarFile jarFile = new JarFile(file)){
                    Enumeration<JarEntry> entryEnumeration = jarFile.entries();
                    while (entryEnumeration.hasMoreElements()) {
                        JarEntry entry = entryEnumeration.nextElement();
                        // 先获取类的名称，符合条件之后再做处理，避免处理不符合条件的类
                        String clazzName = entry.getName();
                        if (clazzName.endsWith(".class")) {
                            // 去掉文件名的后缀
                            clazzName = clazzName.substring(0, clazzName.length() - 6);
                            // 替换分隔符
                            clazzName = clazzName.replace("/", ".");
                            //跳过 module-info.class类，因为它是模块定义文件，不是 Java 类。
                            if(clazzName.equals("module-info")) {
                                continue;
                            }
                            // 加载类,如果失败直接跳过
                            try {
                                classLoader.loadClass(clazzName);


                            } catch (Exception | Error e) {
                                System.err.println(e.getMessage());
                            }

                        }
                    }
                }

            }
        }
    }


}
