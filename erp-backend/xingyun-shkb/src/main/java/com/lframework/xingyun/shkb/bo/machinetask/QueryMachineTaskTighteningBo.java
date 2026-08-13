package com.lframework.xingyun.shkb.bo.machinetask;

import com.lframework.xingyun.shkb.entity.MachineTaskTightening;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QueryMachineTaskTighteningBo {

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("任务ID")
    private String taskId;

    @ApiModelProperty("任务状态 0-待装配 1-已完成")
    private Integer machineTaskStatus;

    @ApiModelProperty("任务类型 0-平台任务 1-线下任务")
    private Integer taskType;

    @ApiModelProperty("合同号")
    private String contractNo;
    @ApiModelProperty("序列号")
    private String serialNo;

    @ApiModelProperty("件号")
    private String partNo;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("上报时间")
    private LocalDateTime reportTime;

    public QueryMachineTaskTighteningBo(MachineTaskTightening d) {
        this.id = d.getId();
        this.taskId = d.getTaskId();
        this.machineTaskStatus = d.getMachineTaskStatus();
        this.taskType = d.getTaskType();
        this.contractNo = d.getContractNo();
        this.serialNo = d.getSerialNo();
        this.partNo = d.getPartNo();
        this.createTime = d.getCreateTime();
        this.reportTime = d.getReportTime();
    }
}
