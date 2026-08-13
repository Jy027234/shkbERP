package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;

/**
 * 发料出库单明细
 * @TableName tbl_material_out_sheet_detail
 */
@TableName(value ="tbl_material_out_sheet_detail")
@Data
public class MaterialOutSheetDetail extends BaseEntity implements BaseDto {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 出库单ID
     */
    private String sheetId;

    /**
     * 商品ID
     */
    private String productId;

    /**
     * 需发料出库数量
     */
    private Integer orderNum;

    /**
     * 原价
     */
    private BigDecimal oriPrice;

    /**
     * 现价
     */
    private BigDecimal taxPrice;

    /**
     * 税率（%）
     */
    private BigDecimal taxRate;

    /**
     * 备注
     */
    private String description;

    /**
     * 排序编号
     */
    private Integer orderNo;

    /**
     * 已出库数量
     */
    private Integer outNum;

    /**
     * 批次库存id
     */
    private String stockBatchId;

    /**
     * 唯一序列号表
     */
    private String serialNumbers;

    /**
     * 总价
     */
    private BigDecimal taxAmount;

    /**
     * 发料单明细id
     */
    private String materialOrderDetailId;




    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}