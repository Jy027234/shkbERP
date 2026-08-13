package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;

/**
 * 合同任务
 * @TableName shkb_contract_task
 */
@TableName(value ="shkb_contract_task")
@Data
public class ContractTask extends BaseEntity implements BaseDto {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 合同id
     */
    private String contractId;

    /**
     * 任务状态
     */
    private String taskStatus;

    /**
     * 维修状态
     */
    private String repairStatus;

    /**
     * 航材状态
     */
    private String materialStatus;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建人ID
     */
    @TableField(fill = FieldFill.INSERT)
    private String createById;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 修改人ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateById;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 派发至
     */
    private String dispatch;

    /**
     * 派发给具体人员
     */
    private String taskUserId;

    /**
     * 合同任务备注
     */
    private String description;

    /**
     * 维修备注
     */
    private String repairDescription;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * SH单位合同id
     */
    private String shContractId;
    
    /**
     * 放行文件编号
     */
    private String approvalFileNumber;
    
    /**
     * 合同编号
     */
    @TableField(exist = false)
    private String contractCode;
    
    /**
     * 合同名称
     */
    @TableField(exist = false)
    private String contractName;
    
    /**
     * 客户标识
     */
    @TableField(exist = false)
    private String customerCode;
    
    /**
     * 客户名称
     */
    @TableField(exist = false)
    private String customerName;
    
    /**
     * 机型编号
     */
    @TableField(exist = false)
    private String machineTypeCode;
    
    /**
     * 机型名称
     */
    @TableField(exist = false)
    private String machineTypeName;
    
    /**
     * 件号编号
     */
    @TableField(exist = false)
    private String partNumberCode;
    
    /**
     * 件号名称
     */
    @TableField(exist = false)
    private String partNumberName;
    
    /**
     * 产品序号
     */
    @TableField(exist = false)
    private String serialNumber;

    /**
     * 其他工卡
     */
    private String otherWorkCardNumber;

    /**
     * 是否发料出库 false 未发料 true 已发料
     */
    private Boolean isMaterialIssued;

    /**
     * 退修原因
     */
    private String returnRepairReason;


    /**
     * 入库时间
     */
    @TableField(exist = false)
    private LocalDateTime storageTime;
    
    /**
     * 计划完工时间
     */
    @TableField(exist = false)
    private LocalDateTime plannedCompletionTime;
    
    /**
     * 其他维修需求
     */
    @TableField(exist = false)
    private String otherRepairRequirements;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}