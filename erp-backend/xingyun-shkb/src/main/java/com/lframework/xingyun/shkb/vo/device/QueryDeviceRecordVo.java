package com.lframework.xingyun.shkb.vo.device;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.web.core.vo.PageVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class QueryDeviceRecordVo extends PageVo {

    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    @ApiModelProperty("设备ID")
    private String deviceId;

    /**
     * 维保人
     */
    @ApiModelProperty("维保人")
    private String maintenancenUser;

    /**
     * 维保时间起始
     */
    @ApiModelProperty("维保时间起始")
    @JsonFormat(pattern = StringPool.DATE_PATTERN, shape = JsonFormat.Shape.STRING)
    private LocalDate maintenanceTimeStart;

    /**
     * 维保时间截止
     */
    @ApiModelProperty("维保时间截止")
    @JsonFormat(pattern = StringPool.DATE_PATTERN, shape = JsonFormat.Shape.STRING)
    private LocalDate maintenanceTimeEnd;

    /**
     * 创建时间起始
     */
    @ApiModelProperty("创建时间起始")
    @JsonFormat(pattern = StringPool.DATE_PATTERN, shape = JsonFormat.Shape.STRING)
    private LocalDateTime createTimeStart;

    /**
     * 创建时间截止
     */
    @ApiModelProperty("创建时间截止")
    @JsonFormat(pattern = StringPool.DATE_PATTERN, shape = JsonFormat.Shape.STRING)
    private LocalDateTime createTimeEnd;
}
