package com.lframework.xingyun.sc.vo.stock.batch;

import com.lframework.starter.web.core.vo.PageVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 查询批次库存的参数
 */
@Data
public class QueryProductStockBatchVo extends PageVo {

    private static final long serialVersionUID = 1L;

    /**
     * 仓库ID
     */
    @ApiModelProperty(value = "仓库ID")
    private String scId;

    /**
     * 商品编号
     */
    @ApiModelProperty(value = "商品编号")
    private String productCode;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String productName;

    /**
     * 商品类目ID
     */
    @ApiModelProperty(value = "商品类目ID")
    private String categoryId;

    /**
     * 商品品牌ID
     */
    @ApiModelProperty(value = "商品品牌ID")
    private String brandId;

    /**
     * 批次号
     */
    @ApiModelProperty(value = "批次号")
    private String batchNumber;
}
