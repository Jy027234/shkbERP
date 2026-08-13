package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import com.lframework.xingyun.shkb.enums.ContractStatus;
import com.lframework.xingyun.shkb.enums.ContractType;
import lombok.Data;

/**
 * 合同表
 * @TableName shkb_contract
 */
@TableName(value ="shkb_contract")
@Data
public class Contract extends BaseEntity implements BaseDto {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 合同编号
     */
    private String code;

    /**
     * 合同名称
     */
    private String name;

    /**
     * 状态
     */
    private Boolean available;

    /**
     * 备注
     */
    private String description;

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

    // 维修类垍id已移至ContractRepair实体类中

    /**
     * 件号id
     */
    private String partNumberId;

    /**
     * 合同时间
     */
    private LocalDateTime contractTime;

    /**
     * 客户标识
     */
    private String customerId;

    /**
     * 产品序号
     */
    private String serialNumber;

    /**
     * 其他维修需求
     */
    private String otherRepairRequirements;

    /**
     * 入库时间
     */
    private LocalDateTime storageTime;

    /**
     * 计划完工时间
     */
    private LocalDateTime plannedCompletionTime;

    /**
     * 发货时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 合同报价
     */
    private BigDecimal contractPrice;

    /**
     * 更换件价格
     */
    private BigDecimal replacementPartPrice;
    /**
     * 合同类型
     */
    private Integer contractType;

    /**
     * 合同进度
     */
    private ContractStatus contractStatus;

    /**
     * 实际完工时间
     */
    private LocalDateTime actualCompletionTime;

    /**
     * 来源合同任务id
     */
    private String fromContractTaskId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}