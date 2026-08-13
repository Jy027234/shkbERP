package com.lframework.xingyun.shkb.dto.material.out;

import com.lframework.starter.web.core.dto.BaseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发料出库单明细批次DTO
 */
@Data
public class MaterialOutSheetDetailLotDto implements BaseDto {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 明细ID
     */
    @ApiModelProperty("明细ID")
    private String detailId;

    /**
     * 批次ID
     */
    @ApiModelProperty("批次ID")
    private String lotId;

    /**
     * 批次号
     */
    @ApiModelProperty("批次号")
    private String lotCode;

    /**
     * 出库数量
     */
    @ApiModelProperty("出库数量")
    private Integer outNum;

    /**
     * 排序号
     */
    @ApiModelProperty("排序号")
    private Integer orderNo;
}
