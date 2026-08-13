package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;

/**
 * 工具计量证书
 * @TableName shkb_tool_file
 */
@TableName(value ="shkb_tool_file")
@Data
public class ShkbToolFile extends BaseEntity implements BaseDto {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 访问路径
     */
    private String url;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 工具id
     */
    private String toolId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

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