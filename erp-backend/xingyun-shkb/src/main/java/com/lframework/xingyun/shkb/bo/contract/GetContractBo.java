package com.lframework.xingyun.shkb.bo.contract;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.xingyun.shkb.entity.Contract;
import com.lframework.xingyun.shkb.enums.ContractType;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 合同详情的BO
 *
 * @author kison
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetContractBo extends BaseBo<Contract> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 合同编号
     */
    @ApiModelProperty("合同编号")
    private String code;

    /**
     * 合同名称
     */
    @ApiModelProperty("合同名称")
    private String name;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private Boolean available;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;

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
     * 客户ID
     */
    @ApiModelProperty("客户ID")
    private String customerId;

    /**
     * 客户名称
     */
    @ApiModelProperty("客户名称")
    private String customerName;

    /**
     * 客户简码
     */
    @ApiModelProperty("客户简码")
    private String mnemonicCode;

    /**
     * 件号ID
     */
    @ApiModelProperty("件号ID")
    private String partNumberId;

    /**
     * 件号编码
     */
    @ApiModelProperty("件号编码")
    private String partNumberCode;

    /**
     * 件号名称
     */
    @ApiModelProperty("件号名称")
    private String partNumberName;

    /**
     * 机型ID
     */
    @ApiModelProperty("机型ID")
    private String machineTypeId;

    /**
     * 机型编码
     */
    @ApiModelProperty("机型编码")
    private String machineTypeCode;

    /**
     * 机型名称
     */
    @ApiModelProperty("机型名称")
    private String machineTypeName;

    /**
     * 维修类型列表
     */
    @ApiModelProperty("维修类型列表")
    private List<RepairTypeVo> repairTypes;
    
    @Data
    public static class RepairTypeVo implements Serializable {
        private static final long serialVersionUID = 1L;
        
        /**
         * 维修类型ID
         */
        @ApiModelProperty("维修类型ID")
        private String id;
        
        /**
         * 维修类型编码
         */
        @ApiModelProperty("维修类型编码")
        private String code;
        
        /**
         * 维修类型名称
         */
        @ApiModelProperty("维修类型名称")
        private String name;
    }

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
    @ApiModelProperty("合同时间")
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private LocalDateTime contractTime;

    /**
     * 入库时间
     */
    @ApiModelProperty("入库时间")
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private LocalDateTime storageTime;

    /**
     * 计划完工时间
     */
    @ApiModelProperty("计划完工时间")
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private LocalDateTime plannedCompletionTime;

    /**
     * 发货时间
     */
    @ApiModelProperty("发货时间")
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private LocalDateTime deliveryTime;

    /**
     * 实际完工时间
     */
    @ApiModelProperty("实际完工时间")
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
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

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createBy;

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
     * 合同进度
     */
    @ApiModelProperty("合同进度")
    private Integer contractStatus;

    /**
     * 合同状态名称
     */
    @ApiModelProperty("合同进度名称")
    private String contractStatusName;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private LocalDateTime createTime;

    /**
     * 修改人
     */
    @ApiModelProperty("修改人")
    private String updateBy;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private LocalDateTime updateTime;

    public GetContractBo() {
    }

    public GetContractBo(Contract dto) {
        super(dto);
    }

    @Override
    protected void afterInit(Contract dto) {
        if (dto.getContractType() != null) {
            // 根据数字编码获取对应的枚举对象及其描述
            ContractType contractTypeEnum = null;
            for (ContractType type : ContractType.values()) {
                if (type.getCode().equals(dto.getContractType())) {
                    contractTypeEnum = type;
                    break;
                }
            }
            
            if (contractTypeEnum != null) {
                this.contractTypeName = contractTypeEnum.getDesc();
            }
        }
        
        // 维修类型列表现在直接从SQL查询中获取，不再需要从ContractRepairService获取
        // 如果SQL查询没有返回维修类型列表，则为空列表
        if (this.repairTypes == null) {
            this.repairTypes = new ArrayList<>();
        }
    }
}
