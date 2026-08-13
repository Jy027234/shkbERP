package com.lframework.xingyun.shkb.vo.contract.task;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 线下鉴定参数
 */
@Data
public class OfflineAppraisalVo {

    /**
     * 任务ID
     */
    @ApiModelProperty(value = "任务ID", required = true)
    @NotBlank(message = "任务ID不能为空！")
    private String id;

    /**
     * 是否通过
     */
    @ApiModelProperty(value = "是否通过", required = true)
    @NotNull(message = "是否通过不能为空！")
    private Boolean approved;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;
}
