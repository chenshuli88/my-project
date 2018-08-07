package com.csl.commons.utils;

import base.config.Constant;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class SpringUtils implements ApplicationListener<ContextRefreshedEvent> {
    private static ApplicationContext context;

    public SpringUtils() {
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static String getProperty(String s) {
        return Constant.getConfig(s);
    }

    public static String getRequiredProperty(String s) {
        String config = Constant.getConfig(s);
        if (config == null) {
            throw new IllegalStateException(String.format("property [%s] not found, and is required!", s));
        } else {
            return config;
        }
    }

    public static <T> T getBean(String name, Class<T> clz) {
        return context.getBean(name, clz);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        context = event.getApplicationContext();
    }
}
