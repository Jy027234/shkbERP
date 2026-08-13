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
 * @TableName shkb_tool
 */
@TableName(value ="shkb_tool")
@Data
public class ShkbTool extends BaseEntity implements BaseDto {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 管理编号
     */
    private String code;

    /**
     * 工具名称
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
     * 管理区域
     */
    private String managementArea;

    /**
     * 状态
     */
    private Boolean available;

    /**
     * 上次计量时间
     */
    private LocalDate lastMaintenanceTime;

    /**
     * 下次计量时间
     */
    private LocalDate nextMaintenanceTime;

    /**
     * 维保到期日期
     */
    private LocalDate expirationTime;

    /**
     * 证书编号
     */
    private String certificateNumber;

    /**
     * 型号
     */
    private String model;

    /**
     * 规格
     */
    private String specification;

    /**
     * 计量标准
     */
    private String standard;

    /**
     * 精度
     */
    @TableField(value = "`precision`")
    private String precision;

    /**
     * 存放位置
     */
    private String storageLocation;

    /**
     * 计量周期
     */
    private Integer calibrationPeriod;

    /**
     * 上次维保单位
     */
    private String lastMaintenanceUnit;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}