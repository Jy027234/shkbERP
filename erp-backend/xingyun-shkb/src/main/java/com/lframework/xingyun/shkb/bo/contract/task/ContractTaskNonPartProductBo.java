package com.lframework.xingyun.shkb.bo.contract.task;

import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.xingyun.shkb.entity.ContractTaskNonPartProduct;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContractTaskNonPartProductBo extends BaseBo<BaseDto> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 任务id
     */
    @ApiModelProperty("任务ID")
    private String taskId;

    /**
     * 商品id
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
     * 机型名称
     */
    @ApiModelProperty("机型名称")
    private String machineTypeName;
    
    /**
     * 件号
     */
    @ApiModelProperty("件号")
    private String partNumber;

    /**
     * 数量
     */
    @ApiModelProperty("数量")
    private Integer quantity;

    /**
     * 原因说明
     */
    @ApiModelProperty("原因说明")
    private String reason;

    /**
     * 附件列表
     */
    @ApiModelProperty("附件列表")
    private List<ContractTaskNonPartFileBo> files;

    public ContractTaskNonPartProductBo() {
    }

    public ContractTaskNonPartProductBo(ContractTaskNonPartProduct dto) {
        super(dto);
    }
}
