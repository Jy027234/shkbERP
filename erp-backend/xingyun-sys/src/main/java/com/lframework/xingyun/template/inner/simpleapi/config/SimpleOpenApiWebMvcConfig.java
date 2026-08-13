package com.lframework.xingyun.template.inner.simpleapi.config;

import com.lframework.xingyun.template.inner.simpleapi.interceptor.SimpleOpenApiInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(SimpleOpenApiProperties.class)
public class SimpleOpenApiWebMvcConfig implements WebMvcConfigurer {

  private final SimpleOpenApiProperties props;

  public SimpleOpenApiWebMvcConfig(SimpleOpenApiProperties props) {
    this.props = props;
  }

  @Bean
  public SimpleOpenApiInterceptor simpleOpenApiInterceptor() {
    return new SimpleOpenApiInterceptor(props);
  }

  @Override
  public void addInterceptors(@NonNull InterceptorRegistry registry) {
    // 全局注册拦截器，只有标注 @SimpleOpenApi 的 Handler 才会触发校验
    registry.addInterceptor(simpleOpenApiInterceptor())
        .addPathPatterns("/**");
  }
}
