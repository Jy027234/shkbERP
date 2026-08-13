package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;

/**
 * 工具计量记录附件
 * @TableName shkb_tool_record_file
 */
@TableName(value ="shkb_tool_record_file")
@Data
public class ToolRecordFile extends BaseEntity implements BaseDto {
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
     * 计量记录id
     */
    private String recordId;

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