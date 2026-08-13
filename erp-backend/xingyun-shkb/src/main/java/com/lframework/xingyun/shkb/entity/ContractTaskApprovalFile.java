package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;

/**
 * 合同任务放行文件
 * @TableName shkb_contract_task_approval_file
 */
@TableName(value ="shkb_contract_task_approval_file")
@Data
public class ContractTaskApprovalFile extends BaseEntity implements BaseDto {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 
     */
    private String url;

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