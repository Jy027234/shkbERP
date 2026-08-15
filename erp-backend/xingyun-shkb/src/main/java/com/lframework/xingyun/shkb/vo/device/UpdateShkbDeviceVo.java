package com.lframework.xingyun.shkb.vo.device;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UpdateShkbDeviceVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ApiModelProperty(value = "ID", required = true)
    @NotBlank(message = "ID不能为空！")
    private String id;

    /**
     * 设备编号
     */
    @ApiModelProperty(value = "设备编号", required = true)
    @NotBlank(message = "设备编号不能为空！")
    private String code;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称", required = true)
    @NotBlank(message = "设备名称不能为空！")
    private String name;

    /**
     * 管理区域
     */
    @ApiModelProperty(value = "管理区域", required = true)
    @NotBlank(message = "管理区域不能为空！")
    private String managementArea;

    /**
     * 维保项目
     */
    @ApiModelProperty("维保项目")
    private String maintenanceProject;

    /**
     * 维保间隔（天）
     */
    @ApiModelProperty("维保间隔（天）")
    private Integer maintenanceInterval;

    /**
     * 维保工卡
     */
    @ApiModelProperty("维保工卡")
    private String maintenanceCard;

    /**
     * 上次维保时间
     */
    @ApiModelProperty("上次维保时间")
    private LocalDate lastMaintenanceTime;

    /**
     * 下次维保时间
     */
    @ApiModelProperty("下次维保时间")
    private LocalDate nextMaintenanceTime;

    /**
     * 设备状态
     * false-停用 true-启用
     */
    @ApiModelProperty(value = "设备状态", required = true)
    @NotNull(message = "请选择设备状态！")
    private Boolean available;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;
}
