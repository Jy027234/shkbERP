package com.lframework.xingyun.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lframework.starter.mq.rabbitmq.config.RabbitMqAutoConfiguration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;

class JuggInnerBeanConflictResolverTest {

  private static final String PROJECT_PREFIX = "com.lframework.xingyun.";
  private static final String JUGG_RABBIT_PREFIX =
      "com.lframework.starter.mq.rabbitmq.listeners.mq.";

  @Test
  void removesActualJuggRabbitImportsWhenProjectReplacementsExist() {
    DefaultListableBeanFactory registry = new DefaultListableBeanFactory();
    List<String> listenerNames = List.of(
        "SysNotifyListener", "SysMailMessageListener", "SysSiteMessageListener");

    for (String listenerName : listenerNames) {
      register(registry, "project" + listenerName,
          PROJECT_PREFIX + "template.inner.listeners.mq." + listenerName);
    }
    processRabbitAutoConfiguration(registry);

    for (String listenerName : listenerNames) {
      assertTrue(containsBeanClass(registry, JUGG_RABBIT_PREFIX + listenerName));
    }
    assertTrue(containsBeanClass(registry, JUGG_RABBIT_PREFIX + "ExportTaskListener"));

    new JuggInnerBeanConflictResolver().postProcessBeanDefinitionRegistry(registry);

    for (String listenerName : listenerNames) {
      assertTrue(registry.containsBeanDefinition("project" + listenerName));
      assertFalse(containsBeanClass(registry, JUGG_RABBIT_PREFIX + listenerName));
    }
    assertTrue(containsBeanClass(registry, JUGG_RABBIT_PREFIX + "ExportTaskListener"));
  }

  @Test
  void keepsJuggOnlyRabbitListeners() {
    DefaultListableBeanFactory registry = new DefaultListableBeanFactory();
    register(registry, "juggExportTaskListener",
        JUGG_RABBIT_PREFIX + "ExportTaskListener");

    new JuggInnerBeanConflictResolver().postProcessBeanDefinitionRegistry(registry);

    assertTrue(registry.containsBeanDefinition("juggExportTaskListener"));
  }

  @Test
  void keepsRequiredJuggInnerServices() {
    DefaultListableBeanFactory registry = new DefaultListableBeanFactory();
    register(registry, "projectSysMenuServiceImpl",
        PROJECT_PREFIX + "template.inner.impl.system.SysMenuServiceImpl");
    register(registry, "juggSysMenuServiceImpl",
        "com.lframework.starter.web.inner.impl.system.SysMenuServiceImpl");

    new JuggInnerBeanConflictResolver().postProcessBeanDefinitionRegistry(registry);

    assertTrue(registry.containsBeanDefinition("juggSysMenuServiceImpl"));
  }

  private static void register(DefaultListableBeanFactory registry, String beanName,
      String beanClassName) {
    GenericBeanDefinition definition = new GenericBeanDefinition();
    definition.setBeanClassName(beanClassName);
    registry.registerBeanDefinition(beanName, definition);
  }

  private static void processRabbitAutoConfiguration(DefaultListableBeanFactory registry) {
    new AnnotatedBeanDefinitionReader(registry).register(RabbitMqAutoConfiguration.class);
    new ConfigurationClassPostProcessor().postProcessBeanDefinitionRegistry(registry);
  }

  private static boolean containsBeanClass(DefaultListableBeanFactory registry,
      String beanClassName) {
    for (String beanName : registry.getBeanDefinitionNames()) {
      if (beanClassName.equals(registry.getBeanDefinition(beanName).getBeanClassName())) {
        return true;
      }
    }
    return false;
  }
}
