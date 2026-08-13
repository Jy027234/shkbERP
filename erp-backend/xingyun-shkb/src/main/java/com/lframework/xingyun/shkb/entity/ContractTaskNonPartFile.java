package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;

/**
 * 非必换件文件列表
 * @TableName shkb_contract_task_non_part_file
 */
@TableName(value ="shkb_contract_task_non_part_file")
@Data
public class ContractTaskNonPartFile extends BaseEntity implements BaseDto {
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
     * 非必换件id
     */
    private String nonPartId;

    /**
     * 文件访问路径
     */
    private String url;

    /**
     * 文件后缀
     */
    private String fileSuffix;

    /**
     * 文件大小
     */
    private String fileSize;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * ContentType
     */

    private String contentType;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
