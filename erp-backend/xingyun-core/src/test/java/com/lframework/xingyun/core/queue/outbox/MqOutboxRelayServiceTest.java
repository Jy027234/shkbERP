package com.lframework.xingyun.core.queue.outbox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MqOutboxRelayServiceTest {

  @Test
  void retryDelayUsesBoundedExponentialBackoff() {
    assertEquals(5, MqOutboxRelayService.retryDelaySeconds(0, 5, 300));
    assertEquals(10, MqOutboxRelayService.retryDelaySeconds(1, 5, 300));
    assertEquals(160, MqOutboxRelayService.retryDelaySeconds(5, 5, 300));
    assertEquals(300, MqOutboxRelayService.retryDelaySeconds(6, 5, 300));
    assertEquals(300, MqOutboxRelayService.retryDelaySeconds(30, 5, 300));
  }
}
