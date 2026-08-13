package com.lframework.xingyun.basedata.impl.repairType;

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
import com.lframework.xingyun.core.annotations.OpLog;
import com.lframework.xingyun.basedata.enums.BaseDataOpLogType;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.xingyun.core.utils.OpLogUtil;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.xingyun.basedata.entity.RepairType;
import com.lframework.xingyun.basedata.mappers.RepairTypeMapper;
import com.lframework.xingyun.basedata.service.repairType.RepairTypeService;
import com.lframework.xingyun.basedata.vo.repairType.CreateRepairTypeVo;
import com.lframework.xingyun.basedata.vo.repairType.RepairTypeSelectorVo;
import com.lframework.xingyun.basedata.vo.repairType.QueryRepairTypeVo;
import com.lframework.xingyun.basedata.vo.repairType.UpdateRepairTypeVo;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
* @author kison
* @description 针对表【base_data_repair_type(维修类型)】的数据库操作Service实现
* @createDate 2025-04-23 09:08:37
*/
@Service
public class RepairTypeServiceImpl extends BaseMpServiceImpl<RepairTypeMapper, RepairType>
    implements RepairTypeService {

  @Override
  public PageResult<RepairType> query(Integer pageIndex, Integer pageSize, QueryRepairTypeVo vo) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);
    List<RepairType> datas = this.query(vo);

    return PageResultUtil.convert(new PageInfo<>(datas));
  }

  @Override
  public List<RepairType> query(QueryRepairTypeVo vo) {

    return getBaseMapper().query(vo);
  }

  @Override
  public PageResult<RepairType> selector(Integer pageIndex, Integer pageSize, RepairTypeSelectorVo vo) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);
    List<RepairType> datas = getBaseMapper().selector(vo);

    return PageResultUtil.convert(new PageInfo<>(datas));
  }

  @Cacheable(value = RepairType.CACHE_NAME, key = "@cacheVariables.tenantId() + #id", unless = "#result == null")
  @Override
  public RepairType findById(String id) {

    return getBaseMapper().selectById(id);
  }

  @OpLog(type = BaseDataOpLogType.BASE_DATA, name = "新增维修类型，ID：{}, 编号：{}", params = {"#id",
      "#code"})
  @Transactional(rollbackFor = Exception.class)
  @Override
  public String create(CreateRepairTypeVo vo) {

    Wrapper<RepairType> checkCodeWrapper = Wrappers.lambdaQuery(RepairType.class)
        .eq(RepairType::getCode, vo.getCode());
    if (getBaseMapper().selectCount(checkCodeWrapper) > 0) {
      throw new DefaultClientException("编号重复，请重新输入！");
    }

    Wrapper<RepairType> checkNameWrapper = Wrappers.lambdaQuery(RepairType.class)
        .eq(RepairType::getName, vo.getName());
    if (getBaseMapper().selectCount(checkNameWrapper) > 0) {
      throw new DefaultClientException("名称重复，请重新输入！");
    }

    RepairType data = new RepairType();
    data.setId(IdUtil.getId());
    data.setCode(vo.getCode());
    data.setName(vo.getName());
    data.setAvailable(Boolean.TRUE);
    data.setDescription(
        StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription());

    getBaseMapper().insert(data);

    OpLogUtil.setVariable("id", data.getId());
    OpLogUtil.setVariable("code", vo.getCode());
    OpLogUtil.setExtra(vo);

    return data.getId();
  }

  @OpLog(type = BaseDataOpLogType.BASE_DATA, name = "修改维修类型，ID：{}, 编号：{}", params = {"#id",
      "#code"})
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(UpdateRepairTypeVo vo) {

    RepairType data = getBaseMapper().selectById(vo.getId());
    if (ObjectUtil.isNull(data)) {
      throw new DefaultClientException("维修类型不存在！");
    }

    Wrapper<RepairType> checkWrapper = Wrappers.lambdaQuery(RepairType.class)
        .eq(RepairType::getCode, vo.getCode()).ne(RepairType::getId, vo.getId());
    if (getBaseMapper().selectCount(checkWrapper) > 0) {
      throw new DefaultClientException("编号重复，请重新输入！");
    }

    Wrapper<RepairType> checkNameWrapper = Wrappers.lambdaQuery(RepairType.class)
        .eq(RepairType::getName, vo.getName()).ne(RepairType::getId, vo.getId());
    if (getBaseMapper().selectCount(checkNameWrapper) > 0) {
      throw new DefaultClientException("名称重复，请重新输入！");
    }

    LambdaUpdateWrapper<RepairType> updateWrapper = Wrappers.lambdaUpdate(RepairType.class)
        .set(RepairType::getCode, vo.getCode()).set(RepairType::getName, vo.getName())
        .set(RepairType::getAvailable, vo.getAvailable()).set(RepairType::getDescription,
            StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription())
        .eq(RepairType::getId, vo.getId());

    update(null, updateWrapper);

    OpLogUtil.setVariable("id", data.getId());
    OpLogUtil.setVariable("code", vo.getCode());
    OpLogUtil.setExtra(vo);
  }

  @CacheEvict(value = RepairType.CACHE_NAME, key = "@cacheVariables.tenantId() + #key")
  @Override
  public void cleanCacheByKey(String key) {

  }


}
