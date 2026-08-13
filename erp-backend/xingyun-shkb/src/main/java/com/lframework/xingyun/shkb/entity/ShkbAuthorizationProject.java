package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 授权项目表
 * @TableName shkb_authorization_project
 */
@TableName(value ="shkb_authorization_project")
@Data
public class ShkbAuthorizationProject implements Serializable {
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
     * 项目名称
     */
    private String projectName;

    /**
     * 项目类型：岗位授权/特种作业授权
     */
    private String projectType;

    /**
     * 有效期数值
     */
    private Integer validityPeriod;

    /**
     * 有效期单位：month-月 year-年
     */
    private String validityUnit;

    /**
     * 项目描述
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