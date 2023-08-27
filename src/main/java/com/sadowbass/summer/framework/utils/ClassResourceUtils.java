package com.sadowbass.summer.framework.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class ClassResourceUtils extends ResourceUtils {

    public static List<URL> getClassResources(String classPath) {
        String currentClassPath = createClassPath(classPath);
        List<URL> resources = getResource(currentClassPath);
        List<URL> acc = new ArrayList<>();
        for (URL resource : resources) {
            File file = new File(resource.getFile());
            try {
                fill(file, acc);
            } catch (MalformedURLException e) {
            }
        }

        return acc;
    }

    private static void fill(File file, List<URL> acc) throws MalformedURLException {
        if (!file.exists()) {
            return;
        }

        for (File currentFile : file.listFiles()) {
            if (currentFile.isDirectory()) {
                fill(currentFile, acc);
            } else if (currentFile.getName().endsWith(".class")) {
                acc.add(currentFile.toURI().toURL());
            }
        }
    }
}
