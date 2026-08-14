package com.lframework.xingyun.core.queue.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lframework.starter.mq.core.queue.QueueDefinition;
import com.lframework.xingyun.core.dto.order.ApprovePassOrderDto;
import com.lframework.xingyun.core.dto.stock.ProductStockChangeDto;
import com.lframework.xingyun.core.queue.MqConstants;
import java.io.IOException;
import java.io.Serializable;

public enum MqOutboxEventType {

  ADD_STOCK(ProductStockChangeDto.class, MqConstants.ADD_STOCK),
  SUB_STOCK(ProductStockChangeDto.class, MqConstants.SUB_STOCK),
  APPROVE_PASS_ORDER(ApprovePassOrderDto.class, MqConstants.APPROVE_PASS_ORDER);

  private final Class<? extends Serializable> payloadType;
  private final QueueDefinition queueDefinition;

  MqOutboxEventType(Class<? extends Serializable> payloadType,
      QueueDefinition queueDefinition) {
    this.payloadType = payloadType;
    this.queueDefinition = queueDefinition;
  }

  public Serializable deserialize(ObjectMapper objectMapper, String payload) throws IOException {
    return objectMapper.readValue(payload, payloadType);
  }

  public QueueDefinition getQueueDefinition() {
    return queueDefinition;
  }

  public void assignEventId(Serializable payload, String eventId) {
    if (payload instanceof ProductStockChangeDto stockChange) {
      stockChange.setEventId(eventId);
    } else if (payload instanceof ApprovePassOrderDto order) {
      order.setEventId(eventId);
    } else {
      throw new IllegalArgumentException("Unsupported outbox payload: " + payload.getClass());
    }
  }
}
