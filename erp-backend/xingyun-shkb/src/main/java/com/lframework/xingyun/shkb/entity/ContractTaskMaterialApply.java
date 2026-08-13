package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;

/**
 * 领料申请
 * @TableName shkb_contract_task_material_apply
 */
@TableName(value ="shkb_contract_task_material_apply")
@Data
public class ContractTaskMaterialApply extends BaseEntity implements BaseDto {
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
     * 申请编号
     */
    private String applyCode;

    /**
     * 申请时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 审批状态 1 通过 2 拒绝
     */
    private Integer approvalStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}