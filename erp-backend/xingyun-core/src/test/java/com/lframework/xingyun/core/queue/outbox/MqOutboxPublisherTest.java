package com.lframework.xingyun.core.queue.outbox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lframework.xingyun.core.dto.order.ApprovePassOrderDto;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory.ConfirmType;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;

class MqOutboxPublisherTest {

  @Test
  void publishesOnlyAfterCorrelatedBrokerAck() throws Exception {
    RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
    doAnswer(invocation -> {
      CorrelationData correlationData = invocation.getArgument(4);
      correlationData.getFuture().complete(new CorrelationData.Confirm(true, null));
      return null;
    }).when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(),
        any(MessagePostProcessor.class), any(CorrelationData.class));

    RabbitProperties rabbitProperties = new RabbitProperties();
    rabbitProperties.setPublisherConfirmType(ConfirmType.CORRELATED);
    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    ApprovePassOrderDto payload = new ApprovePassOrderDto();
    payload.setId("order-1");
    String json = objectMapper.writeValueAsString(payload);
    MqOutboxRecord record = new MqOutboxRecord("event-1",
        MqOutboxEventType.APPROVE_PASS_ORDER, json, 1000, 0, LocalDateTime.now());

    new MqOutboxPublisher(rabbitTemplate, objectMapper, new MqOutboxProperties(),
        rabbitProperties).publish(record);

    assertEquals("event-1", record.id());
  }

  @Test
  void rejectsConfigurationWithoutCorrelatedConfirms() {
    RabbitProperties rabbitProperties = new RabbitProperties();
    rabbitProperties.setPublisherConfirmType(ConfirmType.SIMPLE);

    assertThrows(IllegalStateException.class,
        () -> new MqOutboxPublisher(mock(RabbitTemplate.class), new ObjectMapper(),
            new MqOutboxProperties(), rabbitProperties));
  }
}
