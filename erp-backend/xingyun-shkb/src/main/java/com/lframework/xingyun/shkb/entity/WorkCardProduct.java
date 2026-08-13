package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;

/**
 * 工卡必换件表
 * @TableName shkb_work_card_product
 */
@TableName(value ="shkb_work_card_product")
@Data
public class WorkCardProduct extends BaseEntity implements BaseDto {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 商品id
     */
    private String productId;

    /**
     * 工卡id
     */
    private String workCardId;

    /**
     * 数量
     */
    private Integer quantity;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}