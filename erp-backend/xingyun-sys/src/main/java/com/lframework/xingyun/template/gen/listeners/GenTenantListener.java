package com.lframework.xingyun.template.gen.listeners;

import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.lframework.starter.web.core.utils.DataSourceUtil;
import com.lframework.starter.web.core.event.ReloadTenantEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.ssssssss.magicapi.datasource.model.MagicDynamicDataSource;

public class GenTenantListener {

  // jugg 5 的 TenantAutoConfiguration 已注册同名 reloadTenantListener Bean，改名避免冲突。
  // 本监听器专责把租户数据源注册进 magic-api，与 jugg 内置监听器共存。
  @Component("magicReloadTenantListener")
  public static class ReloadTenantListener implements ApplicationListener<ReloadTenantEvent>,
      Ordered {

    @Autowired
    private MagicDynamicDataSource magicDynamicDataSource;

    @Autowired
    private DynamicDataSourceProperties dynamicDataSourceProperties;

    @Override
    public void onApplicationEvent(ReloadTenantEvent event) {
      DataSourceProperty dataSourceProperty = dynamicDataSourceProperties.getDatasource()
          .get("master");
      magicDynamicDataSource.add(String.valueOf(event.getTenantId()),
          DataSourceUtil.createDataSource(dataSourceProperty, event.getJdbcUrl(),
              event.getJdbcUsername(), event.getJdbcPassword(), event.getDriver()));
    }

    @Override
    public int getOrder() {
      return Integer.MAX_VALUE;
    }
  }
}
