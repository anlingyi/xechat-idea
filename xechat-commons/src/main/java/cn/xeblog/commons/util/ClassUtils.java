package cn.xeblog.commons.util;

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
        Set<Class<?>> classes = new HashSet<>();
        String pkgPath = null;
        if (StrUtil.isNotBlank(pkg)) {
            pkgPath = pkg.replace(".", "/");
        }

        try {
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
                        if (annotationClass != null && !clazz.isAnnotationPresent(annotationClass)) {
                            continue;
                        }

                        classes.add(clazz);
                    } catch (Throwable e) {
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

}
