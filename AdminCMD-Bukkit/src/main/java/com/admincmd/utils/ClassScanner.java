package com.admincmd.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassScanner {

    public static Set<Class<?>> getClassesFromJarWithAnnotation(@NotNull String basePackage, @NotNull Class<? extends Annotation> annotation) {
        Set<Class<?>> classes = new HashSet<>();
        for (Class<?> clazz : getClasses(basePackage, ClassScanner.class)) {
            if (clazz.isAnnotationPresent(annotation)) classes.add(clazz);
        }
        return classes;
    }

    public static Set<Class<?>> getClassesFromJarWithAnnotation(@NotNull String basePackage, @NotNull Class<? extends Annotation> annotation, @NotNull Class<?> jarFileClass) {
        Set<Class<?>> classes = new HashSet<>();
        for (Class<?> clazz : getClasses(basePackage, jarFileClass)) {
            if (clazz.isAnnotationPresent(annotation)) classes.add(clazz);
        }
        return classes;
    }

    public static <T> Set<Class<? extends T>> getClassesThatExtendClass(@NotNull String basePackage, Class<T> ext) {
        Set<Class<? extends T>> classes = new HashSet<>();
        for (Class<?> clazz : getClasses(basePackage, ClassScanner.class)) {
            if (!ext.isAssignableFrom(clazz) || clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }

            @SuppressWarnings("unchecked") Class<? extends T> listenerClass = (Class<? extends T>) clazz;
            classes.add(listenerClass);
        }
        return classes;
    }

    public static <T> Set<Class<? extends T>> getClassesThatExtendClass(@NotNull String basePackage, Class<T> ext, @NotNull Class<?> jarFileClass) {
        Set<Class<? extends T>> classes = new HashSet<>();
        for (Class<?> clazz : getClasses(basePackage, jarFileClass)) {
            if (!ext.isAssignableFrom(clazz) || clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }

            @SuppressWarnings("unchecked") Class<? extends T> listenerClass = (Class<? extends T>) clazz;
            classes.add(listenerClass);
        }
        return classes;
    }

    private static Set<Class<?>> getClasses(@NotNull String basePackage, Class<?> jarFileClass) {
        Set<Class<?>> classes = new HashSet<>();
        String basePath = basePackage.replace('.', '/');

        try {
            File jarFile = new File(jarFileClass.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (!jarFile.getName().endsWith(".jar")) {
                System.err.println("Nicht aus einer JAR gestartet: " + jarFile);
                return classes;
            }

            try (JarFile jar = new JarFile(jarFile)) {
                Enumeration<JarEntry> entries = jar.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();

                    if (name.endsWith(".class") && name.startsWith(basePath) && !entry.isDirectory()) {
                        String className = name.replace('/', '.').replace(".class", "");
                        try {
                            Class<?> clazz = Class.forName(className, false, ClassScanner.class.getClassLoader());
                            classes.add(clazz);
                        } catch (ClassNotFoundException ignored) {
                        }
                    }
                }
            }
        } catch (Exception e) {
            ACLogger.severe(e);
        }

        return classes;
    }
}
