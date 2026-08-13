package com.lframework.xingyun.shkb.vo.contract.task;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 审批领料申请VO
 *
 * @author kison
 */
@Data
public class IssueMaterialVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申请ID列表
     */
    @ApiModelProperty(value = "任务ID", required = true)
    @NotBlank(message = "任务ID不能为空！")
    private String taskId;
    
    /**
     * 仓库ID
     */
    @ApiModelProperty(value = "仓库ID", required = true)
    @NotBlank(message = "仓库ID不能为空！")
    private String scId;

    /**
     * 审批意见
     */
    @ApiModelProperty("发料备注")
    private String remark;
}
