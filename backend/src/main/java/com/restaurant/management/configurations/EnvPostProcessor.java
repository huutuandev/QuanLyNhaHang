package com.restaurant.management.configurations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        File envFile = findEnvFile();
        if (envFile != null && envFile.exists()) {
            Map<String, Object> envProperties = new HashMap<>();
            try {
                List<String> lines = Files.readAllLines(envFile.toPath(), StandardCharsets.UTF_8);
                for (String line : lines) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    int equalsIdx = line.indexOf('=');
                    if (equalsIdx > 0) {
                        String key = line.substring(0, equalsIdx).trim();
                        String value = line.substring(equalsIdx + 1).trim();
                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            value = value.substring(1, value.length() - 1);
                        } else if (value.startsWith("'") && value.endsWith("'")) {
                            value = value.substring(1, value.length() - 1);
                        }
                        envProperties.put(key, value);
                    }
                }
                if (!envProperties.isEmpty()) {
                    environment.getPropertySources().addLast(new MapPropertySource("dotenvProperties", envProperties));
                }
            } catch (IOException e) {
                System.err.println("Failed to load .env file: " + e.getMessage());
            }
        }
    }

    private File findEnvFile() {
        String[] paths = {
            ".env",
            "../.env",
            "backend/.env",
            "../backend/.env"
        };
        for (String path : paths) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                return file;
            }
        }
        return null;
    }
}
