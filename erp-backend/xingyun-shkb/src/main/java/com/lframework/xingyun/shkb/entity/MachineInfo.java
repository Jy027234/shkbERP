package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 自动化设备表
 * @TableName shkb_machine_info
 */
@TableName(value ="shkb_machine_info")
@Data
public class MachineInfo extends BaseEntity implements BaseDto {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 设备id
     */
    private String machineId;

    /**
     * 1-拧紧机- 2磁粉机
     */
    private Integer machineType;

    /**
     * 设备名称
     */
    private String machineName;

    /**
     * 最近访问时间
     */
    private LocalDateTime visitTime;

    /**
     * ip地址
     */
    private String ipAddress;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}