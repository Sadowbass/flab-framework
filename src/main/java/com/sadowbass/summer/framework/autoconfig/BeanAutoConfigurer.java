package com.sadowbass.summer.framework.autoconfig;

import com.sadowbass.summer.framework.annotation.Component;
import com.sadowbass.summer.framework.config.TargetBean;
import com.sadowbass.summer.framework.config.beanfactory.BeanFactory;
import com.sadowbass.summer.framework.config.beanfactory.DefaultBeanFactory;
import com.sadowbass.summer.framework.config.beanfinder.BeanFinder;
import com.sadowbass.summer.framework.config.beanfinder.ComponentBeanFinder;

import java.util.List;

public class BeanAutoConfigurer implements Configurer {

    private BeanFactory beanFactory;

    @Override
    public void configure() {
        BeanFinder beanFinder = new ComponentBeanFinder();
        List<TargetBean> search = beanFinder.search("");
        this.beanFactory = new DefaultBeanFactory(search);
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }
}
