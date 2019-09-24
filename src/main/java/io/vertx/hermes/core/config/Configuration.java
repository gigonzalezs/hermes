package io.vertx.hermes.core.config;

import io.reactivex.Single;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private static final Properties prop = new Properties();

    static {
        String propertiesFileName = Configuration.class.getClassLoader()
                .getResource("app.properties").getFile();
        try (InputStream input = new FileInputStream(propertiesFileName)) {
            prop.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key, String defaultValue) {
        return prop.getProperty(key, defaultValue);
    }

    public static String getProperty(String key) {
        return prop.getProperty(key);
    }

    public static Single<String> rxGetProperty(String key, String defaultValue) {
        return Single.just(prop.getProperty(key, defaultValue));
    }

    public static Single<String> rxGetProperty(String key) {
        return Single.just(prop.getProperty(key));
    }

}
