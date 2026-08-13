package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 培训学员表
 * @TableName shkb_training_participant
 */
@TableName(value ="shkb_training_participant")
@Data
public class ShkbTrainingParticipant implements Serializable {
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
     * 实施计划ID
     */
    private String implementationId;

    /**
     * 员工ID
     */
    private String employeeId;

    /**
     * 培训结果：合格/优秀/不合格/待定
     */
    private String trainingResult;

    /**
     * 证书编号
     */
    private String certificateNo;

    /**
     * 状态：0-未开始 1-进行中 2-已完成
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