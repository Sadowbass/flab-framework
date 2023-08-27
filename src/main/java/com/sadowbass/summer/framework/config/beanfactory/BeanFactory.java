package com.sadowbass.summer.framework.config.beanfactory;

public interface BeanFactory {

    Object getBean(String beanName);

    Object getBean(Class<?> beanType);

    Object getBean(Class<?> beanType, String beanName);
}
