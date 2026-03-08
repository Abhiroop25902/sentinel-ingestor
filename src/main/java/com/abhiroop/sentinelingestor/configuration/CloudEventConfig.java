package com.abhiroop.sentinelingestor.configuration;

import io.cloudevents.spring.mvc.CloudEventHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CloudEventConfig implements WebMvcConfigurer {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // This makes @RequestBody CloudEvent work automatically
        converters.addFirst(new CloudEventHttpMessageConverter());
    }
}
