package com.lframework.xingyun.shkb.bo.material.out;

import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.xingyun.shkb.dto.material.out.MaterialOutSheetDetailSerialDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发料出库单明细序列号Bo
 */
@Data
public class GetMaterialOutSheetDetailSerialBo extends BaseBo<BaseDto> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 序列号
     */
    @ApiModelProperty("序列号")
    private String serialNumber;

    public GetMaterialOutSheetDetailSerialBo() {
    }

    public GetMaterialOutSheetDetailSerialBo(MaterialOutSheetDetailSerialDto dto) {
        if (dto == null) {
            return;
        }
        this.setId(dto.getId());
        this.serialNumber = dto.getSerialNumber();
    }
}
