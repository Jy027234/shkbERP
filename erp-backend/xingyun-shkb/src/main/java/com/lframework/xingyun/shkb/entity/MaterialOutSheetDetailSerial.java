package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;

/**
 * 发料出库单序列号明细
 * @TableName tbl_material_out_sheet_detail_serial
 */
@TableName(value ="tbl_material_out_sheet_detail_serial")
@Data
public class MaterialOutSheetDetailSerial extends BaseEntity implements BaseDto {
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
     * 序列号库存id
     */
    private String stockSerialId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}