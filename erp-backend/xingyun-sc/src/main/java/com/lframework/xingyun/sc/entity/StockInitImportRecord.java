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
 * 库存初始化导入-执行记录
 * @TableName stock_init_import_record
 */
@TableName(value ="stock_init_import_record")
@Data
public class StockInitImportRecord implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 导入批次ID
     */
    private String importBatchId;

    /**
     * 批次内行ID（内容哈希或行号）
     */
    private String importLineId;

    /**
     * PENDING/SUCCESS/FAILED
     */
    private String status;

    /**
     * 失败原因
     */
    private String errorMsg;

    /**
     * 重试次数
     */
    private Integer retries;

    /**
     * 
     */
    private LocalDateTime createdAt;

    /**
     * 
     */
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}