package com.lframework.xingyun.shkb.vo.contract.task;

import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 创建合同任务维修状态记录Vo
 */
@Data
public class CreateContractTaskRepairStatusRecordVo implements BaseVo {

    /**
     * 任务ID
     */
    @ApiModelProperty(value = "任务ID", required = true)
    @NotBlank(message = "任务ID不能为空！")
    private String taskId;

    /**
     * 维修状态
     */
    @ApiModelProperty(value = "维修状态", required = true)
    @NotBlank(message = "维修状态不能为空！")
    private String repairStatus;

    /**
     * 备注说明
     */
    @ApiModelProperty(value = "备注说明")
    private String description;
}
