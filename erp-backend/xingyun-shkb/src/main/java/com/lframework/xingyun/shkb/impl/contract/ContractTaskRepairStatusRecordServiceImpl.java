package com.lframework.xingyun.shkb.impl.contract;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.utils.EnumUtil;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.mappers.ProductMapper;
import com.lframework.xingyun.shkb.bo.contract.task.ContractTaskRepairStatusRecordBo;
import com.lframework.xingyun.shkb.entity.*;
import com.lframework.xingyun.shkb.enums.RepairStatus;
import com.lframework.xingyun.shkb.enums.ContractStatus;
import com.lframework.xingyun.shkb.mappers.ContractMapper;
import com.lframework.xingyun.shkb.mappers.ContractTaskRepairStatusRecordMapper;
import com.lframework.xingyun.shkb.service.contract.ContractTaskRepairStatusRecordService;
import com.lframework.xingyun.shkb.service.contract.ContractTaskService;
import com.lframework.xingyun.shkb.vo.contract.task.CreateContractTaskRepairStatusRecordVo;
import com.lframework.xingyun.shkb.mappers.MachineTaskMagneticPowderMapper;
import com.lframework.xingyun.shkb.mappers.MachineTaskTighteningMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

/**
* @author kison
* @description 针对表【shkb_contract_task_repair_status_record(合同任务维修状态记录)】的数据库操作Service实现
* @createDate 2025-05-28 16:00:01
*/
@Service
@Slf4j
public class ContractTaskRepairStatusRecordServiceImpl extends BaseMpServiceImpl<ContractTaskRepairStatusRecordMapper, ContractTaskRepairStatusRecord>
    implements ContractTaskRepairStatusRecordService {
    
    @Autowired
    private ContractTaskService contractTaskService;

    @Autowired
    private MachineTaskMagneticPowderMapper machineTaskMagneticPowderMapper;

    @Autowired
    private MachineTaskTighteningMapper machineTaskTighteningMapper;

    @Autowired
    private ContractMapper contractMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 创建合同任务维修状态记录
     *
     * @param vo 创建维修状态记录信息
     * @return 维修状态记录ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(CreateContractTaskRepairStatusRecordVo vo) {
        // 查询任务是否存在
        ContractTask task = contractTaskService.getById(vo.getTaskId());
        if (task == null) {
            throw new DefaultClientException("合同任务不存在！");
        }

        // 创建维修状态记录
        ContractTaskRepairStatusRecord record = new ContractTaskRepairStatusRecord();
        record.setId(IdUtil.getId());
        record.setTaskId(vo.getTaskId());
        record.setRepairStatus(vo.getRepairStatus()); // 直接使用字符串类型的维修状态
        record.setDescription(vo.getDescription());
        
        this.save(record);
        
        // 同时更新任务的维修状态
        task.setRepairStatus(vo.getRepairStatus()); // 直接使用字符串类型的维修状态
        contractTaskService.updateById(task);

        // 下发设备任务：检查中 -> 磁粉机；待装配 -> 拧紧机
        String status = vo.getRepairStatus();
        // 获取合同信息
        LambdaQueryWrapper<Contract> contractLambdaQueryWrapper = new LambdaQueryWrapper<>();
        contractLambdaQueryWrapper.eq(Contract::getId, task.getContractId());
        Contract contract = contractMapper.selectOne(contractLambdaQueryWrapper);
        if (contract == null) {
            throw new DefaultClientException("合同不存在！");
        }
        
        // 如果维修状态为完工，则更新合同的实际完工时间和合同状态为待交付
        if (RepairStatus.COMPLETED.getCode().equals(status)) {
            contract.setActualCompletionTime(LocalDateTime.now());
            contract.setContractStatus(ContractStatus.WAIT_DELIVERY);
            contractMapper.updateById(contract);
            log.info("已为合同ID={} 更新实际完工时间并将合同状态变更为待交付", task.getContractId());
        }
        // 获取件号
        LambdaQueryWrapper<Product> contractTaskProductLambdaQueryWrapper = new LambdaQueryWrapper<>();
        contractTaskProductLambdaQueryWrapper.eq(Product::getId, contract.getPartNumberId());
        Product partNumber = productMapper.selectOne(contractTaskProductLambdaQueryWrapper);
        if(partNumber == null) {
            throw new DefaultClientException("件号不存在！");
        }
        if (RepairStatus.CHECKING.getCode().equals(status)) {
            // 若不存在对应磁粉机任务，则创建
            MachineTaskMagneticPowder exist = machineTaskMagneticPowderMapper.selectOne(
                    Wrappers.lambdaQuery(MachineTaskMagneticPowder.class)
                            .eq(MachineTaskMagneticPowder::getTaskId, vo.getTaskId())
            );
            if (exist == null) {
                MachineTaskMagneticPowder mp = new MachineTaskMagneticPowder();
                mp.setId(IdUtil.getId());
                mp.setTaskId(vo.getTaskId());
                mp.setContractNo(contract.getCode());
                mp.setPartNo(partNumber.getCode());
                mp.setSerialNo(contract.getSerialNumber());
                mp.setCreateTime(LocalDateTime.now());
                mp.setMachineTaskStatus(0); // 待下发
                machineTaskMagneticPowderMapper.insert(mp);
                log.info("已为任务ID={} 下发磁粉机任务", vo.getTaskId());
            }
        } else if (RepairStatus.WAIT_ASSEMBLY.getCode().equals(status)) {
            // 若不存在对应拧紧机任务，则创建
            MachineTaskTightening existT = machineTaskTighteningMapper.selectOne(
                    Wrappers.lambdaQuery(MachineTaskTightening.class)
                            .eq(MachineTaskTightening::getTaskId, vo.getTaskId())
            );
            if (existT == null) {
                MachineTaskTightening t = new MachineTaskTightening();
                t.setId(IdUtil.getId());
                t.setTaskId(vo.getTaskId());
                t.setMachineTaskStatus(0); // 待装配
                t.setContractNo(contract.getCode());
                t.setPartNo(partNumber.getCode());
                t.setSerialNo(contract.getSerialNumber());
                t.setTaskType(0); // 平台任务
                t.setCreateTime(LocalDateTime.now());
                machineTaskTighteningMapper.insert(t);
                log.info("已为任务ID={} 下发拧紧机任务", vo.getTaskId());
            }
        }
        
        log.info("添加维修状态记录并更新任务维修状态成功，任务ID：{}，维修状态：{}", vo.getTaskId(), vo.getRepairStatus());
        
        return record.getId();
    }
    
    @Override
    public List<ContractTaskRepairStatusRecordBo> getByTaskId(String taskId) {
        // 构建查询条件
        LambdaQueryWrapper<ContractTaskRepairStatusRecord> queryWrapper = Wrappers.lambdaQuery(ContractTaskRepairStatusRecord.class)
                .eq(ContractTaskRepairStatusRecord::getTaskId, taskId)
                .orderByDesc(ContractTaskRepairStatusRecord::getCreateTime); // 按创建时间降序排序
        
        // 查询数据
        List<ContractTaskRepairStatusRecord> records = this.list(queryWrapper);
        
        // 转换为BO对象
        return records.stream().map(record -> {
            ContractTaskRepairStatusRecordBo bo = new ContractTaskRepairStatusRecordBo(record);
            
            // 设置维修状态名称
            String repairStatusCode = record.getRepairStatus();
            RepairStatus repairStatus = EnumUtil.getByCode(RepairStatus.class, repairStatusCode);
            if (repairStatus != null) {
                bo.setRepairStatusName(repairStatus.getDesc());
            }
            
            return bo;
        }).collect(Collectors.toList());
    }
}
