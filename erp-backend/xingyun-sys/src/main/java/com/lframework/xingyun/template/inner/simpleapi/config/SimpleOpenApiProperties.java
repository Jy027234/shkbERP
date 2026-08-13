package com.lframework.xingyun.template.inner.simpleapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 简单开放接口配置
 */
@ConfigurationProperties(prefix = "simple-open-api")
public class SimpleOpenApiProperties {

  /** 是否启用简单开放接口鉴权 */
  private boolean enabled = true;

  /** 共享密钥（请在生产环境下进行配置） */
  private String secret = "change-me";

  /** 允许的时间误差（分钟），默认 1，即兼容 前/当前/后一分钟 */
  private int toleranceMinutes = 1;

  /** 简单鉴权使用的请求头名称 */
  private String headerName = "X-Simple-Auth";

  /** 可选：从请求头读取的租户ID，用于多租户上下文设置 */
  private String tenantHeaderName = "X-Tenant-Id";

  /**
   * 可选：默认租户ID。当启用多租户且请求未提供租户头时，使用该默认值。
   */
  private Integer defaultTenantId;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public int getToleranceMinutes() {
    return toleranceMinutes;
  }

  public void setToleranceMinutes(int toleranceMinutes) {
    this.toleranceMinutes = toleranceMinutes;
  }

  public String getHeaderName() {
    return headerName;
  }

  public void setHeaderName(String headerName) {
    this.headerName = headerName;
  }

  public String getTenantHeaderName() {
    return tenantHeaderName;
  }

  public void setTenantHeaderName(String tenantHeaderName) {
    this.tenantHeaderName = tenantHeaderName;
  }

  public Integer getDefaultTenantId() {
    return defaultTenantId;
  }

  public void setDefaultTenantId(Integer defaultTenantId) {
    this.defaultTenantId = defaultTenantId;
  }
}
