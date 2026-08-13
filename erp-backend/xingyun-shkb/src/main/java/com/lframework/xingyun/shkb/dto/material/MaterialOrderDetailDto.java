package com.lframework.xingyun.shkb.dto.material;

import com.lframework.starter.web.core.dto.BaseDto;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 发料单明细Dto
 *
 * @author kison
 */
@Data
public class MaterialOrderDetailDto implements BaseDto {

    /**
     * ID
     */
    private String id;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 商品ID
     */
    private String productId;

    /**
     * 商品编号
     */
    private String productCode;

    /**
     * 机型
     */
    private String machineTypeName;



    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品类目名称
     */
    private String categoryName;

    /**
     * 商品品牌名称
     */
    private String brandName;

    /**
     * SKU
     */
    private String skuCode;

    /**
     * 外部编号
     */
    private String externalCode;

    /**
     * 单位
     */
    private String unit;

    /**
     * 规格
     */
    private String spec;

    /**
     * 含税单价
     */
    private BigDecimal taxPrice;

    /**
     * 含税金额
     */
    private BigDecimal taxAmount;

    /**
     * 备注
     */
    private String description;

    /**
     * 已出库数量
     */
    private Integer outNum;

    /**
     * 发料数量
     */
    private Integer orderNum;

    /**
     * 库存数量
     */
    private Integer stockNum;

    /**
     * 是否启用批次管理
     */
    private Boolean isBatch;

    /**
     * 是否唯一序列号管理
     */
    private Boolean isSerial;
    

    /**
     * 组合商品原始明细ID
     */
    private String oriBundleDetailId;
}
