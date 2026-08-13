package com.lframework.xingyun.sc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 库存初始化导入-数据快照
 * @TableName stock_init_import_staging
 */
@TableName(value ="stock_init_import_staging")
@Data
public class StockInitImportStaging implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String importBatchId;

    /**
     * 
     */
    private String importLineId;

    /**
     * 可选：自然键哈希(product+sc+batch+serials)
     */
    private String naturalKeyHash;

    /**
     * 
     */
    private String productId;

    /**
     * 仓库ID
     */
    private String scId;

    /**
     * 数量(>0)
     */
    private Integer qty;

    /**
     * 含税价
     */
    private BigDecimal taxPrice;

    /**
     * 批次号；未启用批次时可为DEFAULT
     */
    private String batchNumber;

    /**
     * 
     */
    private Date productionDate;

    /**
     * 
     */
    private Date expiryDate;

    /**
     * 供应商ID（可选，根据导入的供应商名称匹配）
     */
    private String supplierId;

    /**
     * 
     */
    private String remark;

    /**
     * 
     */
    private LocalDateTime createdAt;

    /**
     * 
     */
    private LocalDateTime updatedAt;

    /**
     * 架位
     */
    private String shelfLocation;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}