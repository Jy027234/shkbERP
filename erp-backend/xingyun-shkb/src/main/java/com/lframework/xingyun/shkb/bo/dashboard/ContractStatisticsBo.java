package com.lframework.xingyun.shkb.bo.dashboard;

import com.lframework.starter.web.core.bo.BaseBo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 合同统计BO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ContractStatisticsBo extends BaseBo {

    /**
     * 任务ID
     */
    @ApiModelProperty("任务ID")
    private String taskId;

    /**
     * 合同ID
     */
    @ApiModelProperty("合同ID")
    private String contractId;

    /**
     * 合同编号
     */
    @ApiModelProperty("合同编号")
    private String contractCode;

    /**
     * 合同类型
     */
    @ApiModelProperty("合同类型")
    private Integer contractType;

    /**
     * 合同类型名称
     */
    @ApiModelProperty("合同类型名称")
    private String contractTypeName;

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
     * 件号编号
     */
    @ApiModelProperty("件号编号")
    private String partNumberCode;

    /**
     * 件号名称
     */
    @ApiModelProperty("件号名称")
    private String partNumberName;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 计划完工时间
     */
    @ApiModelProperty("计划完工时间")
    private LocalDateTime plannedCompletionTime;
}
