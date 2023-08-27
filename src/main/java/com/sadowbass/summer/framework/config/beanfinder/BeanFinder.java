package com.sadowbass.summer.framework.config.beanfinder;

import com.sadowbass.summer.framework.config.TargetBean;

import java.util.List;

public interface BeanFinder {

    List<TargetBean> search(String classPath);
}
