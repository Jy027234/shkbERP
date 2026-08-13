package com.lframework.xingyun.api;

import java.util.Set;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 * 项目 Mapper Bean 名称生成器。
 * <p>
 * jugg 5 的 web-starter 通过 {@code @MapperScan("com.lframework.starter.web.**.mappers")}
 * 注册了内置 Mapper；项目 template/inner 与 core/mappers 中存在同名 Mapper，
 * 默认 Bean 名冲突（ConflictingBeanDefinitionException）。
 * 对与 jugg 内置 Mapper 同名的项目 Mapper 加 "xingyun" 前缀，按类型注入不受影响。
 * 注意：jugg 升级新增内置 Mapper 时需同步此集合。
 */
public class XingyunMapperBeanNameGenerator extends AnnotationBeanNameGenerator {

  /**
   * jugg 5 web-starter 内置 inner Mapper（含 system 子包）的默认 Bean 名。
   */
  private static final Set<String> JUGG_INNER_MAPPER_BEAN_NAMES = Set.of("dicCityMapper",
      "generateCodeMapper", "opLogsMapper", "orderTimeLineMapper", "qrtzMapper",
      "recursionMappingMapper", "securityUploadRecordMapper", "sysDataDicCategoryMapper",
      "sysDataDicItemMapper", "sysDataDicMapper", "sysDataPermissionDataMapper",
      "sysDataPermissionModelDetailMapper", "sysDeptMapper", "sysGenerateCodeMapper",
      "sysMailMessageMapper", "sysMenuMapper", "sysModuleMapper", "sysModuleTenantMapper",
      "sysNoticeLogMapper", "sysNoticeMapper", "sysNotifyGroupMapper",
      "sysNotifyGroupReceiverMapper", "sysOpenDomainMapper", "sysParameterMapper",
      "sysRoleCategoryMapper", "sysRoleMapper", "sysRoleMenuMapper", "sysSiteMessageMapper",
      "sysUserDeptMapper", "sysUserGroupDetailMapper", "sysUserGroupMapper", "sysUserMapper",
      "sysUserMenuSortMapper", "sysUserRoleMapper", "sysUserTelephoneMapper", "tenantMapper",
      "userDetailsMapper");

  @Override
  protected String buildDefaultBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {

    String beanName = super.buildDefaultBeanName(definition, registry);
    if (JUGG_INNER_MAPPER_BEAN_NAMES.contains(beanName)) {
      return "xingyun" + Character.toUpperCase(beanName.charAt(0)) + beanName.substring(1);
    }
    return beanName;
  }
}
