package com.lframework.xingyun.shkb.vo.contract.task;

import com.lframework.starter.web.core.vo.PageVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QueryContractTaskVo extends PageVo {

    /**
     * 任务类型
     */
    @ApiModelProperty("任务类型")
    private String taskType;

    /**
     * 合同编号
     */
    @ApiModelProperty("合同编号")
    private String contractCode;

    /**
     * 客户标识
     */
    @ApiModelProperty("客户Id")
    private String customerId;

    /**
     * 机型ID
     */
    @ApiModelProperty("机型ID")
    private String machineTypeId;

    /**
     * 件号ID
     */
    @ApiModelProperty("件号ID")
    private String partNumberId;

    /**
     * 件号
     */
    @ApiModelProperty("件号")
    private String partNumberCode;

    /**
     * 入库时间起始
     */
    @ApiModelProperty("入库时间起始")
    private LocalDateTime storageTimeStart;

    /**
     * 入库时间截止
     */
    @ApiModelProperty("入库时间截止")
    private LocalDateTime storageTimeEnd;

    /**
     * 计划完工时间起始
     */
    @ApiModelProperty("计划完工时间起始")
    private LocalDateTime plannedCompletionTimeStart;

    /**
     * 计划完工时间截止
     */
    @ApiModelProperty("计划完工时间截止")
    private LocalDateTime plannedCompletionTimeEnd;

    /**
     * 任务状态
     */
    @ApiModelProperty("任务状态")
    private String taskStatus;

    /**
     * 维修状态
     */
    @ApiModelProperty("维修状态")
    private String repairStatus;

    /**
     * 放行文件编号
     */
    @ApiModelProperty("放行文件编号")
    private String approvalFileNumber;
}
