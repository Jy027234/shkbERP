package com.lframework.xingyun.basedata.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lframework.starter.web.core.entity.BaseEntity;
import com.lframework.starter.web.core.dto.BaseDto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 件号
 * @TableName base_data_part_number
 */
@TableName(value ="base_data_part_number")
@Data
public class PartNumber extends BaseEntity implements BaseDto {
    public static final String CACHE_NAME = "partNumber";
    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 编号
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 状态
     */
    private Boolean available;

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
     * 机型id
     */
    private String machineTypeId;

}