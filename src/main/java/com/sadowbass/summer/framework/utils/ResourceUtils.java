package com.sadowbass.summer.framework.utils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public abstract class ResourceUtils {

    private static final String DEFAULT_CLASS_PATH = "";
    public static final String ROOT_PATH;

    static {
        Enumeration<URL> resources = getUrlEnumeration(DEFAULT_CLASS_PATH);

        URL url = resources.nextElement();
        ROOT_PATH = url.getPath();
    }

    public static List<URL> getResource(String classPath) {
        Enumeration<URL> resources = getUrlEnumeration(createClassPath(classPath));

        List<URL> resourceList = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            if (url.getProtocol().equals("file")) {
                resourceList.add(url);
            }
        }

        return resourceList;
    }

    protected static String createClassPath(String classPath) {
        return classPath == null ? DEFAULT_CLASS_PATH : classPath;
    }

    private static Enumeration<URL> getUrlEnumeration(String classPath) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources;
        try {
            resources = classLoader.getResources(classPath);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        return resources;
    }
}
