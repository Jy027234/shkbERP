package com.lframework.xingyun.shkb.vo.contract.task;

import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 修改合同任务参数。
 */
@Data
public class UpdateContractTaskVo implements BaseVo, Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "任务ID", required = true)
    @NotBlank(message = "任务ID不能为空！")
    private String id;

    @ApiModelProperty(value = "机型ID", required = true)
    @NotBlank(message = "机型ID不能为空！")
    private String machineTypeId;

    @ApiModelProperty(value = "件号ID", required = true)
    @NotBlank(message = "件号ID不能为空！")
    private String partNumberId;

    @ApiModelProperty(value = "产品序号", required = true)
    @NotBlank(message = "产品序号不能为空！")
    private String serialNumber;

    @ApiModelProperty(value = "维修类型ID列表", required = true)
    @NotEmpty(message = "维修类型不能为空！")
    private List<String> repairTypeIds;

    @ApiModelProperty("其他维修需求")
    private String otherRepairRequirements;

    @ApiModelProperty(value = "入库时间", required = true)
    @NotNull(message = "入库时间不能为空！")
    private LocalDateTime storageTime;

    @ApiModelProperty(value = "计划完工时间", required = true)
    @NotNull(message = "计划完工时间不能为空！")
    private LocalDateTime plannedCompletionTime;

    @ApiModelProperty(value = "任务状态", required = true)
    @NotBlank(message = "任务状态不能为空！")
    private String taskStatus;

    @ApiModelProperty(value = "任务类型", required = true)
    @NotBlank(message = "任务类型不能为空！")
    private String taskType;

    @ApiModelProperty("备注")
    private String description;
}
