package cn.xeblog.commons.util;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author anlingyi
 * @date 2021/10/3 3:05 下午
 */
public class ClassUtils {

    public static Set<Class<?>> scan(String path) {
        return scan(path, null);
    }

    public static Set<Class<?>> scan(String path, String pkg) {
        return scan(path, pkg, null);
    }

    public static Set<Class<?>> scan(String path, String pkg, Class<? extends Annotation> annotationClass) {
        return scan(path, pkg, annotationClass, null);
    }

    public static Set<Class<?>> scanSubClass(String path, String pkg, Class superClass) {
        return scan(path, pkg, null, superClass);
    }

    public static Set<Class<?>> scan(String path, String pkg, Class<? extends Annotation> annotationClass, Class superClass) {
        Set<Class<?>> classes = new HashSet<>();
        String pkgPath = null;
        if (StrUtil.isNotBlank(pkg)) {
            pkgPath = pkg.replace(".", "/");
        }

        try {
            if (path == null) {
                classes = ClassUtil.scanPackage(pkg, (clazz) -> {
                    if (!isAnnotationPresent(clazz, annotationClass)) {
                        return false;
                    }
                    if (!isSubClass(clazz, superClass)) {
                        return false;
                    }

                    return true;
                });
            } else {
                JarFile jar = new JarFile(path);
                Enumeration<JarEntry> entryEnumeration = jar.entries();
                while (entryEnumeration.hasMoreElements()) {
                    JarEntry entry = entryEnumeration.nextElement();
                    String clazzName = entry.getName();
                    if (pkgPath != null && !clazzName.startsWith(pkgPath)) {
                        continue;
                    }

                    if (clazzName.endsWith(".class")) {
                        clazzName = clazzName.substring(0, clazzName.length() - 6);
                        clazzName = clazzName.replace("/", ".");
                        try {
                            Class<?> clazz = Class.forName(clazzName);
                            if (!isAnnotationPresent(clazz, annotationClass)) {
                                continue;
                            }
                            if (!isSubClass(clazz, superClass)) {
                                continue;
                            }

                            classes.add(clazz);
                        } catch (Throwable e) {
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    public static boolean isAnnotationPresent(Class clazz, Class<? extends Annotation> annotationClass) {
        if (clazz == null || annotationClass == null) {
            return true;
        }

        return clazz.isAnnotationPresent(annotationClass);
    }

    public static boolean isSubClass(Class clazz, Class superClass) {
        if (clazz == null || superClass == null) {
            return true;
        }

        return superClass.isAssignableFrom(clazz) && !superClass.equals(clazz);
    }

}
