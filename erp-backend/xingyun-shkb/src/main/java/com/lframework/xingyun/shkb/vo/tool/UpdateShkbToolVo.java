package com.lframework.xingyun.shkb.vo.tool;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UpdateShkbToolVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ApiModelProperty(value = "ID", required = true)
    @NotBlank(message = "ID不能为空！")
    private String id;

    /**
     * 管理编号
     */
    @ApiModelProperty(value = "管理编号", required = true)
    @NotBlank(message = "管理编号不能为空！")
    private String code;

    /**
     * 工具名称
     */
    @ApiModelProperty(value = "工具名称", required = true)
    @NotBlank(message = "工具名称不能为空！")
    private String name;

    /**
     * 管理区域
     */
    @ApiModelProperty("管理区域")
    private String managementArea;

    /**
     * 证书编号
     */
    @ApiModelProperty("证书编号")
    private String certificateNumber;

    /**
     * 型号
     */
    @ApiModelProperty("型号")
    private String model;

    /**
     * 规格
     */
    @ApiModelProperty("规格")
    private String specification;

    /**
     * 计量标准
     */
    @ApiModelProperty("计量标准")
    private String standard;

    /**
     * 精度
     */
    @ApiModelProperty("精度")
    private String precision;

    /**
     * 存放位置
     */
    @ApiModelProperty("存放位置")
    private String storageLocation;

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
     * 计量周期
     */
    @ApiModelProperty("计量周期")
    private Integer calibrationPeriod;

    /**
     * 上次维保单位
     */
    @ApiModelProperty("上次维保单位")
    private String lastMaintenanceUnit;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态", required = true)
    @NotNull(message = "状态不能为空！")
    private Boolean available;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;
}
