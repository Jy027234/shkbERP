package com.lframework.xingyun.shkb.bo.material.out;

import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.xingyun.shkb.dto.material.out.MaterialOutSheetFullDto;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * 发料出库单打印Bo
 */
@Data
public class PrintMaterialOutSheetBo extends BaseBo<BaseDto> {

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
     * 供应商名称
     */
    @ApiModelProperty("供应商名称")
    private String supplierName;

    /**
     * 发料员姓名
     */
    @ApiModelProperty("发料员姓名")
    private String materialUserName;

    /**
     * 发料日期
     */
    @ApiModelProperty("发料日期")
    private Date materialDate;

    /**
     * 发料单号
     */
    @ApiModelProperty("发料单号")
    private String materialOrderCode;

    /**
     * 商品数量
     */
    @ApiModelProperty("商品数量")
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
     * 明细
     */
    @ApiModelProperty("明细")
    private List<GetMaterialOutSheetDetailBo> details;

    public PrintMaterialOutSheetBo() {
    }

    public PrintMaterialOutSheetBo(MaterialOutSheetFullDto dto) {
        super(dto);
    }
}
