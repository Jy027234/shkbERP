package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 员工证书表
 * @TableName shkb_employee_certificate
 */
@TableName(value ="shkb_employee_certificate")
@Data
public class ShkbEmployeeCertificate implements Serializable {
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
     * 证书类型
     */
    private String certificateType;

    /**
     * 证书名称
     */
    private String certificateName;

    /**
     * 证书编号
     */
    private String certificateNo;

    /**
     * 发证机构
     */
    private String issueOrg;

    /**
     * 发证日期
     */
    private Date issueDate;

    /**
     * 有效期开始
     */
    private Date validStartDate;

    /**
     * 有效期结束
     */
    private Date validEndDate;

    /**
     * 状态：0-过期 1-有效
     */
    private Integer status;

    /**
     * 证书扫描件URL
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