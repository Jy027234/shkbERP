package com.lframework.xingyun.sc.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;

/**
 * 商品库存批次
 * @TableName tbl_product_stock_batch
 */

@Data
@TableName(value ="tbl_product_stock_batch")
public class ProductStockBatch extends BaseEntity implements BaseDto {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 仓库ID
     */
    private String scId;

    /**
     * 商品ID
     */
    private String productId;

    /**
     * 库存数量
     */
    private Integer quantity;

    /**
     * 批次号
     */
    private String batchNumber;

    /**
     * 架位（货架/库位信息）
     */
    private String shelfLocation;

    /**
     * 生产日期
     */
    private LocalDate productionDate;

    /**
     * 失效日期
     */
    private LocalDate expiryDate;


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