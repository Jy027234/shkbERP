package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 人员授权项目关联表
 * @TableName shkb_person_authorization_project
 */
@TableName(value ="shkb_person_authorization_project")
@Data
public class ShkbPersonAuthorizationProject implements Serializable {
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
     * 授权主表ID
     */
    private String authorizationId;

    /**
     * 授权项目ID
     */
    private String projectId;

    /**
     * 状态：0-已过期 1-有效 2-即将过期 3-无效
     */
    private Integer status;

    /**
     * 必修课程是否完成：0-未完成 1-已完成
     */
    private Integer requiredCoursesCompleted;

    /**
     * 创建时间
     */
    private Date createTime;

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