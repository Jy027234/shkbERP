package com.lframework.xingyun.shkb.vo.contract.task;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 合同任务工卡Vo
 *
 * @author kison
 */
@Data
public class ContractTaskWorkCardVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 合同任务ID
     */
    @ApiModelProperty(value = "合同任务ID", required = true)
    @NotBlank(message = "合同任务ID不能为空！")
    private String taskId;

    /**
     * 工卡ID列表
     */
    @ApiModelProperty(value = "工卡ID列表", required = true)
    @NotEmpty(message = "工卡ID列表不能为空！")
    private List<String> workCardIds;
}
