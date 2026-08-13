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
 * 合同任务工卡
 * @TableName shkb_contract_task_work_card
 */
@TableName(value ="shkb_contract_task_work_card")
@Data
public class ContractTaskWorkCard extends BaseEntity implements BaseDto {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 合同id
     */
    private String taskId;

    /**
     * 任务状态
     */
    private String workCardId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}