package com.sadowbass.summer.framework.config.beanfactory;

import com.sadowbass.summer.framework.annotation.Autowired;
import com.sadowbass.summer.framework.config.Bean;
import com.sadowbass.summer.framework.config.TargetBean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

class BeanCreationContext {

    private Deque<TargetBean> dependencies;

    private final DefaultBeanFactory beanFactory;
    private final Map<String, Bean> namedBeans;

    public BeanCreationContext(DefaultBeanFactory beanFactory) {
        this.dependencies = new ArrayDeque<>();
        this.beanFactory = beanFactory;
        this.namedBeans = new HashMap<>();
    }

    public Map<String, Bean> getResult() {
        return namedBeans;
    }

    public void create(TargetBean targetBean) {
        add(targetBean);
        Class<?> targetClass = targetBean.getTargetClass();
        Constructor<?> constructor = chooseConstructor(targetClass.getDeclaredConstructors());
        Bean instantiate = instantiate(targetBean.getTargetName(), constructor);
        if (namedBeans.containsKey(instantiate.getObjectName())) {
            throw new RuntimeException("duplicate bean name");
        }

        namedBeans.put(instantiate.getObjectName(), instantiate);
    }

    private void add(TargetBean targetBean) {
        if (dependencies.contains(targetBean)) {
            throw new RuntimeException("circular");
        }

        dependencies.add(targetBean);
    }

    private Constructor<?> chooseConstructor(Constructor<?>[] constructors) {
        if (constructors.length == 1) {
            return constructors[0];
        }

        Constructor<?> selected = null;
        boolean hasAutowired = false;
        for (Constructor<?> constructor : constructors) {
            if (hasAutowired(constructor.getDeclaredAnnotations())) {
                if (hasAutowired) {
                    throw new RuntimeException("duplicate autowired");
                }
                hasAutowired = true;
                selected = constructor;
                continue;
            }

            if (constructor.getParameterCount() == 0) {
                selected = constructor;
            }
        }

        if (selected == null) {
            throw new RuntimeException("no default constructor");
        }

        return selected;
    }

    private boolean hasAutowired(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == Autowired.class) {
                return true;
            }
        }

        return false;
    }

    private Bean instantiate(String targetName, Constructor<?> constructor) {
        Object[] params = createParams(constructor);
        try {
            return new Bean(targetName, constructor.newInstance(params));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("something exception");
        }
    }

    private Object[] createParams(Constructor<?> constructor) {
        List<Object> paramList = new ArrayList<>();

        for (Parameter param : constructor.getParameters()) {
            Class<?> paramType = param.getType();
            String paramName = param.getName();
            Object bean = findBean(paramType, paramName);
            paramList.add(bean);
        }

        return paramList.toArray();
    }

    private Object findBean(Class<?> beanType, String beanName) {
        if (isAlreadyCreated(beanType, beanName)) {
            return get(beanType, beanName);
        }

        List<TargetBean> targetBeans = beanFactory.getTargetBeanList()
                .stream()
                .filter(bean -> bean.getTargetName().equals(beanName))
                .collect(Collectors.toList());

        if (targetBeans.isEmpty()) {
            throw new RuntimeException("no target bean");
        }

        if (targetBeans.size() > 1) {
            throw new RuntimeException("required single bean");
        }

        create(targetBeans.get(0));

        return get(beanType, beanName);
    }

    private boolean isAlreadyCreated(Class<?> targetClass, String targetName) {
        Object bean = beanFactory.getBean(targetClass, targetName);

        if (bean != null) {
            return true;
        }

        return namedBeans.get(targetName) != null;
    }

    private Object get(Class<?> beanType, String beanName) {
        Object bean = beanFactory.getBean(beanType, beanName);

        if (bean == null) {
            bean = namedBeans.get(beanName).getValue();
        }

        return bean;
    }
}
