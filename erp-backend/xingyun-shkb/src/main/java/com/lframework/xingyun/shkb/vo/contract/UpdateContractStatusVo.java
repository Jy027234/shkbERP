package com.lframework.xingyun.shkb.vo.contract;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 修改合同状态请求VO
 *
 * @author kison
 */
@Data
public class UpdateContractStatusVo {

    /**
     * 合同ID
     */
    @ApiModelProperty(value = "合同ID", required = true)
    @NotBlank(message = "合同ID不能为空！")
    private String contractId;

    /**
     * 合同状态
     */
    @ApiModelProperty(value = "合同状态", required = true)
    @NotBlank(message = "合同状态不能为空！")
    private String contractStatus;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
}
