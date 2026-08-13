package com.lframework.xingyun.shkb.bo.material;

import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.xingyun.shkb.entity.MaterialOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 发料单列表查询Bo
 *
 * @author kison
 */
@Data
public class QueryMaterialOrderBo extends BaseBo<MaterialOrder> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 单号
     */
    @ApiModelProperty("单号")
    private String code;

    /**
     * 仓库ID
     */
    @ApiModelProperty("仓库ID")
    private String scId;

    /**
     * 仓库名称
     */
    @ApiModelProperty("仓库名称")
    private String scName;

    /**
     * 发料数量
     */
    @ApiModelProperty("发料数量")
    private Integer totalNum;

    /**
     * 已发料数量
     */
    @ApiModelProperty("已发料数量")
    private Integer totalOutNum;

    /**
     * 发料金额
     */
    @ApiModelProperty("发料金额")
    private BigDecimal totalAmount;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 发料申请单ID
     */
    @ApiModelProperty("发料申请单ID")
    private String materialApplyId;

    /**
     * 发料申请单编号
     */
    @ApiModelProperty("发料申请单编号")
    private String materialApplyCode;

    /**
     * 合同编号
     */
    @ApiModelProperty("合同编号")
    private String contractCode;

    /**
     * 合同名称
     */
    @ApiModelProperty("合同名称")
    private String contractName;

    /**
     * 客户编号
     */
    @ApiModelProperty("客户编号")
    private String customerCode;

    /**
     * 客户名称
     */
    @ApiModelProperty("客户名称")
    private String customerName;

    /**
     * 机型编号
     */
    @ApiModelProperty("机型编号")
    private String machineTypeCode;

    /**
     * 机型名称
     */
    @ApiModelProperty("机型名称")
    private String machineTypeName;

    /**
     * 件号编号
     */
    @ApiModelProperty("件号编号")
    private String partNumberCode;

    /**
     * 件号名称
     */
    @ApiModelProperty("件号名称")
    private String partNumberName;
    
    /**
     * 是否已全部出库完毕
     */
    @ApiModelProperty("是否已全部出库完毕")
    private Boolean isOutFinish;

    public QueryMaterialOrderBo() {
    }

    public QueryMaterialOrderBo(MaterialOrder dto) {
        super(dto);
    }
    
    // 提供兼容的访问器方法
    public Boolean getIsOutFinish() {
        return isOutFinish;
    }
}
