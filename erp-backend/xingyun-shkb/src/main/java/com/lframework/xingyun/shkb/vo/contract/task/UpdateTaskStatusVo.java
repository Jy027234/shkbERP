package com.lframework.xingyun.shkb.vo.contract.task;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 修改合同任务状态请求VO
 *
 * @author kison
 */
@Data
public class UpdateTaskStatusVo {

    /**
     * 任务ID
     */
    @ApiModelProperty(value = "任务ID", required = true)
    @NotBlank(message = "任务ID不能为空！")
    private String taskId;

    /**
     * 任务状态
     */
    @ApiModelProperty(value = "任务状态", required = true)
    @NotBlank(message = "任务状态不能为空！")
    private String taskStatus;

    /**
     * 退修原因
     */
    @ApiModelProperty(value = "退修原因")
    private String reason;
}
