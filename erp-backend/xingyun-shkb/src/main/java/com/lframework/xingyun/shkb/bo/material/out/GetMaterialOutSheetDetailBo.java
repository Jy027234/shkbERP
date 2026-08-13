package com.lframework.xingyun.shkb.bo.material.out;

import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.xingyun.shkb.dto.material.out.MaterialOutSheetDetailDto;
import com.lframework.xingyun.shkb.dto.material.out.MaterialOutSheetDetailLotDto;
import com.lframework.xingyun.shkb.dto.material.out.MaterialOutSheetDetailSerialDto;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Data;

/**
 * 发料出库单明细Bo
 */
@Data
public class GetMaterialOutSheetDetailBo extends BaseBo<BaseDto> {

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
     * 出库数量
     */
    @ApiModelProperty("出库数量")
    private Integer outNum;

    /**
     * 发料数量
     */
    @ApiModelProperty("发料数量")
    private Integer orderNum;

    /**
     * 已发料数量
     */
    @ApiModelProperty("已发料数量")
    private Integer orderOutNum;

    /**
     * 库存数量
     */
    @ApiModelProperty("库存数量")
    private Integer stockNum;

    /**
     * 是否批次管理
     */
    @ApiModelProperty("是否批次管理")
    private Boolean isBatch;

    /**
     * 是否序列号管理
     */
    @ApiModelProperty("是否序列号管理")
    private Boolean isSerial;

    /**
     * 含税价格
     */
    @ApiModelProperty("含税价格")
    private BigDecimal taxPrice;

    /**
     * 排序号
     */
    @ApiModelProperty("排序号")
    private Integer orderNo;

    /**
     * 含税金额
     */
    @ApiModelProperty("含税金额")
    private BigDecimal taxAmount;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;

    /**
     * 批次明细
     */
    @ApiModelProperty("批次明细")
    private List<GetMaterialOutSheetDetailLotBo> lots;

    /**
     * 序列号明细
     */
    @ApiModelProperty("序列号明细")
    private List<GetMaterialOutSheetDetailSerialBo> serials;

    /**
     * 发料单明细ID
     */
    @ApiModelProperty("发料单明细ID")
    private String materialOrderDetailId;

    public GetMaterialOutSheetDetailBo() {
    }

    public GetMaterialOutSheetDetailBo(MaterialOutSheetDetailDto dto) {
        if (dto == null) {
            return;
        }
        // flat fields
        this.id = dto.getId();
        this.productId = dto.getProductId();
        this.productCode = dto.getProductCode();
        this.productName = dto.getProductName();
        this.categoryName = dto.getCategoryName();
        this.brandName = dto.getBrandName();
        this.skuCode = dto.getSkuCode();
        this.externalCode = dto.getExternalCode();
        this.unit = dto.getUnit();
        this.spec = dto.getSpec();
        this.isBatch = dto.getIsBatch();
        this.isSerial = dto.getIsSerial();
        this.outNum = dto.getOutNum();
        this.orderNum = dto.getOrderNum();
        this.orderOutNum = dto.getOrderOutNum();
        this.stockNum = dto.getStockNum();
        this.taxPrice = dto.getTaxPrice();
        this.taxAmount = dto.getTaxAmount();
        this.description = dto.getDescription();
        this.orderNo = dto.getOrderNo();
        this.materialOrderDetailId = dto.getMaterialOrderDetailId();

        // lots mapping
        if (dto.getLots() != null) {
            this.lots = dto.getLots().stream()
                    .filter(Objects::nonNull)
                    .map(l -> {
                        GetMaterialOutSheetDetailLotBo bo = new GetMaterialOutSheetDetailLotBo();
                        bo.setId(l.getId());
                        bo.setLotId(l.getLotId());
                        bo.setLotCode(l.getLotCode());
                        bo.setOutNum(l.getOutNum());
                        return bo;
                    })
                    .collect(Collectors.toList());
        }

        // serials mapping (ensure serialNumber carried over)
        if (dto.getSerials() != null) {
            this.serials = dto.getSerials().stream()
                    .filter(Objects::nonNull)
                    .map(GetMaterialOutSheetDetailSerialBo::new)
                    .collect(Collectors.toList());
        }
    }
}
