package com.sadowbass.summer.framework;

import com.sadowbass.summer.framework.autoconfig.BeanAutoConfigurer;
import com.sadowbass.summer.framework.autoconfig.Configurer;
import com.sadowbass.summer.framework.config.beanfactory.BeanFactory;

public class SummerApplication {

    private BeanFactory beanFactory;

    public static SummerApplication run() {
        SummerApplication summerApplication = new SummerApplication();

        Configurer configurer = new BeanAutoConfigurer();
        configurer.configure();
        if (configurer instanceof BeanAutoConfigurer) {
            BeanAutoConfigurer beanAutoConfigurer = (BeanAutoConfigurer) configurer;
            summerApplication.beanFactory = beanAutoConfigurer.getBeanFactory();
        }

        return summerApplication;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }
}
