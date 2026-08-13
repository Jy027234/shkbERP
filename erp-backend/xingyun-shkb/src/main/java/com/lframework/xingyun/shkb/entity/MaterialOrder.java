package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;

/**
 * 发料单
 * @TableName shkb_material_order
 */
@TableName(value ="shkb_material_order")
@Data
public class MaterialOrder extends BaseEntity implements BaseDto {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 单号
     */
    private String code;

    /**
     * 仓库ID
     */
    private String scId;

    /**
     * 发料数量
     */
    private Integer totalNum;

    /**
     * 已发料数量
     */
    private Integer totalOutNum;

    /**
     * 发料金额
     */
    private BigDecimal totalAmount;

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
     * 发料申请单ID
     */
    private String materialApplyId;

    /**
     * 是否出库完毕
     */
    private Boolean isOutFinish;

    /**
     * 仓库名称
     */
    @TableField(exist = false)
    private String scName;

    /**
     * 发料申请单号
     */
    @TableField(exist = false)
    private String materialApplyCode;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}