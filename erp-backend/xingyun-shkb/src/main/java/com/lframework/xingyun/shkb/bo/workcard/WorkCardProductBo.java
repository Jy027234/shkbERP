package com.lframework.xingyun.shkb.bo.workcard;

import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.entity.MachineType;
import com.lframework.xingyun.basedata.entity.PartNumber;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.service.machineType.MachineTypeService;
import com.lframework.xingyun.basedata.service.partNumber.PartNumberService;
import com.lframework.xingyun.basedata.service.product.ProductService;
import com.lframework.xingyun.shkb.entity.WorkCardProduct;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 工卡必换件 Bo
 *
 * @author kison
 */
@Data
public class WorkCardProductBo extends BaseBo<WorkCardProduct> {

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
     * 商品规格
     */
    @ApiModelProperty("商品规格")
    private String productSpec;

    /**
     * 商品单位
     */
    @ApiModelProperty("商品单位")
    private String productUnit;

    /**
     * 件号
     */
    @ApiModelProperty("件号")
    private String partNumber;

    /**
     * 机型
     */

    @ApiModelProperty("机型")
    private String machineType;



    /**
     * 工卡ID
     */
    @ApiModelProperty("工卡ID")
    private String workCardId;

    @ApiModelProperty("数量")
    private Integer quantity;

    public WorkCardProductBo() {
    }

    public WorkCardProductBo(WorkCardProduct dto) {
        super(dto);
    }

    @Override
    protected void afterInit(WorkCardProduct dto) {
        // 设置商品信息
        if (dto.getProductId() != null) {
            ProductService productService = ApplicationUtil.getBean(ProductService.class);
            Product product = productService.findById(dto.getProductId());
            if (product != null) {
                this.productCode = product.getCode();
                this.productName = product.getName();
                this.productSpec = product.getSpec();
                this.productUnit = product.getUnit();
                this.partNumber =  product.getCode();

                // 获取机型
                if(product.getMachineTypeId() != null) {
                    MachineTypeService machineTypeService = ApplicationUtil.getBean(MachineTypeService.class);
                    MachineType machineType = machineTypeService.findById(product.getMachineTypeId());
                    if(machineType != null) {
                        this.machineType = machineType.getName();
                    }
                }
            }
        }
    }
}
