package com.lframework.xingyun.shkb.bo.contract.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.xingyun.shkb.entity.ContractTaskMaterialApply;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class QueryContractTaskMaterialApplyBo extends BaseBo<ContractTaskMaterialApply> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 任务ID
     */
    @ApiModelProperty("任务ID")
    private String taskId;

    /**
     * 申请编号
     */
    @ApiModelProperty("申请编号")
    private String applyCode;

    /**
     * 合同编号
     */
    @ApiModelProperty("合同编号")
    private String contractCode;

    /**
     * 机型
     */
    @ApiModelProperty("机型")
    private String machineTypeName;

    /**
     * 件号
     */
    @ApiModelProperty("件号")
    private String partNumberName;

    /**
     * 产品序号
     */
    @ApiModelProperty("产品序号")
    private String serialNumber;

    /**
     * 申请时间
     */
    @ApiModelProperty("申请时间")
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private LocalDateTime createTime;

    /**
     * 审批状态
     */
    @ApiModelProperty("审批状态")
    private Integer approvalStatus;

    /**
     * 审批状态文本
     */
    @ApiModelProperty("审批状态文本")
    private String approvalStatusText;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 审批时间
     */
    @ApiModelProperty("审批时间")
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private Date approvalTime;
    
    /**
     * 必换件单号
     */
    @ApiModelProperty("必换件单号")
    private String replacementPartCode;
    
    /**
     * 非必换件单号
     */
    @ApiModelProperty("非必换件单号")
    private String nonReplacementPartCode;

    /**
     * 是否已创建发料单
     */
    @ApiModelProperty("是否已创建发料单")
    private Boolean hasMaterialOrder;

    public QueryContractTaskMaterialApplyBo() {

    }

    public QueryContractTaskMaterialApplyBo(ContractTaskMaterialApply dto) {

        super(dto);
    }

    @Override
    protected void afterInit(ContractTaskMaterialApply dto) {

        // 设置审批状态文本
        if (this.approvalStatus != null) {
            this.approvalStatusText = this.approvalStatus == 0 ? "未审批" : (this.approvalStatus == 1 ? "已审批" : "已拒绝");
        }
    }
}
