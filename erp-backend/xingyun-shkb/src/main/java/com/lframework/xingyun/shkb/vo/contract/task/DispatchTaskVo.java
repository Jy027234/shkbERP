package com.lframework.xingyun.shkb.vo.contract.task;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 任务派发参数
 */
@Data
public class DispatchTaskVo {

    /**
     * 任务ID
     */
    @ApiModelProperty(value = "任务ID", required = true)
    @NotBlank(message = "任务ID不能为空！")
    private String id;

    /**
     * 派发给用户ID
     */
    @ApiModelProperty(value = "派发给用户ID", required = true)
    @NotBlank(message = "派发给用户ID不能为空！")
    private String taskUserId;
}
