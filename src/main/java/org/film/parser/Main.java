package org.film.parser;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "org.film.parser.core.configuration")
public class Main {
    public static void main(String[] args) {

        ApplicationContext ctx = SpringApplication.run(Main.class, args);

    }
}

