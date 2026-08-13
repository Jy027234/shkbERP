package com.lframework.xingyun.shkb.bo.contract.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.web.core.bo.BaseBo;

import com.lframework.xingyun.shkb.entity.ContractTask;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetContractTaskBo extends BaseBo<ContractTask> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

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
     * 合同名称
     */
    @ApiModelProperty("合同名称")
    private String contractName;

    /**
     * 客户标识
     */
    @ApiModelProperty("客户标识")
    private String customerId;

    /**
     * 客户简码
     */
    @ApiModelProperty("客户简码")
    private String mnemonicCode;

    /**
     * 客户名称
     */
    @ApiModelProperty("客户名称")
    private String customerName;

    /**
     * 机型编号
     */
    @ApiModelProperty("机型编号")
    private String machineTypeCode;

    /**
     * 机型名称
     */
    @ApiModelProperty("机型名称")
    private String machineTypeName;

    /**
     * 机型ID
     */
    @ApiModelProperty("机型ID")
    private String machineTypeId;

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
     * 件号ID
     */
    @ApiModelProperty("件号ID")
    private String partNumberId;

    /**
     * 产品序号
     */
    @ApiModelProperty("产品序号")
    private String serialNumber;

    /**
     * 其他工卡
     */
    @ApiModelProperty("其他工卡")
    private String otherWorkCardNumber;


    /**
     * 维修类型列表
     */
    @ApiModelProperty("维修类型列表")
    private List<RepairTypeBo> repairTypes;

    /**
     * 任务类型
     */
    @ApiModelProperty("任务类型")
    private String taskType;

    /**
     * 任务类型名称
     */
    @ApiModelProperty("任务类型名称")
    private String taskTypeName;

    /**
     * 任务状态
     */
    @ApiModelProperty("任务状态")
    private String taskStatus;


    /**
     * 任务状态名称
     */
    @ApiModelProperty("任务状态名称")
    private String taskStatusName;

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

    /**
     * 航材状态
     */
    @ApiModelProperty("航材状态")
    private String materialStatus;

    /**
     * 航材状态名称
     */
    @ApiModelProperty("航材状态名称")
    private String materialStatusName;

    /**
     * 入库时间
     */
    @ApiModelProperty("入库时间")
    @JsonFormat(pattern = StringPool.DATE_PATTERN)
    private LocalDateTime storageTime;

    /**
     * 计划完工时间
     */
    @ApiModelProperty("计划完工时间")
    @JsonFormat(pattern = StringPool.DATE_PATTERN)
    private LocalDateTime plannedCompletionTime;

    /**
     * 派发至
     */
    @ApiModelProperty("派发至")
    private String dispatch;

    /**
     * 派发给具体人员
     */
    @ApiModelProperty("派发给具体人员")
    private String taskUserId;

    /**
     * 派发给具体人员名称
     */
    @ApiModelProperty("派发给具体人员名称")
    private String taskUserName;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;

    /**
     * 退修原因
     */
    @ApiModelProperty("退修原因")
    private String returnRepairReason;

    /**
     * 维修备注
     */
    @ApiModelProperty("维修备注")
    private String repairDescription;

    /**
     * 其他维修需求
     */
    @ApiModelProperty("其他维修需求")
    private String otherRepairRequirements;
    
    /**
     * 工卡列表
     */
    @ApiModelProperty("工卡列表")
    private List<ContractTaskWorkCardBo> workCards;

    /**
     * 放行文件编号
     */
    @ApiModelProperty("放行文件编号")
    private String approvalFileNumber;

    public GetContractTaskBo() {
    }

    public GetContractTaskBo(ContractTask dto) {
        super(dto);
    }

    @Data
    public static class RepairTypeBo {

        /**
         * ID
         */
        @ApiModelProperty("ID")
        private String id;

        /**
         * 编号
         */
        @ApiModelProperty("编号")
        private String code;

        /**
         * 名称
         */
        @ApiModelProperty("名称")
        private String name;
    }
}
