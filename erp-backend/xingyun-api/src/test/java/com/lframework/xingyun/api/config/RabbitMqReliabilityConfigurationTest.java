package com.lframework.xingyun.api.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory.ConfirmType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecovererWithConfirms;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.core.io.ClassPathResource;

class RabbitMqReliabilityConfigurationTest {

    @Test
    void declaresDurableFailureTopology() {
        Declarables topology = new RabbitMqReliabilityConfiguration().rabbitFailureTopology();

        DirectExchange exchange = find(topology, DirectExchange.class);
        Queue queue = find(topology, Queue.class);
        Binding binding = find(topology, Binding.class);

        assertEquals(RabbitMqReliabilityConfiguration.FAILED_EXCHANGE, exchange.getName());
        assertTrue(exchange.isDurable());
        assertFalse(exchange.isAutoDelete());
        assertEquals(RabbitMqReliabilityConfiguration.FAILED_QUEUE, queue.getName());
        assertTrue(queue.isDurable());
        assertEquals(RabbitMqReliabilityConfiguration.FAILED_EXCHANGE, binding.getExchange());
        assertEquals(RabbitMqReliabilityConfiguration.FAILED_QUEUE, binding.getDestination());
        assertEquals(RabbitMqReliabilityConfiguration.FAILED_ROUTING_KEY, binding.getRoutingKey());
    }

    @Test
    void applicationConfigBoundsConsumerAndPublisherRetries() {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yml"));
        Properties properties = yaml.getObject();

        assertEquals("false", properties.getProperty(
                "spring.rabbitmq.listener.direct.default-requeue-rejected"));
        assertEquals("true", properties.getProperty("spring.rabbitmq.listener.direct.retry.enabled"));
        assertEquals("3", properties.getProperty("spring.rabbitmq.listener.direct.retry.max-attempts"));
        assertEquals("true", properties.getProperty("spring.rabbitmq.template.mandatory"));
        assertEquals("true", properties.getProperty("spring.rabbitmq.template.retry.enabled"));
        assertEquals("3", properties.getProperty("spring.rabbitmq.template.retry.max-attempts"));
        assertEquals("readinessState,db,redis,rabbit",
                properties.getProperty("management.endpoint.health.group.readiness.include"));
        assertEquals("true", properties.getProperty("app.rabbitmq.outbox.enabled"));
        assertEquals("10", properties.getProperty("app.rabbitmq.outbox.max-attempts"));
        assertEquals("5000", properties.getProperty("app.rabbitmq.outbox.confirm-timeout-millis"));
    }

    @Test
    void everyRuntimeProfileUsesCorrelatedPublisherConfirms() {
        for (String profile : new String[] {"dev", "test", "prod"}) {
            YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
            yaml.setResources(new ClassPathResource("application-" + profile + ".yml"));
            Properties properties = yaml.getObject();
            assertEquals("CORRELATED",
                    properties.getProperty("spring.rabbitmq.publisher-confirm-type"), profile);
        }
    }

    @Test
    void requiresPublisherConfirmsForFailureRecovery() {
        RabbitMqReliabilityConfiguration configuration = new RabbitMqReliabilityConfiguration();
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        RabbitProperties properties = new RabbitProperties();

        assertThrows(IllegalStateException.class,
                () -> configuration.rabbitMessageRecoverer(rabbitTemplate, properties));

        properties.setPublisherConfirmType(ConfirmType.SIMPLE);
        assertThrows(IllegalStateException.class,
                () -> configuration.rabbitMessageRecoverer(rabbitTemplate, properties));

        properties.setPublisherConfirmType(ConfirmType.CORRELATED);
        assertInstanceOf(RepublishMessageRecovererWithConfirms.class,
                configuration.rabbitMessageRecoverer(rabbitTemplate, properties));
    }

    private static <T extends Declarable> T find(Declarables topology, Class<T> type) {
        return topology.getDeclarables().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst()
                .orElseThrow();
    }
}
