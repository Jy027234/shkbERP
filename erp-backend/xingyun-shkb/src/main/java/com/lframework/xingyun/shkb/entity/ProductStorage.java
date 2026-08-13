package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lframework.starter.web.core.dto.BaseDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 成品出入库
 * @TableName shkb_product_storage
 */
@TableName(value ="shkb_product_storage")
@Data
public class ProductStorage implements Serializable, BaseDto {
    /**
     * ID
     */
    @TableId
    private String id;

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
     * 客户名称
     */
    private String clientName;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 件号
     */
    private String productCode;

    /**
     * 序列号
     */
    private String serialNumber;

    /**
     * 入库时间
     */
    private LocalDateTime storageTime;

    /**
     * 出库时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 备注
     */
    private String description;

    /**
     * 入库单号
     */
    private String storageTrackingNumber;

    /**
     * 出库原因
     */
    private String deliveryReason;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}