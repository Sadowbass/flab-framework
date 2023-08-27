package com.sadowbass.summer.framework.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Bean {

    private final String objectName;
    private final Object value;

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public boolean isSame(String targetName) {
        return objectName.equals(targetName);
    }

    public Class<?> getBeanClass() {
        return this.value.getClass();
    }
}
