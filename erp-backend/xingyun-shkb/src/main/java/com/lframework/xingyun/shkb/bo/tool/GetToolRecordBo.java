package com.lframework.xingyun.shkb.bo.tool;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.xingyun.shkb.entity.ShkbToolRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GetToolRecordBo extends BaseBo<ShkbToolRecord> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 工具ID
     */
    @ApiModelProperty("工具ID")
    private String toolId;

    /**
     * 工具名称
     */
    @ApiModelProperty("工具名称")
    private String toolName;

    /**
     * 维保人
     */
    @ApiModelProperty("维保人")
    private String maintenancenUser;

    /**
     * 计量时间
     */
    @ApiModelProperty("计量时间")
    @JsonFormat(pattern = StringPool.DATE_PATTERN)
    private LocalDate maintenanceTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;

    /**
     * 计量证书编号
     */
    @ApiModelProperty("计量证书编号")
    private String certificateNumber;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private LocalDateTime createTime;

    /**
     * 修改人
     */
    @ApiModelProperty("修改人")
    private String updateBy;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private LocalDateTime updateTime;

    public GetToolRecordBo() {
    }

    public GetToolRecordBo(ShkbToolRecord dto) {
        super(dto);
    }
}
