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
 * 设备管理
 * @TableName shkb_device
 */
@TableName(value ="shkb_device")
@Data
public class ShkbDevice extends BaseEntity implements BaseDto {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 设备编号
     */
    private String code;

    /**
     * 名称
     */
    private String name;

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
    @TableField(fill = FieldFill.INSERT)
    private String updateBy;

    /**
     * 修改人ID
     */
    @TableField(fill = FieldFill.INSERT)
    private String updateById;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 管理区域
     */
    private String managementArea;

    /**
     * 状态
     */
    private Boolean available;

    /**
     * 维保项目
     */
    private String maintenanceProject;

    /**
     * 维保间隔 单位：月
     */
    private Integer maintenanceInterval;

    /**
     * 维保工卡
     */
    private String maintenanceCard;

    /**
     * 上次维保时间
     */
    private LocalDate lastMaintenanceTime;

    /**
     * 下次维保时间
     */
    private LocalDate nextMaintenanceTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}