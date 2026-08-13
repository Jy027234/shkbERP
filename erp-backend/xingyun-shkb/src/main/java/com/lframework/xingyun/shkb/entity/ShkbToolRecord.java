package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;

/**
 * 计量记录
 * @TableName shkb_tool_record
 */
@TableName(value ="shkb_tool_record")
@Data
public class ShkbToolRecord extends BaseEntity implements BaseDto {
    /**
     * ID
     */
    private String id;

    /**
     * 设备id
     */
    private String toolId;

    /**
     * 维保人
     */
    private String maintenancenUser;

    /**
     * 计量证书编号
     */
    private String certificateNumber;

    /**
     * 备注
     */
    private String description;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建人ID
     */
    @TableField(fill = FieldFill.INSERT)
    private String createById;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 修改人ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateById;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 计量时间
     */
    private LocalDate maintenanceTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}