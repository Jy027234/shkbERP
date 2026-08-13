package com.lframework.xingyun.shkb.dto.contract.task;

import com.lframework.starter.web.core.dto.BaseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 合同任务必换件Dto
 *
 * @author kison
 */
@Data
public class ContractTaskProductDto implements BaseDto {

    /**ContractTaskProductDto
     * ID
     */
    private String id;

    /**
     * 工卡ID
     */
    private String workCardId;

    /**
     * 工卡号
     */
    private String workCardCode;

    /**
     * 工卡名称
     */
    private String workCardName;

    /**
     * 维修类型ID
     */
    private String repairTypeId;

    /**
     * 维修类型名称
     */
    private String repairTypeName;

    /**
     * 件号ID
     */
    private String partNumberId;

    /**
     * 件号名称
     */
    private String partNumber;

    /**
     * 件号
     */
    private String partNumberCode;

    /**
     * 工卡件号机型名称
     */
    private String machineTypeName;

    /**
     * 换件清单机型
     */
    private String productMachineTypeName;

    /**
     * 商品ID
     */
    private String productId;

    /**
     * 商品编号
     */
    private String productCode;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品规格
     */
    private String productSpec;

    /**
     * 商品单位
     */
    private String productUnit;
    
    /**
     * 数量
     */
    private Integer quantity;
}
