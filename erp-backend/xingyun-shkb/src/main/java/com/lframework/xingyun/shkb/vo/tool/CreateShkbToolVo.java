package com.lframework.xingyun.shkb.vo.tool;

import com.lframework.starter.web.core.components.validation.IsCode;
import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreateShkbToolVo implements BaseVo, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 管理区域
     */
    @ApiModelProperty(value = "管理区域", required = true)
    @NotBlank(message = "请输入管理区域！")
    private String managementArea;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称", required = true)
    @NotBlank(message = "请输入设备名称！")
    private String name;

    /**
     * 管理编号
     */
    @ApiModelProperty(value = "管理编号", required = true)
    @IsCode
    @NotBlank(message = "请输入管理编号！")
    private String code;

    /**
     * 证书编号
     */
    @ApiModelProperty(value = "证书编号", required = true)
    @NotBlank(message = "请输入证书编号！")
    private String certificateNumber;

    /**
     * 型号/规格
     */
    @ApiModelProperty(value = "型号/规格", required = true)
    @NotBlank(message = "请输入型号/规格！")
    private String specification;

    /**
     * 型号
     */
    @ApiModelProperty(value = "型号", required = true)
    @NotBlank(message = "请输入型号！")
    private String model;

    /**
     * 计量标准
     */
    @ApiModelProperty(value = "计量标准", required = true)
    @NotBlank(message = "请输入计量标准！")
    private String standard;

    /**
     * 精度
     */
    @ApiModelProperty(value = "精度")
    private String precision;

    /**
     * 存放位置
     */
    @ApiModelProperty(value = "存放位置", required = true)
    @NotBlank(message = "请输入存放位置！")
    private String storageLocation;

    /**
     * 上次计量日期
     */
    @ApiModelProperty(value = "上次计量日期", required = true)
    private LocalDate lastMaintenanceTime;

    /**
     * 下次维保时间
     */
    @ApiModelProperty(value = "下次计量日期", required = true)
    private LocalDate nextMaintenanceTime;

    /**
     * 计量周期
     */
    @ApiModelProperty(value = "计量周期", required = true)
    @NotNull(message = "请输入计量周期！")
    private Integer calibrationPeriod;

    /**
     * 上次维保单位
     */
    @ApiModelProperty(value = "上次维保单位", required = true)
    @NotBlank(message = "请输入上次维保单位！")
    private String lastMaintenanceUnit;

    /**
     * 维保人
     */
    @ApiModelProperty(value = "维保人", required = true)
    @NotBlank(message = "请输入维保人！")
    private String maintenancenUser;

    /**
     * 计量证书编号
     */
    @ApiModelProperty(value = "计量证书编号", required = true)
    @NotBlank(message = "请输入计量证书编号！")
    private String recordCertificateNumber;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态", required = true)
    @NotNull(message = "请选择状态！")
    private Boolean available;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;
}
