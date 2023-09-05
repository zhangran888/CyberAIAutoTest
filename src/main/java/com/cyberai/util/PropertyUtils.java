package com.cyberai.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.MissingResourceException;
import java.util.Properties;


public class PropertyUtils {

    private static Properties RESOURCE_BUNDLE = null;

    private static boolean loadDone = false;

    public static String getProperty(String key) {
        loadAllProperties();

        try {
            return RESOURCE_BUNDLE.getProperty(key);
        } catch (MissingResourceException e) {
            throw new RuntimeException("can't load context file,key:" + key, e);
        }
    }

    private static synchronized void loadAllProperties() {
        if(loadDone) {
            return;
        }
        Properties properties = new Properties();
        properties.putAll(loadProperties("config.properties"));
        loadDone=true;
        RESOURCE_BUNDLE = properties;
    }

    public static int getIntProperty(String key) {
        return Integer.valueOf(getProperty(key));
    }

    public static boolean getBooleanProperty(String key) {
        return Boolean.valueOf(getProperty(key));
    }

    public static Properties loadProperties(String path) {
        Properties properties = new Properties();
        InputStreamReader isr;
        try {
            isr = new InputStreamReader(loadAsInputStream(path), "UTF-8");
            BufferedReader bf = new BufferedReader(isr);
            properties.load(bf);
        } catch (IOException e) {
            throw new RuntimeException("配置文件解析失败。", e);
        }
        return properties;
    }

    private static InputStream loadAsInputStream(String path) {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (stream == null) {
            throw new RuntimeException("配置文件加载失败或文件不存在:" + path);
        }
        return stream;
    }
}
