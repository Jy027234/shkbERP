package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 培训课程表
 * @TableName shkb_training_course
 */
@TableName(value ="shkb_training_course")
@Data
public class ShkbTrainingCourse implements Serializable {
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
     * 课程名称
     */
    private String courseName;

    /**
     * 课程类型：安全培训/技能培训/管理培训
     */
    private String courseType;

    /**
     * 培训学时
     */
    private BigDecimal trainingHours;

    /**
     * 实施间隔数值
     */
    private Integer implementationInterval;

    /**
     * 间隔单位：month-月 year-年
     */
    private String intervalUnit;

    /**
     * 课程描述
     */
    private String description;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

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