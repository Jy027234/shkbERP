package com.lframework.xingyun.template.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Springdoc group for template/system APIs.
 * Replaces legacy springfox Docket configuration under Spring Boot 3.
 */
@Configuration
public class TemplateApiConfiguration {

  @Bean
  public GroupedOpenApi templateApiGroup() {
    return GroupedOpenApi.builder()
        .group("系统内置模块")
        .packagesToScan("com.lframework.xingyun.template")
        .build();
  }
}
