package com.lframework.xingyun.core.queue.outbox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lframework.xingyun.core.dto.order.ApprovePassOrderDto;
import com.lframework.xingyun.core.dto.stock.ProductStockChangeDto;
import com.lframework.xingyun.core.queue.MqConstants;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class MqOutboxEventTypeTest {

  private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

  @Test
  void roundTripsStockChangeAndAssignsStableEventId() throws Exception {
    ProductStockChangeDto payload = new ProductStockChangeDto();
    payload.setScId("sc-1");
    payload.setProductId("product-1");
    payload.setNum(2);
    payload.setCreateTime(LocalDateTime.of(2026, 8, 14, 12, 0));

    MqOutboxEventType.ADD_STOCK.assignEventId(payload, "event-1");
    ProductStockChangeDto restored = (ProductStockChangeDto) MqOutboxEventType.ADD_STOCK
        .deserialize(objectMapper, objectMapper.writeValueAsString(payload));

    assertEquals("event-1", restored.getEventId());
    assertEquals("sc-1", restored.getScId());
    assertEquals(2, restored.getNum());
    assertSame(MqConstants.ADD_STOCK, MqOutboxEventType.ADD_STOCK.getQueueDefinition());
  }

  @Test
  void roundTripsApprovedOrderAndAssignsStableEventId() throws Exception {
    ApprovePassOrderDto payload = new ApprovePassOrderDto();
    payload.setId("order-1");
    payload.setTotalAmount(new BigDecimal("88.50"));
    payload.setOrderType(ApprovePassOrderDto.OrderType.PURCHASE_ORDER);

    MqOutboxEventType.APPROVE_PASS_ORDER.assignEventId(payload, "event-2");
    ApprovePassOrderDto restored = (ApprovePassOrderDto) MqOutboxEventType.APPROVE_PASS_ORDER
        .deserialize(objectMapper, objectMapper.writeValueAsString(payload));

    assertEquals("event-2", restored.getEventId());
    assertEquals("order-1", restored.getId());
    assertEquals(new BigDecimal("88.50"), restored.getTotalAmount());
    assertSame(MqConstants.APPROVE_PASS_ORDER,
        MqOutboxEventType.APPROVE_PASS_ORDER.getQueueDefinition());
  }
}
