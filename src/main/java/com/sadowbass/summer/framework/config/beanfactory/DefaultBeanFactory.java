package com.sadowbass.summer.framework.config.beanfactory;

import com.sadowbass.summer.framework.config.Bean;
import com.sadowbass.summer.framework.config.TargetBean;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultBeanFactory implements BeanFactory {

    private List<TargetBean> targetBeanList;
    private Map<Class<?>, List<TargetBean>> targetBeanMap;

    private final Map<Class<?>, List<Bean>> typedBeans;
    private final Map<String, Bean> namedBeans;

    public DefaultBeanFactory(List<TargetBean> targetBeanList) {
        this.targetBeanList = targetBeanList;
        this.targetBeanMap = new HashMap<>();

        this.typedBeans = new HashMap<>();
        this.namedBeans = new HashMap<>();

        for (TargetBean targetBean : this.targetBeanList) {
            fillTargetMap(targetBean.getTargetClass(), targetBean);
        }

        create();
        clearTargets();

        namedBeans.entrySet().forEach(System.out::println);
    }

    private void fillTargetMap(Class<?> target, TargetBean targetBean) {
        Set<Class<?>> superClass = parseAllSuperClass(target);

        for (Class<?> aClass : superClass) {
            List<TargetBean> beanList = targetBeanMap.getOrDefault(aClass, new ArrayList<>());
            beanList.add(targetBean);
            targetBeanMap.put(aClass, beanList);
        }
    }

    private Set<Class<?>> parseAllSuperClass(Class<?> target) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        classes.add(target);

        if (target.getSuperclass() != null) {
            Set<Class<?>> superClasses = parseAllSuperClass(target.getSuperclass());
            classes.addAll(superClasses);
        }

        for (Class<?> anInterface : target.getInterfaces()) {
            Set<Class<?>> interfaces = parseAllSuperClass(anInterface);
            classes.addAll(interfaces);
        }

        return classes;
    }

    private void create() {
        for (TargetBean targetBean : targetBeanList) {
            if (isNotCreate(targetBean)) {
                BeanCreationContext context = new BeanCreationContext(this);
                context.create(targetBean);
                merge(context.getResult());
            }
        }
    }

    private boolean isNotCreate(TargetBean targetBean) {
        Bean bean = namedBeans.get(targetBean.getTargetName());
        return bean == null;
    }

    private void merge(Map<String, Bean> context) {
        for (Map.Entry<String, Bean> entry : context.entrySet()) {
            String key = entry.getKey();
            if (namedBeans.containsKey(key)) {
                throw new RuntimeException("duplicate bean name");
            }
            Bean value = entry.getValue();
            namedBeans.put(key, value);

            Set<Class<?>> superClass = parseAllSuperClass(value.getBeanClass());
            for (Class<?> aClass : superClass) {
                List<Bean> typeList = typedBeans.getOrDefault(aClass, new ArrayList<>());
                typeList.add(value);
                typedBeans.put(aClass, typeList);
            }
        }

    }

    private void clearTargets() {
        this.targetBeanList.clear();
        this.targetBeanMap.clear();
    }

    @Override
    public Object getBean(String beanName) {
        return namedBeans.get(beanName).getValue();
    }

    @Override
    public Object getBean(Class<?> beanType) {
        List<Bean> beans = typedBeans.get(beanType);
        if (beans == null || beans.isEmpty()) {
            return null;
        }

        if (beans.size() != 1) {
            throw new RuntimeException("required single bean");
        }

        return beans.get(0).getValue();
    }

    @Override
    public Object getBean(Class<?> beanType, String beanName) {
        List<Bean> beans = typedBeans.get(beanType);
        if (beans == null || beans.isEmpty()) {
            return null;
        }

        List<Bean> filteredBeans = beans.stream()
                .filter(bean -> bean.isSame(beanName))
                .collect(Collectors.toList());

        if (filteredBeans.size() != 1) {
            throw new RuntimeException("required single bean");
        }

        return filteredBeans.get(0).getValue();
    }

    public List<TargetBean> getTargetBeanList() {
        return targetBeanList;
    }
}
