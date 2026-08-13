package com.lframework.xingyun.shkb.vo.contract.task;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class UpdateContractTaskApprovalFileNumberVo {

    /**
     * 合同任务ID
     */
    @ApiModelProperty("合同任务ID")
    @NotBlank(message = "合同任务ID不能为空！")
    private String id;

    /**
     * 放行文件编号
     */
    @ApiModelProperty("放行文件编号")
    @NotBlank(message = "放行文件编号不能为空！")
    private String approvalFileNumber;
}
