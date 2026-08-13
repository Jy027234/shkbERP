package com.lframework.xingyun.shkb.bo.dashboard;

import com.lframework.starter.web.core.bo.BaseBo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 工具统计BO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ToolStatisticsBo extends BaseBo {

    /**
     * 工具ID
     */
    @ApiModelProperty("工具ID")
    private String toolId;

    /**
     * 工具编号
     */
    @ApiModelProperty("工具编号")
    private String toolCode;

    /**
     * 工具名称
     */
    @ApiModelProperty("工具名称")
    private String toolName;

    /**
     * 规格
     */
    @ApiModelProperty("规格")
    private String specification;

    /**
     * 校准周期（月）
     */
    @ApiModelProperty("校准周期（月）")
    private Integer calibrationPeriod;

    /**
     * 上次校准日期
     */
    @ApiModelProperty("上次校准日期")
    private LocalDateTime lastCalibrationDate;

    /**
     * 下次校准日期
     */
    @ApiModelProperty("下次校准日期")
    private LocalDateTime nextCalibrationDate;

    /**
     * 是否即将到期（30天内）
     */
    @ApiModelProperty("是否即将到期（30天内）")
    private Boolean nearlyExpired;

    /**
     * 是否已过期
     */
    @ApiModelProperty("是否已过期")
    private Boolean expired;
}
