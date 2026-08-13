package com.lframework.xingyun.sc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 库存初始化导入-序列号快照
 * @TableName stock_init_import_serials
 */
@TableName(value ="stock_init_import_serials")
@Data
public class StockInitImportSerials implements Serializable {
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
     * 
     */
    private String serialNumber;

    /**
     * 
     */
    private LocalDateTime createdAt;

    /**
     * 架位
     */
    private String shelfLocation;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}