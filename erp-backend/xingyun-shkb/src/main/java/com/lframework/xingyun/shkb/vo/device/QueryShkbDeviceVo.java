package com.lframework.xingyun.shkb.vo.device;

import com.lframework.starter.web.core.vo.PageVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class QueryShkbDeviceVo extends PageVo {

    /**
     * 设备编号
     */
    @ApiModelProperty("设备编号")
    private String code;

    /**
     * 设备名称
     */
    @ApiModelProperty("设备名称")
    private String name;

    /**
     * 管理区域
     */
    @ApiModelProperty("管理区域")
    private String managementArea;

    /**
     * 维保项目
     */
    @ApiModelProperty("维保项目")
    private String maintenanceProject;

    /**
     * 维保间隔
     */
    @ApiModelProperty("维保间隔")
    private Integer maintenanceInterval;

    /**
     * 上次维保时间起始
     */
    @ApiModelProperty("上次维保时间起始")
    private LocalDate lastMaintenanceTimeStart;

    /**
     * 上次维保时间截止
     */
    @ApiModelProperty("上次维保时间截止")
    private LocalDate lastMaintenanceTimeEnd;

    /**
     * 下次维保时间起始
     */
    @ApiModelProperty("下次维保时间起始")
    private LocalDate nextMaintenanceTimeStart;

    /**
     * 下次维保时间截止
     */
    @ApiModelProperty("下次维保时间截止")
    private LocalDate nextMaintenanceTimeEnd;

    /**
     * 维保工卡
     */
    @ApiModelProperty("维保工卡")
    private String maintenanceCard;

    /**
     * 设备状态
     */
    @ApiModelProperty("设备状态")
    private Boolean available;

    /**
     * 创建时间起始
     */
    @ApiModelProperty("创建时间起始")
    private LocalDate createTimeStart;

    /**
     * 创建时间截止
     */
    @ApiModelProperty("创建时间截止")
    private LocalDate createTimeEnd;
}
