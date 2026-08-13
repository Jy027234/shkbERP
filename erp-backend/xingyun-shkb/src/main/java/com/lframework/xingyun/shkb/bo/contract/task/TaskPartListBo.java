package com.lframework.xingyun.shkb.bo.contract.task;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 任务换件清单BO
 */
@Data
public class TaskPartListBo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 商品ID
     */
    @ApiModelProperty("商品ID")
    private String productId;

    /**
     * 商品编码
     */
    @ApiModelProperty("商品编码")
    private String productCode;

    /**
     * 商品名称
     */
    @ApiModelProperty("商品名称")
    private String productName;

    /**
     * 商品规格
     */
    @ApiModelProperty("商品规格")
    private String productSpec;

    /**
     * 商品单位
     */
    @ApiModelProperty("商品单位")
    private String productUnit;

    /**
     * 件号
     */
    @ApiModelProperty("件号")
    private String partNumber;

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
     * 数量
     */
    @ApiModelProperty("数量")
    private Integer quantity;

    /**
     * 库存数量
     */
    @ApiModelProperty("库存数量")
    private Integer stockNum;

    /**
     * 是否必换件
     */
    @ApiModelProperty("是否必换件")
    private Boolean isMandatory;

    /**
     * 非必换件原因
     */
    @ApiModelProperty("非必换件原因")
    private String reason;
}
