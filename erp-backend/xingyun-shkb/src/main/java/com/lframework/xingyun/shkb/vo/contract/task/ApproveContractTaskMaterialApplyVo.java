package com.lframework.xingyun.shkb.vo.contract.task;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
public class ApproveContractTaskMaterialApplyVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申请ID列表
     */
    @ApiModelProperty(value = "申请ID列表", required = true)
    @NotEmpty(message = "申请ID列表不能为空！")
    private List<String> ids;

    /**
     * 审批结果
     * true - 通过
     * false - 拒绝
     */
    @ApiModelProperty(value = "审批结果", required = true)
    @NotNull(message = "审批结果不能为空！")
    private Boolean approved;

    /**
     * 审批意见
     */
    @ApiModelProperty("审批意见")
    private String comment;
}
