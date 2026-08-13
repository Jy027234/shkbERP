package com.lframework.xingyun.shkb.vo.contract;

import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新合同的参数
 *
 * @author kison
 */
@Data
public class UpdateContractVo implements BaseVo, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ApiModelProperty(value = "ID", required = true)
    @NotBlank(message = "ID不能为空！")
    private String id;

    /**
     * 合同编号
     */
    @ApiModelProperty(value = "合同编号", required = true)
    @NotBlank(message = "合同编号不能为空！")
    private String code;

    /**
     * 合同名称
     */
    @ApiModelProperty(value = "合同名称", required = true)
    @NotBlank(message = "合同名称不能为空！")
    private String name;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态", required = true)
    @NotNull(message = "状态不能为空！")
    private Boolean available;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;

    /**
     * 合同类型
     */
    @ApiModelProperty(value = "合同类型", required = true)
    @NotNull(message = "合同类型不能为空！")
    private Integer contractType;

    /**
     * 客户标识
     */
    @ApiModelProperty(value = "客户标识", required = true)
    @NotBlank(message = "客户标识不能为空！")
    private String customerId;

    /**
     * 件号ID
     */
    @ApiModelProperty(value = "件号ID", required = true)
    @NotBlank(message = "件号ID不能为空！")
    private String partNumberId;

    /**
     * 维修类型ID列表
     */
    @ApiModelProperty(value = "维修类型ID列表", required = true)
    @NotNull(message = "维修类型ID列表不能为空！")
    private List<String> repairTypeIds;

    /**
     * 产品序号
     */
    @ApiModelProperty("产品序号")
    private String serialNumber;

    /**
     * 其他维修需求
     */
    @ApiModelProperty("其他维修需求")
    private String otherRepairRequirements;

    /**
     * 合同时间
     */
    @ApiModelProperty(value = "合同时间", required = true)
    @NotNull(message = "合同时间不能为空！")
    private LocalDateTime contractTime;

    /**
     * 入库时间
     */
    @ApiModelProperty("入库时间")
    private LocalDateTime storageTime;

    /**
     * 计划完工时间
     */
    @ApiModelProperty("计划完工时间")
    private LocalDateTime plannedCompletionTime;

    /**
     * 发货时间
     */
    @ApiModelProperty("发货时间")
    private LocalDateTime deliveryTime;

    /**
     * 实际完工时间
     */
    @ApiModelProperty("实际完工时间")
    private LocalDateTime actualCompletionTime;

    /**
     * 合同报价
     */
    @ApiModelProperty("合同报价")
    private BigDecimal contractPrice;

    /**
     * 更换件价格
     */
    @ApiModelProperty("更换件价格")
    private BigDecimal replacementPartPrice;
}
