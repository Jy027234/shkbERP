package com.lframework.xingyun.api;

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * jugg 5 内置 inner 栈与项目 template/inner 栈的冲突处理器。
 * <p>
 * jugg 5 的 WebBeanAutoConfiguration 通过 @Import 注册了一批
 * {@code com.lframework.starter.web.inner.} 下的 impl/controller，
 * 与项目 {@code com.lframework.xingyun.} 下的同名实现重复，
 * 导致接口注入歧义与 URL 映射冲突。
 * <p>
 * 处理规则：jugg inner Bean 或 RabbitMQ Listener 若存在同名（简单类名）的项目 Bean，
 * 则移除 jugg 的；
 * 项目没有对应实现的（如 SecurityUploadRecordServiceImpl、SysUserGroupServiceImpl
 * 等 12 个）保留，保证 jugg 独有功能可用。
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class JuggInnerBeanConflictResolver implements BeanDefinitionRegistryPostProcessor {

  private static final String JUGG_INNER_PREFIX = "com.lframework.starter.web.inner.";

  private static final String JUGG_RABBIT_LISTENER_PREFIX =
      "com.lframework.starter.mq.rabbitmq.listeners.";

  private static final String PROJECT_PREFIX = "com.lframework.xingyun.";

  /**
   * 不移除的 jugg inner Bean（简单类名）。
   * jugg 组件（TenantAutoConfiguration、rabbitmq-starter 消息监听器）直接注入 jugg inner
   * 服务接口；这些接口使用 jugg inner 实体/VO 类型，项目实现类使用项目自有类型、无法替代。
   * 以下为 jugg 独有 Bean 与直接/传递依赖的静态闭包（共 24 个）：
   * 这些 jugg 实现与项目实现按类型各自注入，共存于同一批表上。
   */
  private static final Set<String> KEEP_SIMPLE_NAMES = Set.of("DefaultUserDetailsService",
      "GenerateCodeServiceImpl", "RecursionMappingServiceImpl", "SecurityUploadRecordServiceImpl",
      "SysMailMessageServiceImpl", "SysMenuServiceImpl",
      "SysNotifyGroupForDeleteSysUserGroupListener", "SysNotifyGroupReceiverServiceImpl",
      "SysNotifyGroupServiceImpl", "SysRoleCategoryController", "SysRoleCategoryServiceImpl",
      "SysRoleServiceImpl", "SysSiteMessageServiceImpl", "SysUserDeptController",
      "SysUserDeptForDeleteSysDeptListener", "SysUserDeptServiceImpl", "SysUserGroupController",
      "SysUserGroupDetailServiceImpl", "SysUserGroupServiceImpl", "SysUserMenuSortServiceImpl",
      "SysUserRoleForDeleteSysRoleListener", "SysUserRoleServiceImpl", "SysUserServiceImpl",
      "TenantServiceImpl", "SysModuleServiceImpl", "SysModuleTenantServiceImpl");

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

    // 收集项目侧 Bean 的简单类名
    Set<String> projectSimpleNames = new HashSet<>();
    for (String name : registry.getBeanDefinitionNames()) {
      BeanDefinition bd = registry.getBeanDefinition(name);
      String className = bd.getBeanClassName();
      if (className != null && className.startsWith(PROJECT_PREFIX)) {
        projectSimpleNames.add(simpleName(className));
      }
    }

    // 移除与项目同名的 jugg inner Bean 和 RabbitMQ Listener。jugg 独有的导出 Listener
    // 没有项目侧同名实现，因此会继续保留。
    int removed = 0;
    for (String name : registry.getBeanDefinitionNames()) {
      BeanDefinition bd = registry.getBeanDefinition(name);
      String className = bd.getBeanClassName();
      if (className != null && isReplaceableJuggBean(className)
          && projectSimpleNames.contains(simpleName(className))
          && !KEEP_SIMPLE_NAMES.contains(simpleName(className))) {
        registry.removeBeanDefinition(name);
        removed++;
      }
    }
    if (removed > 0) {
      System.out.println("[JuggInnerBeanConflictResolver] removed " + removed
          + " conflicting jugg bean definitions");
    }
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    // no-op
  }

  private static String simpleName(String className) {

    int idx = className.lastIndexOf('.');
    return idx < 0 ? className : className.substring(idx + 1);
  }

  private static boolean isReplaceableJuggBean(String className) {

    return className.startsWith(JUGG_INNER_PREFIX)
        || className.startsWith(JUGG_RABBIT_LISTENER_PREFIX);
  }
}
