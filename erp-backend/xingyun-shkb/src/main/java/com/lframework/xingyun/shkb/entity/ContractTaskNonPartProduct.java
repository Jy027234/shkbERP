package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;

/**
 * 非必换件数量
 * @TableName shkb_contract_task_non_part_product
 */
@TableName(value ="shkb_contract_task_non_part_product")
@Data
public class ContractTaskNonPartProduct extends BaseEntity implements BaseDto {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 商品id
     */
    private String productId;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 原因说明
     */
    private String reason;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}