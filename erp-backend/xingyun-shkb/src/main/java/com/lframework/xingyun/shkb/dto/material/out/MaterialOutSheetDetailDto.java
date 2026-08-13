package com.lframework.xingyun.shkb.dto.material.out;

import com.lframework.starter.web.core.dto.BaseDto;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * 发料出库单明细DTO
 */
@Data
public class MaterialOutSheetDetailDto implements BaseDto {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 出库单ID
     */
    @ApiModelProperty("出库单ID")
    private String sheetId;

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
     * stockNum
     */
    @ApiModelProperty("库存数量")
    private Integer stockNum;


    /**
     * 含税价格
     */
    @ApiModelProperty("含税价格")
    private BigDecimal taxPrice;

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
     * 排序号
     */
    @ApiModelProperty("排序号")
    private Integer orderNo;

    /**
     * 批次明细
     */
    @ApiModelProperty("批次明细")
    private List<MaterialOutSheetDetailLotDto> lots;

    /**
     * 序列号明细
     */
    @ApiModelProperty("序列号明细")
    private List<MaterialOutSheetDetailSerialDto> serials;

    /**
     * 发料单明细ID
     */
    @ApiModelProperty("发料单明细ID")
    private String materialOrderDetailId;
}
