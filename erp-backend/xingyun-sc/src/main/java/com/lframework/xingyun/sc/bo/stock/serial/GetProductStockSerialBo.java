package com.lframework.xingyun.sc.bo.stock.serial;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.entity.ProductBrand;
import com.lframework.xingyun.basedata.entity.ProductCategory;
import com.lframework.xingyun.basedata.entity.StoreCenter;
import com.lframework.xingyun.basedata.service.product.ProductBrandService;
import com.lframework.xingyun.basedata.service.product.ProductCategoryService;
import com.lframework.xingyun.basedata.service.product.ProductService;
import com.lframework.xingyun.basedata.service.storecenter.StoreCenterService;
import com.lframework.xingyun.basedata.entity.Supplier;
import com.lframework.xingyun.basedata.service.supplier.SupplierService;
import com.lframework.xingyun.sc.entity.ProductStockBatch;
import com.lframework.xingyun.sc.entity.ProductStockSerial;
import com.lframework.xingyun.sc.service.stock.ProductStockBatchService;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetProductStockSerialBo extends BaseBo<ProductStockSerial> {

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
    private Integer stockStatus;

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

    public GetProductStockSerialBo() {
    }

    public GetProductStockSerialBo(ProductStockSerial dto) {
        super(dto);
    }

    @Override
    protected void afterInit(ProductStockSerial dto) {
        this.id = dto.getId();
        this.productId = dto.getProductId();
        this.serialNumber = dto.getSerialNumber();
        this.batchId = dto.getBatchId();
        this.productionDate = dto.getProductionDate();
        this.expiryDate = dto.getExpiryDate();
        this.stockStatus = dto.getStockStatus();
        this.shelfLocation = dto.getShelfLocation();
        this.supplierId = dto.getSupplierId();

        ProductService productService = ApplicationUtil.getBean(ProductService.class);
        Product product = productService.findById(dto.getProductId());
        this.productCode = product.getCode();
        this.productName = product.getName();

        if (!StringUtil.isBlank(product.getCategoryId())) {
            ProductCategoryService productCategoryService = ApplicationUtil.getBean(
                    ProductCategoryService.class);
            String categoryName = productCategoryService.findById(product.getCategoryId()).getName();

            this.categoryName = categoryName;
        }

        if (!StringUtil.isBlank(product.getBrandId())) {
            ProductBrandService productBrandService = ApplicationUtil.getBean(ProductBrandService.class);
            String brandName = productBrandService.findById(product.getBrandId()).getName();

            this.brandName = brandName;
        }

        // 获取批次信息
        if (!StringUtil.isBlank(dto.getBatchId())) {
            ProductStockBatchService batchService = ApplicationUtil.getBean(ProductStockBatchService.class);
            ProductStockBatch batch = batchService.findById(dto.getBatchId());
            if (batch != null) {
                this.batchNumber = batch.getBatchNumber();
                
                // 通过批次获取仓库信息
                StoreCenterService storeCenterService = ApplicationUtil.getBean(StoreCenterService.class);
                StoreCenter sc = storeCenterService.findById(batch.getScId());
                this.scId = sc.getId();
                this.scCode = sc.getCode();
                this.scName = sc.getName();
            }
        }

        if (supplierId != null && !supplierId.trim().isEmpty()) {
            SupplierService supplierService = ApplicationUtil.getBean(SupplierService.class);
            Supplier supplier = supplierService.findById(supplierId);
            if (supplier != null) {
                this.supplierName = supplier.getName();
            }
        }
    }
}
