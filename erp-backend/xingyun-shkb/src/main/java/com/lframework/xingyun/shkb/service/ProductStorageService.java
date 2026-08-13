package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.ProductStorage;
import com.lframework.xingyun.shkb.vo.productstorage.CreateProductStorageVo;
import com.lframework.xingyun.shkb.vo.productstorage.QueryProductStorageVo;

public interface ProductStorageService extends BaseMpService<ProductStorage> {

  PageResult<ProductStorage> query(Integer pageIndex, Integer pageSize, QueryProductStorageVo vo);

  String create(CreateProductStorageVo vo);

  void deleteById(String id);

  void deleteByIds(java.util.List<String> ids);

  ProductStorage findById(String id);

  void update(ProductStorage entity);
}
