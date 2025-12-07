package com.example.useradmin;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan("com.example.useradmin")
@EnableJpaRepositories(basePackages = {
        "com.example.useradmin.domain.repo"
})
@EntityScan(basePackages = {
        "com.example.useradmin.domain.model"
})
public class UserAdminConfig {
}
