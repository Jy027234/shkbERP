package com.lframework.xingyun.shkb.service.contract;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.bo.contract.GetContractBo;
import com.lframework.xingyun.shkb.entity.ContractRepair;

import java.util.List;

/**
 * 合同维修类型服务接口
 *
 * @author kison
 */
public interface ContractRepairService extends BaseMpService<ContractRepair> {

    /**
     * 根据合同ID查询维修类型关联列表
     *
     * @param contractId 合同ID
     * @return 维修类型关联列表
     */
    List<ContractRepair> getByContractId(String contractId);
    
    /**
     * 根据合同ID获取维修类型列表（包含维修类型详情）
     *
     * @param contractId 合同ID
     * @return 维修类型列表
     */
    List<GetContractBo.RepairTypeVo> getRepairTypeListByContractId(String contractId);

    /**
     * 创建合同维修类型关联
     *
     * @param contractId    合同ID
     * @param repairTypeIds 维修类型ID列表
     */
    void createContractRepairs(String contractId, List<String> repairTypeIds);

    /**
     * 更新合同维修类型关联
     *
     * @param contractId    合同ID
     * @param repairTypeIds 维修类型ID列表
     */
    void updateContractRepairs(String contractId, List<String> repairTypeIds);

    /**
     * 根据合同ID删除维修类型关联
     *
     * @param contractId 合同ID
     */
    void deleteByContractId(String contractId);
}
