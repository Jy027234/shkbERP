package com.lframework.xingyun.api.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.web.core.components.tenant.TenantContextHolder;
import com.lframework.xingyun.core.queue.outbox.MqOutboxProperties;
import com.lframework.xingyun.core.queue.outbox.MqOutboxRelayService;
import com.lframework.xingyun.template.inner.entity.Tenant;
import com.lframework.xingyun.template.inner.service.TenantService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqOutboxRelayTask {

  private final TenantService tenantService;
  private final MqOutboxRelayService relayService;
  private final MqOutboxProperties properties;

  public MqOutboxRelayTask(TenantService tenantService, MqOutboxRelayService relayService,
      MqOutboxProperties properties) {
    this.tenantService = tenantService;
    this.relayService = relayService;
    this.properties = properties;
  }

  @Scheduled(fixedDelayString = "${app.rabbitmq.outbox.fixed-delay:1000}")
  public void relay() {
    if (!properties.isEnabled()) {
      return;
    }
    forEachAvailableTenant(() -> relayService.relayBatch(), "relay");
  }

  @Scheduled(cron = "${app.rabbitmq.outbox.cleanup-cron:0 30 3 * * ?}")
  public void cleanup() {
    if (!properties.isEnabled()) {
      return;
    }
    forEachAvailableTenant(() -> {
      int purged = relayService.purgeExpiredSent();
      if (purged > 0) {
        log.info("Purged {} expired MQ outbox events", purged);
      }
    }, "cleanup");
  }

  private void forEachAvailableTenant(Runnable action, String operation) {
    List<Tenant> tenants = tenantService.list(
        Wrappers.lambdaQuery(Tenant.class)
            .select(Tenant::getId)
            .eq(Tenant::getAvailable, true));
    for (Tenant tenant : tenants) {
      try {
        TenantContextHolder.setTenantId(tenant.getId());
        action.run();
      } catch (Exception e) {
        log.error("MQ outbox {} failed for tenant {}", operation, tenant.getId(), e);
      } finally {
        TenantContextHolder.clearTenantId();
      }
    }
  }
}
