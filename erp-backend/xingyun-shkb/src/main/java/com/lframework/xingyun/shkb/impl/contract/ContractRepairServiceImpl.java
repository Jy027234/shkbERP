package com.lframework.xingyun.shkb.impl.contract;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.xingyun.basedata.entity.RepairType;
import com.lframework.xingyun.basedata.mappers.RepairTypeMapper;
import com.lframework.xingyun.shkb.bo.contract.GetContractBo;
import com.lframework.xingyun.shkb.entity.ContractRepair;
import com.lframework.xingyun.shkb.mappers.ContractRepairMapper;
import com.lframework.xingyun.shkb.service.contract.ContractRepairService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 合同维修类型服务实现类
 *
 * @author kison
 */
@Service
public class ContractRepairServiceImpl extends BaseMpServiceImpl<ContractRepairMapper, ContractRepair>
    implements ContractRepairService {

    private final RepairTypeMapper repairTypeMapper;

    public ContractRepairServiceImpl(RepairTypeMapper repairTypeMapper) {
        this.repairTypeMapper = repairTypeMapper;
    }

    @Override
    public List<ContractRepair> getByContractId(String contractId) {
        Wrapper<ContractRepair> queryWrapper = Wrappers.lambdaQuery(ContractRepair.class)
            .eq(ContractRepair::getContractId, contractId);
        return getBaseMapper().selectList(queryWrapper);
    }
    
    @Override
    public List<GetContractBo.RepairTypeVo> getRepairTypeListByContractId(String contractId) {
        // 获取合同维修类型关联
        List<ContractRepair> contractRepairs = getByContractId(contractId);
        if (contractRepairs.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 将维修类型关联转换为维修类型列表
        List<GetContractBo.RepairTypeVo> result = new ArrayList<>();
        for (ContractRepair contractRepair : contractRepairs) {
            RepairType repairType = repairTypeMapper.selectById(contractRepair.getRepairTypeId());
            if (repairType != null) {
                GetContractBo.RepairTypeVo vo = new GetContractBo.RepairTypeVo();
                vo.setId(repairType.getId());
                vo.setCode(repairType.getCode());
                vo.setName(repairType.getName());
                result.add(vo);
            }
        }
        
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createContractRepairs(String contractId, List<String> repairTypeIds) {
        if (repairTypeIds == null || repairTypeIds.isEmpty()) {
            return;
        }

        List<ContractRepair> records = new ArrayList<>();
        for (String repairTypeId : repairTypeIds) {
            ContractRepair record = new ContractRepair();
            record.setId(IdUtil.getId());
            record.setContractId(contractId);
            record.setRepairTypeId(repairTypeId);
            records.add(record);
        }

        saveBatch(records);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateContractRepairs(String contractId, List<String> repairTypeIds) {
        // 先删除原有关联
        deleteByContractId(contractId);
        // 再创建新关联
        createContractRepairs(contractId, repairTypeIds);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByContractId(String contractId) {
        LambdaQueryWrapper<ContractRepair> queryWrapper = Wrappers.lambdaQuery(ContractRepair.class)
            .eq(ContractRepair::getContractId, contractId);
        getBaseMapper().delete(queryWrapper);
    }
}
