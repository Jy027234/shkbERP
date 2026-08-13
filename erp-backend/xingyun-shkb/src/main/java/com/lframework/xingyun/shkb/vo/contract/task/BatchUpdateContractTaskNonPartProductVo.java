package com.lframework.xingyun.shkb.vo.contract.task;

import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 批量更新任务非必换件数量VO
 */
@Data
public class BatchUpdateContractTaskNonPartProductVo implements BaseVo, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @ApiModelProperty(value = "任务ID", required = true)
    @NotBlank(message = "任务ID不能为空！")
    private String taskId;

    /**
     * 非必换件记录列表
     */

    @ApiModelProperty(value = "非必换件记录列表", required = true)
    @NotEmpty(message = "非必换件记录列表不能为空！")
    @Valid
    private List<UpdateContractTaskNonPartProductVo> records;
}
