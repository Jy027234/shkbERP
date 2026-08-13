package com.lframework.xingyun.shkb.dto.material.out;

import com.lframework.starter.web.core.dto.BaseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发料出库单明细序列号DTO
 */
@Data
public class MaterialOutSheetDetailSerialDto implements BaseDto {

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
     * 序列号
     */
    @ApiModelProperty("序列号")
    private String serialNumber;

    /**
     * 排序号
     */
    @ApiModelProperty("排序号")
    private Integer orderNo;
}
