package com.lframework.xingyun.shkb.bo.material.out;

import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.dto.BaseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发料出库单明细批次Bo
 */
@Data
public class GetMaterialOutSheetDetailLotBo extends BaseBo<BaseDto> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

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

    public GetMaterialOutSheetDetailLotBo() {
    }
}
