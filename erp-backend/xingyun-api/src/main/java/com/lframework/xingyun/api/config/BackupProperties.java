package com.lframework.xingyun.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "backup.mysql")
public class BackupProperties {

  private boolean enabled = true;

  private String dir;

  private int retainDays = 7;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getDir() {
    return dir;
  }

  public void setDir(String dir) {
    this.dir = dir;
  }

  public int getRetainDays() {
    return retainDays;
  }

  public void setRetainDays(int retainDays) {
    this.retainDays = retainDays;
  }
}
