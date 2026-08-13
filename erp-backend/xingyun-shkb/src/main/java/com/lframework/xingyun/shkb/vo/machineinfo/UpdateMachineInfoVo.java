package com.lframework.xingyun.shkb.vo.machineinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class UpdateMachineInfoVo {

    @ApiModelProperty("ID")
    @NotBlank(message = "ID不能为空！")
    private String id;

    @ApiModelProperty("设备名称")
    @NotBlank(message = "设备名称不能为空！")
    private String machineName;

    @ApiModelProperty("IP地址")
    private String ipAddress;
}
