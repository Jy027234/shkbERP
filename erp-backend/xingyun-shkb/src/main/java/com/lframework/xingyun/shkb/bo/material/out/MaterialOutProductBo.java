package com.lframework.xingyun.shkb.bo.material.out;

import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.xingyun.sc.dto.purchase.PurchaseProductDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发料出库可选航材。
 */
@Data
public class MaterialOutProductBo extends BaseBo<PurchaseProductDto> {

    @ApiModelProperty("航材ID")
    private String productId;

    @ApiModelProperty("航材编号")
    private String productCode;

    @ApiModelProperty("航材名称")
    private String productName;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("品牌名称")
    private String brandName;

    @ApiModelProperty("SKU")
    private String skuCode;

    @ApiModelProperty("简码")
    private String externalCode;

    @ApiModelProperty("规格")
    private String spec;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("库存数量")
    private Integer stockNum;

    @ApiModelProperty("是否启用批次管理")
    private Boolean isBatch;

    @ApiModelProperty("是否启用序列号管理")
    private Boolean isSerial;

    public MaterialOutProductBo(PurchaseProductDto dto, Integer stockNum) {
        this.init(dto);
        this.stockNum = stockNum == null ? 0 : stockNum;
    }

    @Override
    protected void afterInit(PurchaseProductDto dto) {
        this.productId = dto.getId();
        this.productCode = dto.getCode();
        this.productName = dto.getName();
        this.isBatch = dto.getIsBatch();
        this.isSerial = dto.getIsSerial();
    }
}
