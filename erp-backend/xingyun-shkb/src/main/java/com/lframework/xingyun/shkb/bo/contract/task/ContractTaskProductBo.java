package com.lframework.xingyun.shkb.bo.contract.task;

import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.service.product.ProductService;
import com.lframework.xingyun.shkb.entity.WorkCard;
import com.lframework.xingyun.shkb.entity.WorkCardProduct;
import com.lframework.xingyun.shkb.service.workcard.WorkCardService;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 合同任务必换件 Bo
 *
 * @author kison
 */
@Data
public class ContractTaskProductBo extends BaseBo<WorkCardProduct> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 工卡ID
     */
    @ApiModelProperty("工卡ID")
    private String workCardId;

    /**
     * 工卡号
     */
    @ApiModelProperty("工卡号")
    private String workCardCode;

    /**
     * 工卡名称
     */
    @ApiModelProperty("工卡名称")
    private String workCardName;

    /**
     * 维修类型ID
     */
    @ApiModelProperty("维修类型ID")
    private String repairTypeId;

    /**
     * 维修类型名称
     */
    @ApiModelProperty("维修类型名称")
    private String repairTypeName;

    /**
     * 件号ID
     */
    @ApiModelProperty("件号ID")
    private String partNumberId;

    /**
     * 件号名称
     */
    @ApiModelProperty("件号名称")
    private String partNumber;

    /**
     * 件号
     */
    @ApiModelProperty("件号")
    private String partNumberCode;

    /**
     * 工卡机型名称
     */
    @ApiModelProperty("机型名称")
    private String machineTypeName;

    /**
     * 换件清单机型名称
     */
    @ApiModelProperty("换件清单机型名称")

    private String productMachineTypeName;

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
     * 数量
     */
    @ApiModelProperty("数量")
    private Integer quantity;

    public ContractTaskProductBo() {
    }

    public ContractTaskProductBo(WorkCardProduct dto) {
        super(dto);
    }


}
