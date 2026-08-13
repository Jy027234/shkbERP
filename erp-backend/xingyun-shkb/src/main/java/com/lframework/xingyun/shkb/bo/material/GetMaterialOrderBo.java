package com.lframework.xingyun.shkb.bo.material;

import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.xingyun.shkb.dto.material.MaterialOrderFullDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 发料单详情Bo
 *
 * @author kison
 */
@Data
public class GetMaterialOrderBo extends BaseBo<MaterialOrderFullDto> {

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
     * 发料申请单号
     */
    @ApiModelProperty("发料申请单号")
    private String materialApplyCode;

    /**
     * 发料单明细
     */
    @ApiModelProperty("发料单明细")
    private List<GetMaterialOrderDetailBo> details;

    /**
     * 是否已全部出库完毕
     */
    @ApiModelProperty("是否已全部出库完毕")
    private Boolean isOutFinish;

    public GetMaterialOrderBo() {
    }

    public GetMaterialOrderBo(MaterialOrderFullDto dto) {
        super(dto);
    }
}
