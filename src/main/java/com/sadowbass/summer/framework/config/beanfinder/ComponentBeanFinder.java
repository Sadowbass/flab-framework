package com.sadowbass.summer.framework.config.beanfinder;

import com.sadowbass.summer.framework.annotation.Component;
import com.sadowbass.summer.framework.config.TargetBean;
import com.sadowbass.summer.framework.utils.ClassResourceUtils;
import com.sadowbass.summer.framework.utils.ResourceUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ComponentBeanFinder extends AbstractBeanFinder implements BeanFinder {

    @Override
    public List<TargetBean> search(String classPath) {
        List<URL> classResources = ClassResourceUtils.getClassResources(classPath);

        List<TargetBean> targetBeanList = new ArrayList<>();
        for (URL resource : classResources) {
            File file = new File(resource.getFile());
            String targetPath = createTargetPath(ResourceUtils.ROOT_PATH, file);
            try {
                Class<?> targetClass = Class.forName(targetPath);
                if (targetClass.isInterface()) {
                    continue;
                }
                
                if (isComponent(targetClass)) {
                    targetBeanList.add(new TargetBean(targetClass, targetClass.getSimpleName()));
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        return targetBeanList;
    }

    private boolean isComponent(Class<?> aClass) {
        Annotation[] annotations = aClass.getAnnotations();
        boolean flag = false;

        for (Annotation annotation : annotations) {
            Class<? extends Annotation> type = annotation.annotationType();
            if (type.getPackage().getName().equals("java.lang.annotation")) {
                continue;
            }

            if (type == Component.class) {
                flag = true;
            }
            flag = flag || isComponent(type);

            if (flag) {
                break;
            }
        }

        return flag;
    }
}
