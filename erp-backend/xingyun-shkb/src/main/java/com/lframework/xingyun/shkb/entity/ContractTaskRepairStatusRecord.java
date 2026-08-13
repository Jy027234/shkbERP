package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;

/**
 * 合同任务维修状态记录
 * @TableName shkb_contract_task_repair_status_record
 */
@TableName(value ="shkb_contract_task_repair_status_record")
@Data
public class ContractTaskRepairStatusRecord extends BaseEntity implements BaseDto {
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
     * 派发给具体人员
     */
    private String taskId;

    /**
     * 备注
     */
    private String description;

    /**
     * 维修状态
     */
    private String repairStatus;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}