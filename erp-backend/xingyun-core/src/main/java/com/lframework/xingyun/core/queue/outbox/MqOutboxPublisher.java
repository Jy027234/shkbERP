package com.lframework.xingyun.core.queue.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lframework.starter.mq.rabbitmq.queue.RabbitMQQueueDefinition;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory.ConfirmType;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Component;

@Component
public class MqOutboxPublisher {

  public static final String OUTBOX_ID_HEADER = "x-outbox-id";
  private static final String TENANT_ID_HEADER = "tenantId";

  private final RabbitTemplate rabbitTemplate;
  private final ObjectMapper objectMapper;
  private final MqOutboxProperties properties;

  public MqOutboxPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper,
      MqOutboxProperties properties, RabbitProperties rabbitProperties) {
    if (rabbitProperties.getPublisherConfirmType() != ConfirmType.CORRELATED) {
      throw new IllegalStateException(
          "MQ outbox requires spring.rabbitmq.publisher-confirm-type=CORRELATED");
    }
    this.rabbitTemplate = rabbitTemplate;
    this.objectMapper = objectMapper;
    this.properties = properties;
  }

  public void publish(MqOutboxRecord record) {
    try {
      Serializable payload = record.eventType().deserialize(objectMapper, record.payload());
      record.eventType().assignEventId(payload, record.id());
      RabbitMQQueueDefinition definition = (RabbitMQQueueDefinition) record.eventType()
          .getQueueDefinition();
      CorrelationData correlationData = new CorrelationData(record.id());
      rabbitTemplate.convertAndSend(definition.getExchange(),
          Objects.requireNonNullElse(definition.getRoutingKey(), ""), payload, message -> {
            message.getMessageProperties().setHeader(OUTBOX_ID_HEADER, record.id());
            if (record.tenantId() != null) {
              message.getMessageProperties().setHeader(TENANT_ID_HEADER, record.tenantId());
            }
            return message;
          }, correlationData);

      CorrelationData.Confirm confirm = correlationData.getFuture().get(
          properties.getConfirmTimeoutMillis(), TimeUnit.MILLISECONDS);
      ReturnedMessage returned = correlationData.getReturned();
      if (!confirm.isAck()) {
        throw new IllegalStateException("RabbitMQ publisher nack: " + confirm.getReason());
      }
      if (returned != null) {
        throw new IllegalStateException(
            "RabbitMQ returned message: " + returned.getReplyText());
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted while waiting for RabbitMQ confirm", e);
    } catch (ExecutionException | TimeoutException e) {
      throw new IllegalStateException("RabbitMQ publisher confirm failed", e);
    } catch (Exception e) {
      if (e instanceof IllegalStateException illegalStateException) {
        throw illegalStateException;
      }
      throw new IllegalStateException("Cannot publish MQ outbox event " + record.id(), e);
    }
  }
}
