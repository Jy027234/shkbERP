package com.lframework.xingyun.shkb.bo.material;

import com.lframework.starter.web.core.bo.BasePrintDataBo;
import com.lframework.xingyun.shkb.dto.material.MaterialOrderFullDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 发料单打印Bo
 *
 * @author kison
 */
@Data
public class PrintMaterialOrderBo extends BasePrintDataBo<MaterialOrderFullDto> {

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
     * 发料单明细
     */
    @ApiModelProperty("发料单明细")
    private List<GetMaterialOrderDetailBo> details;

    public PrintMaterialOrderBo() {
    }

    public PrintMaterialOrderBo(MaterialOrderFullDto dto) {
        super(dto);
    }
}
