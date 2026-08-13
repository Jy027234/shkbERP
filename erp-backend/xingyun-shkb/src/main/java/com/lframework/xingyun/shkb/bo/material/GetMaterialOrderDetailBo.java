package com.lframework.xingyun.shkb.bo.material;

import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.xingyun.shkb.dto.material.MaterialOrderDetailDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 发料单明细Bo
 *
 * @author kison
 */
@Data
public class GetMaterialOrderDetailBo extends BaseBo<MaterialOrderDetailDto> {

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
     * 商品编号
     */
    @ApiModelProperty("商品编号")
    private String productCode;

    /**
     * 机器类型名称
     */
    @ApiModelProperty("机器类型名称")
    private String machineTypeName;

    /**
     * 商品名称
     */
    @ApiModelProperty("商品名称")
    private String productName;

    /**
     * 商品类目名称
     */
    @ApiModelProperty("商品类目名称")
    private String categoryName;

    /**
     * 商品品牌名称
     */
    @ApiModelProperty("商品品牌名称")
    private String brandName;

    /**
     * SKU
     */
    @ApiModelProperty("SKU")
    private String skuCode;

    /**
     * 外部编号
     */
    @ApiModelProperty("外部编号")
    private String externalCode;

    /**
     * 单位
     */
    @ApiModelProperty("单位")
    private String unit;

    /**
     * 规格
     */
    @ApiModelProperty("规格")
    private String spec;

    /**
     * 含税单价
     */
    @ApiModelProperty("含税单价")
    private BigDecimal taxPrice;

    /**
     * 含税金额
     */
    @ApiModelProperty("含税金额")
    private BigDecimal taxAmount;

    /**
     * 发料数量
     */
    @ApiModelProperty("发料数量")
    private Integer orderNum;

    /**
     * 已出库数量
     */
    @ApiModelProperty("已出库数量")
    private Integer outNum;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;

    /**
     * 是否启用批次管理
     */
    @ApiModelProperty("是否启用批次管理")
    private Boolean isBatch;

    /**
     * 是否启用唯一序列号管理
     */
    @ApiModelProperty("是否启用唯一序列号管理")
    private Boolean isSerial;

    /**
     * 库存数量
     */
    @ApiModelProperty("库存数量")
    private Integer stockNum;

    public GetMaterialOrderDetailBo() {
    }

    public GetMaterialOrderDetailBo(MaterialOrderDetailDto dto) {
        super(dto);
    }
}
