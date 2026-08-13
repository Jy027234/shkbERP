package com.lframework.xingyun.shkb.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.xingyun.shkb.entity.ProductStorage;
import com.lframework.xingyun.shkb.mappers.ProductStorageMapper;
import com.lframework.xingyun.shkb.service.ProductStorageService;
import com.lframework.xingyun.shkb.vo.productstorage.CreateProductStorageVo;
import com.lframework.xingyun.shkb.vo.productstorage.QueryProductStorageVo;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductStorageServiceImpl extends BaseMpServiceImpl<ProductStorageMapper, ProductStorage>
    implements ProductStorageService {

  @Override
  public PageResult<ProductStorage> query(Integer pageIndex, Integer pageSize, QueryProductStorageVo vo) {
    PageHelperUtil.startPage(pageIndex, pageSize);
    List<ProductStorage> datas = this.query(vo);
    return PageResultUtil.convert(new PageInfo<>(datas));
  }

  private List<ProductStorage> query(QueryProductStorageVo vo) {
    LambdaQueryWrapper<ProductStorage> wrapper = Wrappers.lambdaQuery(ProductStorage.class);
    if (vo.getClientName() != null && vo.getClientName().length() > 0) {
      wrapper.like(ProductStorage::getClientName, vo.getClientName());
    }
    if (vo.getProductName() != null && vo.getProductName().length() > 0) {
      wrapper.like(ProductStorage::getProductName, vo.getProductName());
    }
    if (vo.getProductCode() != null && vo.getProductCode().length() > 0) {
      wrapper.like(ProductStorage::getProductCode, vo.getProductCode());
    }
    if (vo.getSerialNumber() != null && vo.getSerialNumber().length() > 0) {
      wrapper.like(ProductStorage::getSerialNumber, vo.getSerialNumber());
    }
    wrapper.orderByDesc(ProductStorage::getCreateTime);

    return getBaseMapper().selectList(wrapper);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public String create(CreateProductStorageVo vo) {
    ProductStorage entity = new ProductStorage();
    entity.setId(IdUtil.getId());
    entity.setClientName(vo.getClientName());
    entity.setProductName(vo.getProductName());
    entity.setProductCode(vo.getProductCode());
    entity.setSerialNumber(vo.getSerialNumber());
    if (vo.getStorageTime() != null && !vo.getStorageTime().isEmpty()) {
      entity.setStorageTime(LocalDateTime.parse(vo.getStorageTime().replace(" ", "T")));
    }
    if (vo.getDeliveryTime() != null && !vo.getDeliveryTime().isEmpty()) {
      entity.setDeliveryTime(LocalDateTime.parse(vo.getDeliveryTime().replace(" ", "T")));
    }
    entity.setDescription(vo.getDescription());
    entity.setStorageTrackingNumber(vo.getStorageTrackingNumber());
    entity.setDeliveryReason(vo.getDeliveryReason());

    getBaseMapper().insert(entity);
    return entity.getId();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteById(String id) {
    getBaseMapper().deleteById(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteByIds(java.util.List<String> ids) {
    if (ids == null || ids.isEmpty()) {
      return;
    }
    getBaseMapper().deleteBatchIds(ids);
  }

  @Override
  public ProductStorage findById(String id) {
    return getBaseMapper().selectById(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void update(ProductStorage entity) {
    entity.setUpdateTime(LocalDateTime.now());
    getBaseMapper().updateById(entity);
  }
}




