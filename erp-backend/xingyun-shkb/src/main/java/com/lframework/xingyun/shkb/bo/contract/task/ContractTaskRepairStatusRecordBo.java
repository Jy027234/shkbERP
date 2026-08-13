package com.lframework.xingyun.shkb.bo.contract.task;

import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.xingyun.shkb.entity.ContractTaskRepairStatusRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 合同任务维修状态记录Bo
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ContractTaskRepairStatusRecordBo extends BaseBo<ContractTaskRepairStatusRecord> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createBy;

    /**
     * 创建人ID
     */
    @ApiModelProperty("创建人ID")
    private String createById;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 任务ID
     */
    @ApiModelProperty("任务ID")
    private String taskId;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;

    /**
     * 维修状态
     */
    @ApiModelProperty("维修状态")
    private String repairStatus;

    /**
     * 维修状态名称
     */
    @ApiModelProperty("维修状态名称")
    private String repairStatusName;

    public ContractTaskRepairStatusRecordBo() {
    }

    public ContractTaskRepairStatusRecordBo(ContractTaskRepairStatusRecord dto) {
        super(dto);
    }
}
