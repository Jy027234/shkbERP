package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 培训实施计划表
 * @TableName shkb_training_implementation
 */
@TableName(value ="shkb_training_implementation")
@Data
public class ShkbTrainingImplementation implements Serializable {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 计划开始日期
     */
    private Date planStartDate;

    /**
     * 计划结束日期
     */
    private Date planEndDate;

    /**
     * 实际开始时间
     */
    private Date actualStartDate;

    /**
     * 实际结束时间
     */
    private Date actualEndDate;

    /**
     * 状态：0-计划中 1-进行中 2-已完成 3-已取消
     */
    private Integer status;

    /**
     * 培训地点
     */
    private String trainingLocation;

    /**
     * 培训讲师
     */
    private String instructor;

    /**
     * 学员人数
     */
    private Integer participantCount;

    /**
     * 培训说明
     */
    private String description;

    /**
     * 创建人ID
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人ID
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标志：0-正常 1-已删除
     */
    private Integer deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}