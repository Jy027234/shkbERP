package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;

/**
 * 设备附件管理
 * @TableName shkb_device_file
 */
@TableName(value ="shkb_device_file")
@Data
public class ShkbDeviceFile extends BaseEntity implements BaseDto {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 
     */
    private String url;

    /**
     * 
     */
    private String fileName;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 
     */
    private Date createTime;

    /**
     * ContentType
     */
    private String contentType;

    /**
     * 文件大小
     */
    private String fileSize;

    /**
     * 文件后缀
     */
    private String fileSuffix;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}