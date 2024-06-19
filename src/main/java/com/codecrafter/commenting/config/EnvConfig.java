package com.codecrafter.commenting.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
    @PropertySource("file:${user.dir}/.env")
})
public class EnvConfig {

}