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
 * 员工培训记录表
 * @TableName shkb_employee_training
 */
@TableName(value ="shkb_employee_training")
@Data
public class ShkbEmployeeTraining implements Serializable {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 员工ID
     */
    private String employeeId;

    /**
     * 培训名称
     */
    private String trainingName;

    /**
     * 培训类型
     */
    private String trainingType;

    /**
     * 培训机构
     */
    private String trainingOrg;

    /**
     * 培训内容
     */
    private String trainingContent;

    /**
     * 培训开始日期
     */
    private Date startDate;

    /**
     * 培训结束日期
     */
    private Date endDate;

    /**
     * 培训学时
     */
    private BigDecimal trainingHours;

    /**
     * 培训结果
     */
    private String trainingResult;

    /**
     * 培训证书编号
     */
    private String certificateNo;

    /**
     * 培训证书扫描件URL
     */
    private String fileUrl;

    /**
     * 备注
     */
    private String description;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建人ID
     */
    private String createById;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改人ID
     */
    private String updateById;

    /**
     * 修改时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}