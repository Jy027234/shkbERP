package com.lframework.xingyun.basedata.impl.partNumber;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.github.pagehelper.PageInfo;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.Assert;
import com.lframework.starter.common.utils.ObjectUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.xingyun.basedata.bo.partNumber.GetPartNumberBo;
import com.lframework.xingyun.core.annotations.OpLog;
import com.lframework.xingyun.basedata.enums.BaseDataOpLogType;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.xingyun.core.utils.OpLogUtil;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.xingyun.basedata.bo.partNumber.PartNumberSelectorBo;
import com.lframework.xingyun.basedata.bo.partNumber.QueryPartNumberBo;
import com.lframework.xingyun.basedata.entity.PartNumber;
import com.lframework.xingyun.basedata.mappers.PartNumberMapper;
import com.lframework.xingyun.basedata.service.partNumber.PartNumberService;
import com.lframework.xingyun.basedata.vo.partNumber.CreatePartNumberVo;
import com.lframework.xingyun.basedata.vo.partNumber.PartNumberSelectorVo;
import com.lframework.xingyun.basedata.vo.partNumber.QueryPartNumberVo;
import com.lframework.xingyun.basedata.vo.partNumber.UpdatePartNumberVo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author kison
* @description 针对表【base_data_part_number(件号)】的数据库操作Service实现
* @createDate 2025-04-23 10:10:53
*/
@Service
public class PartNumberServiceImpl extends BaseMpServiceImpl<PartNumberMapper, PartNumber>
    implements PartNumberService{

  @Autowired
  private PartNumberMapper partNumberMapper;

  @Override
  public PageResult<QueryPartNumberBo> query(Integer pageIndex, Integer pageSize, QueryPartNumberVo vo) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);
    List<QueryPartNumberBo> datas = this.query(vo);

    return PageResultUtil.convert(new PageInfo<>(datas));
  }

  @Override
  public List<QueryPartNumberBo> query(QueryPartNumberVo vo) {

    return getBaseMapper().query(vo);
  }

  @Override
  public PageResult<PartNumberSelectorBo> selector(Integer pageIndex, Integer pageSize,
      PartNumberSelectorVo vo) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);
    List<PartNumberSelectorBo> datas = getBaseMapper().selector(vo);

    return PageResultUtil.convert(new PageInfo<>(datas));
  }

  @Cacheable(value = PartNumber.CACHE_NAME, key = "@cacheVariables.tenantId() + #id", unless = "#result == null")
  @Override
  public PartNumber findById(String id) {

    return getBaseMapper().selectById(id);
  }

  @OpLog(type = BaseDataOpLogType.BASE_DATA, name = "新增件号", params = {"#id", "#code"})
  @Transactional(rollbackFor = Exception.class)
  @Override
  public String create(CreatePartNumberVo vo) {

    Wrapper<PartNumber> checkWrapper = Wrappers.lambdaQuery(PartNumber.class)
        .eq(PartNumber::getCode, vo.getCode());
    if (getBaseMapper().selectCount(checkWrapper) > 0) {
      throw new DefaultClientException("编号重复，请重新输入！");
    }

    PartNumber data = new PartNumber();
    data.setId(IdUtil.getId());
    data.setCode(vo.getCode());
    data.setName(vo.getName());
    data.setAvailable(vo.getAvailable());
    data.setDescription(StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription());
    data.setMachineTypeId(vo.getMachineTypeId());

    getBaseMapper().insert(data);

    OpLogUtil.setVariable("id", data.getId());
    OpLogUtil.setVariable("code", vo.getCode());
    OpLogUtil.setExtra(vo);

    return data.getId();
  }

  @OpLog(type = BaseDataOpLogType.BASE_DATA, name = "修改件号", params = {"#id", "#code"})
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(UpdatePartNumberVo vo) {

    PartNumber data = getBaseMapper().selectById(vo.getId());
    if (ObjectUtil.isNull(data)) {
      throw new DefaultClientException("件号不存在！");
    }

    Wrapper<PartNumber> checkWrapper = Wrappers.lambdaQuery(PartNumber.class)
        .eq(PartNumber::getCode, vo.getCode())
        .ne(PartNumber::getId, vo.getId());
    if (getBaseMapper().selectCount(checkWrapper) > 0) {
      throw new DefaultClientException("编号重复，请重新输入！");
    }

    LambdaUpdateWrapper<PartNumber> updateWrapper = Wrappers.lambdaUpdate(PartNumber.class)
        .set(PartNumber::getCode, vo.getCode())
        .set(PartNumber::getName, vo.getName())
        .set(PartNumber::getAvailable, vo.getAvailable())
        .set(PartNumber::getDescription,
            StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription())
        .set(PartNumber::getMachineTypeId, vo.getMachineTypeId())
        .eq(PartNumber::getId, vo.getId());

    getBaseMapper().update(null, updateWrapper);

    OpLogUtil.setVariable("id", data.getId());
    OpLogUtil.setVariable("code", vo.getCode());
    OpLogUtil.setExtra(vo);
  }
  
  @CacheEvict(value = PartNumber.CACHE_NAME, key = "@cacheVariables.tenantId() + #key")
  @Override
  public void cleanCacheByKey(String key) {
    // 清除缓存
  }

  @Override
  public GetPartNumberBo getPartNumberDetail(String id) {
    PartNumber partNumber = partNumberMapper.selectById(id);
    if (partNumber == null) {
      throw new DefaultClientException("件号不存在！");
    }

    return partNumberMapper.getPartNumberDetail(id);
  }
}
