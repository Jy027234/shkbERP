package com.lframework.xingyun.template.inner.simpleapi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 简单开放接口注解：标记接口使用简单鉴权（X-Simple-Auth = MD5(secret + yyyyMMddHHmm)）
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleOpenApi {
}
