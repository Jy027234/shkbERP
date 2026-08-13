package com.lframework.xingyun.shkb.bo.dashboard;

import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.xingyun.shkb.entity.ShkbDevice;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备统计BO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceStatisticsBo extends BaseBo<ShkbDevice> {

    /**
     * 设备ID
     */
    @ApiModelProperty("设备ID")
    private String deviceId;

    /**
     * 设备编号
     */
    @ApiModelProperty("设备编号")
    private String deviceCode;

    /**
     * 设备名称
     */
    @ApiModelProperty("设备名称")
    private String deviceName;

    /**
     * 设备规格
     */
    @ApiModelProperty("设备规格")
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
