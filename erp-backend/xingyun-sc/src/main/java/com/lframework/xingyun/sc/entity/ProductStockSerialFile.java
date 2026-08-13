package com.lframework.xingyun.sc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.entity.BaseEntity;
import lombok.Data;

/**
 * 商品序列证书
 * @TableName tbl_product_stock_serial_file
 */
@TableName(value ="tbl_product_stock_serial_file")
@Data
public class ProductStockSerialFile extends BaseEntity implements BaseDto {
    /**
     * ID
     */
    @TableId
    private String id;

    /**
     * 
     */
    private String url;

    /**
     * 
     */
    private String fileName;

    /**
     * 库存批次id
     */
    private String stockSerialId;

    /**
     * 
     */
    private Date createTime;

    /**
     * ContentType
     */
    private String contentType;

    /**
     * 文件大小
     */
    private String fileSize;

    /**
     * 文件后缀
     */
    private String fileSuffix;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}