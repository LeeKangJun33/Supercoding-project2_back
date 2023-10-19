package com.example.project02.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String coolSmsKey;
    private String coolSmsSecret;
    private String coolSmsFromPhoneNumber;

    public void setCoolSmsKey(String coolSmsKey) {
        this.coolSmsKey = coolSmsKey;
    }

    public void setCoolSmsSecret(String coolSmsSecret) {
        this.coolSmsSecret = coolSmsSecret;
    }

    public void setCoolSmsFromPhoneNumber(String coolSmsFromPhoneNumber) {
        this.coolSmsFromPhoneNumber = coolSmsFromPhoneNumber;
    }
}
