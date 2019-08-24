package com.itheima.travel.factory;

import java.util.ResourceBundle;

public class BeansFactory {
    public static Object createBeans(String key) {
        ResourceBundle resourceBundle=ResourceBundle.getBundle("beans");
        String Classpath = resourceBundle.getString(key);
        try {
            Class<?> clazz = Class.forName(Classpath);
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
