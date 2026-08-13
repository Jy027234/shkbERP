package com.lframework.xingyun.shkb.vo.device;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.common.constants.StringPool;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreateDeviceRecordVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    @ApiModelProperty(value = "设备ID", required = true)
    @NotBlank(message = "设备ID不能为空！")
    private String deviceId;

    /**
     * 维保人
     */
    @ApiModelProperty(value = "维保人", required = true)
    @NotBlank(message = "维保人不能为空！")
    private String maintenancenUser;

    /**
     * 维保时间
     */
    @ApiModelProperty(value = "维保时间", required = true)
    private LocalDate maintenanceTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;
}
