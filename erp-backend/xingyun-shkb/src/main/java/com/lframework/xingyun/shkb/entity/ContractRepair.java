package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import com.lframework.xingyun.shkb.enums.ContractStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 合同表
 * @TableName shkb_contract
 */
@TableName(value ="shkb_contract_repair")
@Data
public class ContractRepair extends BaseEntity implements BaseDto {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 维修类型ID
     */
    private String repairTypeId;

    /**
     * 合同ID
     */
    private String contractId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}