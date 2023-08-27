package com.sadowbass.summer.framework.config;

import lombok.Getter;

@Getter
public class TargetBean {

    private final Class<?> targetClass;
    private final String targetName;

    public TargetBean(Class<?> targetClass, String targetName) {
        this.targetClass = targetClass;

        char[] chars = targetName.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        this.targetName = new String(chars);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TargetBean{");
        sb.append("targetClass=").append(targetClass);
        sb.append(", targetName='").append(targetName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
