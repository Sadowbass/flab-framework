package com.sadowbass.summer.framework.config.beanfinder;

import java.io.File;

public abstract class AbstractBeanFinder implements BeanFinder {

    protected String createTargetPath(String systemClassPath, File file) {
        return file.getPath()
                .replace(systemClassPath, "")
                .replace(".class", "")
                .replace("/", ".");
    }
}
