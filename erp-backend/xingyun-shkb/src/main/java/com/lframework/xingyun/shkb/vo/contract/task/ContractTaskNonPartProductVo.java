package com.lframework.xingyun.shkb.vo.contract.task;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContractTaskNonPartProductVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @ApiModelProperty(value = "任务ID", required = true)
    @NotBlank(message = "任务ID不能为空！")
    private String taskId;

    /**
     * 商品ID
     */
    @ApiModelProperty(value = "商品ID", required = true)
    @NotBlank(message = "商品ID不能为空！")
    private String productId;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量", required = true)
    @NotNull(message = "数量不能为空！")
    @Min(value = 1, message = "数量必须大于0！")
    private Integer quantity;

    /**
     * 原因说明
     */
    @ApiModelProperty(value = "原因说明")
    private String reason;

    /**
     * 附件列表
     */
    @ApiModelProperty("附件列表")
    private List<ContractTaskNonPartFileVo> files;
}
