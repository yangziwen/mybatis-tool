package io.github.yangziwen.mybatistool;

import java.io.File;
import java.util.Collections;
import java.util.List;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public interface GenerateConfig {

    static Config config = load();

    static Config load() {
        return ConfigFactory.parseFile(new File("conf/generate.config"));
    }

    interface database {

        Config config = getConfig(GenerateConfig.config, "database");

        static String driver_class_name = getStringOrDefault(config, "driver-class-name", "");

        static String url = getStringOrDefault(config, "url", "");

        static String username = getStringOrDefault(config, "username", "");

        static String password = getStringOrDefault(config, "password", "");

    }

    static Config getConfig(Config config, String path) {
        return hasPath(config, path)? config.getConfig(path) : ConfigFactory.empty();
    }

    static boolean getBooleanOrDefault(Config config, String path, boolean defaultValue) {
        return hasPath(config, path) ? config.getBoolean(path) : defaultValue;
    }

    static int getIntOrDefault(Config config, String path, int defaultValue) {
        return hasPath(config, path) ? config.getInt(path) : defaultValue;
    }

    static String getStringOrDefault(Config config, String path, String defaultValue) {
        return hasPath(config, path) ? config.getString(path) : defaultValue;
    }

    static List<String> getStringList(Config config, String path) {
        return hasPath(config, path) ? config.getStringList(path) : Collections.emptyList();
    }

    static boolean hasPath(Config config, String path) {
        return config != null && config.hasPath(path);
    }


}

