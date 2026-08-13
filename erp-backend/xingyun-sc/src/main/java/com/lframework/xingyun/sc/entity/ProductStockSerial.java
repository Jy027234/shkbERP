package com.lframework.xingyun.sc.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;

/**
 * 商品唯一码表
 * @TableName tbl_product_stock_serial
 */
@TableName(value ="tbl_product_stock_serial")
@Data
public class ProductStockSerial extends BaseEntity implements BaseDto {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 商品ID
     */
    private String productId;

    /**
     * 唯一序列号
     */
    private String serialNumber;

    /**
     * 在库状态
     */
    private Integer stockStatus;

    /**
     * 所属批次id
     */
    private String batchId;

    /**
     * 生产日期
     */
    private LocalDate productionDate;

    /**
     * 失效日期
     */
    private LocalDate expiryDate;

    /**
     * 架位（货架/库位信息）
     */
    private String shelfLocation;

    /**
     * 供应商ID
     */
    private String supplierId;

    /**
     * 创建时间 新增时赋值
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}