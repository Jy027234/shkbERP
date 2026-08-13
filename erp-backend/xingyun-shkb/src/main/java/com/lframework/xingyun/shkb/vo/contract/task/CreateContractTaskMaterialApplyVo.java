package com.lframework.xingyun.shkb.vo.contract.task;

import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 创建合同任务领料申请VO
 *
 * @author kison
 */
@Data
public class CreateContractTaskMaterialApplyVo implements BaseVo {

    /**
     * 任务ID
     */
    @ApiModelProperty(value = "任务ID", required = true)
    @NotBlank(message = "任务ID不能为空！")
    private String taskId;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;
}
