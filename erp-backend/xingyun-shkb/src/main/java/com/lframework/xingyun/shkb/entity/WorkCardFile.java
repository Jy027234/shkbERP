package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.util.Date;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;

/**
 * 工卡附件表
 * @TableName shkb_work_card_file
 */
@TableName(value ="shkb_work_card_file")
@Data
public class WorkCardFile extends BaseEntity implements BaseDto {
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
     * 工卡id
     */
    private String workCardId;

    /**
     * 上传时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * ContentType
     */

    private String ContentType;

    /**
     * 文件后缀
     */
    private String fileSuffix;

    /**
     * 文件大小
     */
    private String fileSize;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}