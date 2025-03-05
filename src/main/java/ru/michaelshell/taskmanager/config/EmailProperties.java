package ru.michaelshell.taskmanager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.mail")
public class EmailProperties {
    private String from;
    private String recipient;
}
