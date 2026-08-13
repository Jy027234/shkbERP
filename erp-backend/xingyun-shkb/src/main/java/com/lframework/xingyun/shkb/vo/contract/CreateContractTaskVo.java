package com.lframework.xingyun.shkb.vo.contract;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 生成合同任务参数
 */
@Data
public class CreateContractTaskVo {

    /**
     * 合同ID
     */
    @ApiModelProperty(value = "合同ID", required = true)
    @NotBlank(message = "合同ID不能为空！")
    private String contractId;
}
