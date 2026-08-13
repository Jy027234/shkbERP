package com.lframework.xingyun.shkb.vo.contract.task;

import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 更新任务非必换件数量VO
 */
@Data
public class UpdateContractTaskNonPartProductVo implements BaseVo, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 非必换件记录ID
     */
    @ApiModelProperty(value = "非必换件记录ID", required = true)
    @NotBlank(message = "非必换件记录ID不能为空！")
    private String id;

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
}
