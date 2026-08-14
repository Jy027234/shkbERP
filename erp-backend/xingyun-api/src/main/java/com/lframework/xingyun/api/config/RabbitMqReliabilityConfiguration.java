package com.lframework.xingyun.api.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory.ConfirmType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecovererWithConfirms;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 消费失败恢复拓扑。
 *
 * <p>监听器由 Spring Boot 的有限重试拦截器处理；重试耗尽后，原消息及异常信息会在发布确认成功后
 * 转存到持久化失败队列，避免毒消息无限重入队或被静默丢弃。</p>
 */
@Configuration(proxyBeanMethods = false)
public class RabbitMqReliabilityConfiguration {

    static final String FAILED_EXCHANGE = "shkb.failed.direct";
    static final String FAILED_QUEUE = "shkb.failed";
    static final String FAILED_ROUTING_KEY = "failed";
    private static final long FAILED_PUBLISH_CONFIRM_TIMEOUT_MILLIS = 5_000L;

    @Bean
    public Declarables rabbitFailureTopology() {
        DirectExchange exchange = new DirectExchange(FAILED_EXCHANGE, true, false);
        Queue queue = QueueBuilder.durable(FAILED_QUEUE).build();
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(FAILED_ROUTING_KEY);
        return new Declarables(exchange, queue, binding);
    }

    @Bean
    public MessageRecoverer rabbitMessageRecoverer(RabbitTemplate rabbitTemplate,
            RabbitProperties rabbitProperties) {
        ConfirmType confirmType = rabbitProperties.getPublisherConfirmType();
        if (ConfirmType.CORRELATED != confirmType) {
            throw new IllegalStateException(
                    "RabbitMQ outbox requires publisher-confirm-type CORRELATED");
        }
        RepublishMessageRecovererWithConfirms recoverer = new RepublishMessageRecovererWithConfirms(
                rabbitTemplate, FAILED_EXCHANGE, FAILED_ROUTING_KEY, confirmType);
        recoverer.setConfirmTimeout(FAILED_PUBLISH_CONFIRM_TIMEOUT_MILLIS);
        recoverer.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        return recoverer;
    }
}
