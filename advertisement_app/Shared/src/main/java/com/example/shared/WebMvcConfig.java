package com.example.shared;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

@Component
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    @Qualifier("webJacksonMessageConverter")
    private MappingJackson2HttpMessageConverter webJacksonMessageConverter;


    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(converter -> converter instanceof MappingJackson2HttpMessageConverter);
        converters.add(webJacksonMessageConverter);
    }

}