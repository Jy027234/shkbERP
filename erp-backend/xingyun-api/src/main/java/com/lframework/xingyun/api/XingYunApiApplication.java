package com.lframework.xingyun.api;

import com.lframework.starter.web.core.annotations.locker.EnableLock;
import com.lframework.starter.web.core.annotations.locker.LockType;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.mybatis.spring.annotation.MapperScan;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableLock(type = LockType.REDIS)
@ServletComponentScan(basePackages = {"com.lframework.xingyun"})
@SpringBootApplication(scanBasePackages = {"com.lframework.xingyun"})
@MapperScan(value = {"com.lframework.xingyun.**.mappers"}, nameGenerator = XingyunMapperBeanNameGenerator.class)
public class XingYunApiApplication {

  public static void main(String[] args) {

    SpringApplication.run(XingYunApiApplication.class, args);
  }

  /**
   * OpenAPI / springdoc configuration. Replaces legacy springfox Docket beans.
   */
  @Configuration
  public static class SwaggerApiConfiguration {

    @Bean
    public GroupedOpenApi defaultApi() {
      return GroupedOpenApi.builder()
          .group("星云ERP")
          .packagesToScan(
              "com.lframework.xingyun.api",
              "com.lframework.xingyun.basedata",
              "com.lframework.xingyun.chart",
              "com.lframework.xingyun.core",
              "com.lframework.xingyun.sc",
              "com.lframework.xingyun.shkb",
              "com.lframework.xingyun.settle",
              "com.lframework.xingyun.template",
              "com.lframework.xingyun.comp")
          .build();
    }

    // Keep this bean so API metadata remains customizable.
    @Bean
    public OpenAPI apiInfo() {
      return new OpenAPI().info(new Info()
          .title("星云ERP接口文档")
          .description("# 星云ERP接口文档")
          .contact(new Contact().email("lframework@163.com")));
    }
  }
}
