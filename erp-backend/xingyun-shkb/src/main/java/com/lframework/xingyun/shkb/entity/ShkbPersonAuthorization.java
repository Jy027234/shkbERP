package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 人员授权主表
 * @TableName shkb_person_authorization
 */
@TableName(value ="shkb_person_authorization")
@Data
public class ShkbPersonAuthorization implements Serializable {
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
     * 员工ID
     */
    private String employeeId;

    /**
     * 授权日期
     */
    private Date authorizationDate;

    /**
     * 到期日期
     */
    private Date expiryDate;

    /**
     * 状态：0-已过期 1-有效 2-即将过期 3-无效
     */
    private Integer status;

    /**
     * 凭据附件URL
     */
    private String credentialFileUrl;

    /**
     * 凭据附件名称
     */
    private String credentialFileName;

    /**
     * 备注
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