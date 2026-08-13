package com.lframework.xingyun.sc.bo.stock.serial;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.entity.StoreCenter;
import com.lframework.xingyun.basedata.service.product.ProductService;
import com.lframework.xingyun.basedata.service.storecenter.StoreCenterService;
import com.lframework.xingyun.sc.entity.ProductStockSerial;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class QueryProductStockSerialBo extends BaseBo<ProductStockSerial> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 仓库ID
     */
    @ApiModelProperty("仓库ID")
    private String scId;

    /**
     * 仓库编号
     */
    @ApiModelProperty("仓库编号")
    private String scCode;

    /**
     * 仓库名称
     */
    @ApiModelProperty("仓库名称")
    private String scName;

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
     * 序列号
     */
    @ApiModelProperty("序列号")
    private String serialNumber;

    /**
     * 批次ID
     */
    @ApiModelProperty("批次ID")
    private String batchId;

    /**
     * 批次号
     */
    @ApiModelProperty("批次号")
    private String batchNumber;

    /**
     * 架位（货架/库位信息）
     */
    @ApiModelProperty("架位")
    private String shelfLocation;

    /**
     * 生产日期
     */
    @ApiModelProperty("生产日期")
    @JsonFormat(pattern = StringPool.DATE_PATTERN)
    private LocalDate productionDate;

    /**
     * 失效日期
     */
    @ApiModelProperty("失效日期")
    @JsonFormat(pattern = StringPool.DATE_PATTERN)
    private LocalDate expiryDate;

    /**
     * 在库状态
     */
    @ApiModelProperty("在库状态")
    private Boolean stockStatus;
    
    /**
     * 商品类目ID
     */
    @ApiModelProperty("商品类目ID")
    private String categoryId;

    /**
     * 商品类目名称
     */
    @ApiModelProperty("商品类目名称")
    private String categoryName;

    /**
     * 商品品牌ID
     */
    @ApiModelProperty("商品品牌ID")
    private String brandId;

    /**
     * 商品品牌名称
     */
    @ApiModelProperty("商品品牌名称")
    private String brandName;

    /**
     * 供应商ID
     */
    @ApiModelProperty("供应商ID")
    private String supplierId;

    /**
     * 供应商名称
     */
    @ApiModelProperty("供应商名称")
    private String supplierName;

    public QueryProductStockSerialBo() {
    }

    public QueryProductStockSerialBo(ProductStockSerial dto) {
        super(dto);
    }

    @Override
    protected void afterInit(ProductStockSerial dto) {
        /*this.id = dto.getId();
        this.productId = dto.getProductId();
        this.serialNumber = dto.getSerialNumber();
        this.batchId = dto.getBatchId();
        this.productionDate = dto.getProductionDate();
        this.expiryDate = dto.getExpiryDate();
        this.stockStatus = dto.getStockStatus();

        if (!StringUtil.isBlank(dto.getProductId())) {
            ProductService productService = ApplicationUtil.getBean(ProductService.class);
            Product product = productService.findById(dto.getProductId());
            this.productCode = product.getCode();
            this.productName = product.getName();
        }
        
        // 获取仓库信息 - 通过批次ID关联
        // 注意：这里假设序列号通过批次ID关联到仓库
        if (!StringUtil.isBlank(dto.getBatchId())) {
            // 这里需要通过批次ID查找对应的批次和仓库信息
            // 实际实现可能需要通过ProductStockBatchService获取批次信息
            // 为了简化示例，这里假设直接从数据库获取关联的仓库信息
            StoreCenterService storeCenterService = ApplicationUtil.getBean(StoreCenterService.class);
            // 假设能从关联的批次中获取仓库ID
            // 实际实现中可能需要通过批次服务获取仓库ID
            String scId = ""; // 需要实际实现
            if (!StringUtil.isBlank(scId)) {
                StoreCenter sc = storeCenterService.findById(scId);
                this.scId = sc.getId();
                this.scCode = sc.getCode();
                this.scName = sc.getName();
            }
        }*/
    }
}
