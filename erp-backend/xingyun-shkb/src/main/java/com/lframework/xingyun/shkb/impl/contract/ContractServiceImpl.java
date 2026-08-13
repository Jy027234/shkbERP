package com.lframework.xingyun.shkb.impl.contract;

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
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.basedata.mappers.ProductMapper;
import com.lframework.xingyun.core.annotations.OpLog;
import com.lframework.xingyun.core.enums.DefaultOpLogType;
import com.lframework.xingyun.core.utils.OpLogUtil;
import com.lframework.xingyun.shkb.bo.contract.GetContractBo;
import com.lframework.xingyun.shkb.bo.contract.QueryContractBo;
import com.lframework.xingyun.shkb.entity.Contract;
import com.lframework.xingyun.shkb.enums.ContractStatus;
import com.lframework.xingyun.shkb.mappers.ContractMapper;
import com.lframework.xingyun.shkb.service.contract.ContractService;
import com.lframework.xingyun.shkb.service.contract.ContractRepairService;
import com.lframework.xingyun.shkb.vo.contract.CreateContractVo;
import com.lframework.xingyun.shkb.vo.contract.QueryContractVo;
import com.lframework.xingyun.shkb.vo.contract.UpdateContractVo;
import com.lframework.xingyun.shkb.vo.contract.UpdateContractStatusVo;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 合同服务实现类
 *
 * @author kison
 */
@Service
public class ContractServiceImpl extends BaseMpServiceImpl<ContractMapper, Contract>
    implements ContractService {
    
    private final ContractRepairService contractRepairService;    
    @Autowired
    private ProductMapper productMapper;
    
    public ContractServiceImpl(ContractRepairService contractRepairService,
        ProductMapper productMapper) {
        this.contractRepairService = contractRepairService;
    }

    @Override
    public PageResult<QueryContractBo> query(Integer pageIndex, Integer pageSize, QueryContractVo vo) {
        Assert.greaterThanZero(pageIndex);
        Assert.greaterThanZero(pageSize);

        PageHelperUtil.startPage(pageIndex, pageSize);
        List<QueryContractBo> datas = this.query(vo);

        return PageResultUtil.convert(new PageInfo<>(datas));
    }

    @Override
    public List<QueryContractBo> query(QueryContractVo vo) {
        return getBaseMapper().query(vo);
    }

//    @Cacheable(value = "shkb:contract:id", key = "@cacheVariables.tenantId() + #id", unless = "#result == null")
    @Override
    public Contract findById(String id) {
        return getBaseMapper().selectById(id);
    }
    
//    @Cacheable(value = "shkb:contract:detail", key = "@cacheVariables.tenantId() + #id", unless = "#result == null")
    @Override
    public GetContractBo getDetail(String id) {
        return getBaseMapper().getDetail(id);
    }

    @OpLog(type = DefaultOpLogType.OTHER, name = "新增合同，ID：{}, 编号：{}", params = {"#id", "#code"})
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String create(CreateContractVo vo) {
        Wrapper<Contract> checkWrapper = Wrappers.lambdaQuery(Contract.class)
            .eq(Contract::getCode, vo.getCode());
        if (getBaseMapper().selectCount(checkWrapper) > 0) {
            throw new DefaultClientException("编号重复，请重新输入！");
        }

        // 校验件号对应航材是否存在
        Assert.notBlank(vo.getPartNumberId(), "件号不能为空！");
        if (productMapper.selectById(vo.getPartNumberId()) == null) {
            throw new DefaultClientException("件号对应的航材不存在！");
        }

        Contract data = new Contract();
        data.setId(IdUtil.getId());
        data.setCode(vo.getCode());
        data.setName(vo.getName());
        data.setAvailable(Boolean.TRUE);
        data.setDescription(StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription());
        data.setContractType(vo.getContractType());
        data.setCustomerId(vo.getCustomerId());
        data.setPartNumberId(vo.getPartNumberId());
        // 维修类型已移至ContractRepair表
        data.setSerialNumber(vo.getSerialNumber());
        data.setOtherRepairRequirements(vo.getOtherRepairRequirements());
        data.setContractTime(vo.getContractTime());
        data.setStorageTime(vo.getStorageTime());
        data.setPlannedCompletionTime(vo.getPlannedCompletionTime());
        data.setActualCompletionTime(vo.getActualCompletionTime());
        data.setContractPrice(vo.getContractPrice());
        data.setReplacementPartPrice(vo.getReplacementPartPrice());
        // 设置默认合同状态为待生成合同任务
        data.setContractStatus(ContractStatus.WAIT_CREATE);

        getBaseMapper().insert(data);
        
        // 创建合同维修类型关联
        contractRepairService.createContractRepairs(data.getId(), vo.getRepairTypeIds());

        OpLogUtil.setVariable("id", data.getId());
        OpLogUtil.setVariable("code", vo.getCode());
        OpLogUtil.setExtra(vo);

        return data.getId();
    }

    @OpLog(type = DefaultOpLogType.OTHER, name = "修改合同，ID：{}, 编号：{}", params = {"#id", "#code"})
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(UpdateContractVo vo) {
        Contract data = getBaseMapper().selectById(vo.getId());
        if (ObjectUtil.isNull(data)) {
            throw new DefaultClientException("合同不存在！");
        }

        Wrapper<Contract> checkWrapper = Wrappers.lambdaQuery(Contract.class)
            .eq(Contract::getCode, vo.getCode())
            .ne(Contract::getId, vo.getId());
        if (getBaseMapper().selectCount(checkWrapper) > 0) {
            throw new DefaultClientException("编号重复，请重新输入！");
        }

        // 校验件号对应航材是否存在
        Assert.notBlank(vo.getPartNumberId(), "件号不能为空！");
        if (productMapper.selectById(vo.getPartNumberId()) == null) {
            throw new DefaultClientException("件号对应的航材不存在！");
        }

        LambdaUpdateWrapper<Contract> updateWrapper = Wrappers.lambdaUpdate(Contract.class)
            .set(Contract::getCode, vo.getCode())
            .set(Contract::getName, vo.getName())
            .set(Contract::getAvailable, vo.getAvailable())
            .set(Contract::getDescription, StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription())
            .set(Contract::getContractType, vo.getContractType())
            .set(Contract::getCustomerId, vo.getCustomerId())
            .set(Contract::getPartNumberId, vo.getPartNumberId())
            // 维修类型已移至ContractRepair表
            .set(Contract::getSerialNumber, vo.getSerialNumber())
            .set(Contract::getOtherRepairRequirements, vo.getOtherRepairRequirements())
            .set(Contract::getContractTime, vo.getContractTime())
            .set(Contract::getStorageTime, vo.getStorageTime())
            .set(Contract::getPlannedCompletionTime, vo.getPlannedCompletionTime())
            .set(Contract::getContractPrice, vo.getContractPrice())
            .set(Contract::getReplacementPartPrice, vo.getReplacementPartPrice())
            .eq(Contract::getId, vo.getId());

        if (vo.getDeliveryTime() != null) {
            updateWrapper.set(Contract::getDeliveryTime, vo.getDeliveryTime());
        }

        if (vo.getActualCompletionTime() != null) {
            updateWrapper.set(Contract::getActualCompletionTime, vo.getActualCompletionTime());
        }

        getBaseMapper().update(null, updateWrapper);
        
        // 更新合同维修类型关联
        contractRepairService.updateContractRepairs(data.getId(), vo.getRepairTypeIds());

        OpLogUtil.setVariable("id", data.getId());
        OpLogUtil.setVariable("code", vo.getCode());
        OpLogUtil.setExtra(vo);
    }

    @OpLog(type = DefaultOpLogType.OTHER, name = "删除合同，ID：{}", params = {"#id"})
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(String id) {
        Contract data = getBaseMapper().selectById(id);
        if (ObjectUtil.isNull(data)) {
            throw new DefaultClientException("合同不存在！");
        }

        // 先删除合同维修类型关联
        contractRepairService.deleteByContractId(id);
        
        // 再删除合同
        getBaseMapper().deleteById(id);
    }

    @CacheEvict(value = {"shkb:contract:id", "shkb:contract:detail"}, key = "@cacheVariables.tenantId() + #key")
    @Override
    public void cleanCacheByKey(String key) {
        // 清除缓存
    }
    
    @OpLog(type = DefaultOpLogType.OTHER, name = "修改合同状态，ID：{}, 状态：{}", params = {"#id", "#status"})
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateStatus(UpdateContractStatusVo vo) {
        // 获取合同
        Contract contract = this.getById(vo.getContractId());
        if (contract == null) {
            throw new DefaultClientException("合同不存在！");
        }
        
        // 根据状态码获取对应的枚举值
        ContractStatus contractStatus = null;
        for (ContractStatus status : ContractStatus.values()) {
            if (status.getCode().toString().equals(vo.getContractStatus()) || 
                status.name().equals(vo.getContractStatus())) {
                contractStatus = status;
                break;
            }
        }
        
        if (contractStatus == null) {
            throw new DefaultClientException("无效的合同状态！");
        }
        
        // 更新合同状态
        LambdaUpdateWrapper<Contract> updateWrapper = Wrappers.lambdaUpdate(Contract.class)
            .set(Contract::getContractStatus, contractStatus)
            .eq(Contract::getId, vo.getContractId());
            
        // 如果有备注，更新备注
        if (vo.getRemark() != null && !vo.getRemark().isEmpty()) {
            updateWrapper.set(Contract::getDescription, vo.getRemark());
        }
        
        // 保存更新
        this.update(updateWrapper);
        
        // 记录日志变量
        OpLogUtil.setVariable("id", vo.getContractId());
        OpLogUtil.setVariable("status", contractStatus.getDesc());
        OpLogUtil.setExtra(vo);
        
        // 清除缓存
        this.cleanCacheByKey(vo.getContractId());
    }
}
