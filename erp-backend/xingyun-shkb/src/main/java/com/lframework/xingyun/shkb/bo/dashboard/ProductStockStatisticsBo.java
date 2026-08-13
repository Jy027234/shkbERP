package com.lframework.xingyun.shkb.bo.dashboard;

import com.lframework.starter.web.core.bo.BaseBo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 产品库存统计BO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductStockStatisticsBo extends BaseBo {

    /**
     * 产品ID
     */
    @ApiModelProperty("产品ID")
    private String productId;

    /**
     * 产品编号
     */
    @ApiModelProperty("产品编号")
    private String productCode;

    /**
     * 产品名称
     */
    @ApiModelProperty("产品名称")
    private String productName;

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
     * 库存数量
     */
    @ApiModelProperty("库存数量")
    private Integer stockNum;

    /**
     * 可用数量
     */
    @ApiModelProperty("可用数量")
    private Integer availableNum;
}
