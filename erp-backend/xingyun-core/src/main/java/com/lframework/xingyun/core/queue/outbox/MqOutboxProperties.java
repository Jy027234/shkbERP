package com.lframework.xingyun.core.queue.outbox;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.rabbitmq.outbox")
public class MqOutboxProperties {

  private boolean enabled = true;
  private int batchSize = 20;
  private int leaseSeconds = 60;
  private int maxAttempts = 10;
  private long initialRetrySeconds = 5;
  private long maxRetrySeconds = 300;
  private long confirmTimeoutMillis = 5_000;
  private int retentionDays = 7;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

  public int getLeaseSeconds() {
    return leaseSeconds;
  }

  public void setLeaseSeconds(int leaseSeconds) {
    this.leaseSeconds = leaseSeconds;
  }

  public int getMaxAttempts() {
    return maxAttempts;
  }

  public void setMaxAttempts(int maxAttempts) {
    this.maxAttempts = maxAttempts;
  }

  public long getInitialRetrySeconds() {
    return initialRetrySeconds;
  }

  public void setInitialRetrySeconds(long initialRetrySeconds) {
    this.initialRetrySeconds = initialRetrySeconds;
  }

  public long getMaxRetrySeconds() {
    return maxRetrySeconds;
  }

  public void setMaxRetrySeconds(long maxRetrySeconds) {
    this.maxRetrySeconds = maxRetrySeconds;
  }

  public long getConfirmTimeoutMillis() {
    return confirmTimeoutMillis;
  }

  public void setConfirmTimeoutMillis(long confirmTimeoutMillis) {
    this.confirmTimeoutMillis = confirmTimeoutMillis;
  }

  public int getRetentionDays() {
    return retentionDays;
  }

  public void setRetentionDays(int retentionDays) {
    this.retentionDays = retentionDays;
  }
}
