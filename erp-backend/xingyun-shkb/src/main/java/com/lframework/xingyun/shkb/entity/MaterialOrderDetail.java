package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;

/**
 * 发料单明细
 * @TableName shkb_material_order_detail
 */
@TableName(value ="shkb_material_order_detail")
@Data
public class MaterialOrderDetail extends BaseEntity implements BaseDto {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 商品ID
     */
    private String productId;

    /**
     * 含税单价
     */
    private BigDecimal taxPrice;
    /**
     * 含税金额 单价*数量
     */
    private BigDecimal taxAmount;

    /**
     * 备注
     */
    private String description;

    /**
     * 已出库数量
     */
    private Integer outNum;

    /**
     * 发料数量
     */
    private Integer orderNum;

    /**
     * 组合商品原始明细ID
     */
    private String oriBundleDetailId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}