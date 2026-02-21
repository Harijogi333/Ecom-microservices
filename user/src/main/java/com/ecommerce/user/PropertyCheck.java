package com.ecommerce.user;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class PropertyCheck {

    @Value("${spring.data.mongodb.database}")
    private String dbName;

    @PostConstruct
    public void check() {
        System.out.println("🔥 DATABASE FROM PROPERTIES = " + dbName);
    }
}
