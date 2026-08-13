package com.lframework.xingyun.basedata.impl.machineType;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.Assert;
import com.lframework.starter.common.utils.StringUtil;

import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.basedata.entity.MachineType;
import com.lframework.xingyun.basedata.service.machineType.MachineTypeService;
import com.lframework.xingyun.basedata.mappers.MachineTypeMapper;
import com.lframework.xingyun.basedata.vo.machineType.CreateMachineTypeVo;
import com.lframework.xingyun.basedata.vo.machineType.MachineTypeSelectorVo;
import com.lframework.xingyun.basedata.vo.machineType.QueryMachineTypeVo;
import com.lframework.xingyun.basedata.vo.machineType.UpdateMachineTypeVo;
import com.github.pagehelper.PageInfo;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.mappers.ProductMapper;

/**
* @author kison
* @description 针对表【base_data_machine_type(机型)】的数据库操作Service实现
* @createDate 2025-04-22 14:54:44
*/
@Service
public class MachineTypeServiceImpl extends BaseMpServiceImpl<MachineTypeMapper, MachineType>
    implements MachineTypeService{

  @Autowired
  private ProductMapper productMapper;

  @Override
  public PageResult<MachineType> query(Integer pageIndex, Integer pageSize, QueryMachineTypeVo vo) {
    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    Page<MachineType> page = new Page<>(pageIndex, pageSize);
    page.setSearchCount(true);
    page.setOptimizeCountSql(true);

    LambdaQueryWrapper<MachineType> queryWrapper = Wrappers.lambdaQuery(MachineType.class)
        .orderByAsc(MachineType::getCode);
    if (StringUtil.isNotBlank(vo.getCode())) {
      queryWrapper.like(MachineType::getCode, StringUtil.format("%{}%", vo.getCode()));
    }
    if (StringUtil.isNotBlank(vo.getName())) {
      queryWrapper.like(MachineType::getName, StringUtil.format("%{}%", vo.getName()));
    }
    if (vo.getAvailable() != null) {
      queryWrapper.eq(MachineType::getAvailable, vo.getAvailable());
    }

    Page<MachineType> result = this.page(page, queryWrapper);

    return PageResultUtil.convert(result);
  }

  @Cacheable(value = MachineType.CACHE_NAME, key = "#id", unless = "#result == null")
  @Override
  public MachineType findById(String id) {
    return this.getById(id);
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public String create(CreateMachineTypeVo vo) {
    Wrapper<MachineType> checkWrapper = Wrappers.lambdaQuery(MachineType.class)
        .eq(MachineType::getCode, vo.getCode());
    if (this.count(checkWrapper) > 0) {
      throw new DefaultClientException("编号重复，请重新输入！");
    }

    Wrapper<MachineType> checkNameWrapper = Wrappers.lambdaQuery(MachineType.class)
            .eq(MachineType::getName, vo.getName());
    if (this.count(checkNameWrapper) > 0) {
      throw new DefaultClientException("机型重复，请重新输入！");
    }

    MachineType data = new MachineType();
    data.setCode(vo.getCode());
    data.setName(vo.getName());
    data.setAvailable(vo.getAvailable());
    data.setDescription(StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription());

    this.save(data);

    return data.getId();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(UpdateMachineTypeVo vo) {
    MachineType data = this.getById(vo.getId());
    if (data == null) {
      throw new DefaultClientException("机型不存在！");
    }

    LambdaQueryWrapper<MachineType> checkWrapper = Wrappers.lambdaQuery(MachineType.class)
        .eq(MachineType::getCode, data.getCode())
        .ne(MachineType::getId, vo.getId());
    if (this.count(checkWrapper) > 0) {
      throw new DefaultClientException("编号重复，请重新输入！");
    }

    LambdaQueryWrapper<MachineType> checkNameWrapper = Wrappers.lambdaQuery(MachineType.class)
            .eq(MachineType::getName, data.getName())
            .ne(MachineType::getId, vo.getId());
    if (this.count(checkNameWrapper) > 0) {
      throw new DefaultClientException("机型重复，请重新输入！");
    }

    data.setName(vo.getName());
    data.setAvailable(vo.getAvailable());
    data.setDescription(StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription());

    this.updateById(data);
  }

  @CacheEvict(value = MachineType.CACHE_NAME, key = "#key")
  @Override
  public void cleanCacheByKey(String key) {
  }
  
  @Override
  public PageResult<MachineType> selector(Integer pageIndex, Integer pageSize, MachineTypeSelectorVo vo) {
    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);
    List<MachineType> datas = getBaseMapper().selector(vo);

    return PageResultUtil.convert(new PageInfo<>(datas));
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deleteById(String id) {
    MachineType data = this.getById(id);
    if (data == null) {
      throw new DefaultClientException("机型不存在！");
    }

    // 先清空商品上与该机型的关联
    LambdaUpdateWrapper<Product> clearWrapper = Wrappers.lambdaUpdate(Product.class)
        .set(Product::getMachineTypeId, null)
        .eq(Product::getMachineTypeId, id);
    productMapper.update(null, clearWrapper);

    // 删除机型
    this.removeById(id);

    // 清除缓存
    this.cleanCacheByKey(id);
  }
}
