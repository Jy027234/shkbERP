package com.lframework.xingyun.shkb.vo.machinetask;

import com.lframework.starter.web.core.vo.PageVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class QueryMachineTaskTighteningVo extends PageVo {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("创建时间起始")
    private LocalDateTime createTimeStart;

    @ApiModelProperty("创建时间截止")
    private LocalDateTime createTimeEnd;

    @ApiModelProperty("合同号")
    private String contractNo;

    @ApiModelProperty("件号")
    private String partNo;

    @ApiModelProperty("序列号")
    private String serialNumber;

    @ApiModelProperty("任务状态 0-待装配 1-已完成")
    private Integer machineTaskStatus;

    @ApiModelProperty("任务类型 0-平台任务 1-线下任务")
    private Integer taskType;
}
