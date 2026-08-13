package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 磁粉机任务表
 * @TableName shkb_machine_task_magnetic_powder
 */
@TableName(value ="shkb_machine_task_magnetic_powder")
@Data
public class MachineTaskMagneticPowder implements Serializable {
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
     * 合同号
     */
    private String contractNo;

    /**
     * 件号
     */
    private String partNo;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 序列号
     */
    private String serialNo;

    /**
     * 任务状态 0-待下发 1已下发
     */
    private Integer machineTaskStatus;

    /**
     * 下发时间
     */
    private LocalDateTime sendTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}