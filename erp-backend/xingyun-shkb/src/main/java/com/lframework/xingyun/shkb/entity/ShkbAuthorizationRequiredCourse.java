package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 授权项目必修课程关联表
 * @TableName shkb_authorization_required_course
 */
@TableName(value ="shkb_authorization_required_course")
@Data
public class ShkbAuthorizationRequiredCourse implements Serializable {
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
     * 授权项目ID
     */
    private String projectId;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 是否必修：0-选修 1-必修
     */
    private Integer isRequired;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 删除标志：0-正常 1-已删除
     */
    private Integer deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}