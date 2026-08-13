package com.lframework.xingyun.shkb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 客户接收单附件
 * @TableName shkb_product_storage_file
 */
@TableName(value ="shkb_product_storage_file")
@Data
public class ProductStorageFile implements Serializable {
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
     * 成品库id
     */
    private String productStorageId;

    /**
     * 上传时间
     */
    private Date createTime;

    /**
     * 文件名称
     */
    private String fileName;

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