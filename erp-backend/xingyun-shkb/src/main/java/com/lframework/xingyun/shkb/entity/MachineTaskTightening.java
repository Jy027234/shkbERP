package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;

/**
 * 拧紧机任务表
 * @TableName shkb_machine_task_tightening
 */
@TableName(value ="shkb_machine_task_tightening")
@Data
public class MachineTaskTightening extends BaseEntity implements BaseDto {
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
     * 任务状态 0-待装配 1已完成
     */
    private Integer machineTaskStatus;

    /**
     * 合同号
     */
    private String contractNo;

    /**
     * 序列号
     */
    private String serialNo;

    /**
     * 件号
     */
    private String partNo;
    /**
     * 任务类型 0-平台任务 1-线下任务
     */

    private Integer taskType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 上报时间
     */
    private LocalDateTime reportTime;

    /**
     * 上报数据
     */
    private String reportData;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}