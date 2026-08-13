package com.lframework.xingyun.shkb.vo.material.out;

import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 审核通过发料出库单VO
 */
@Data
public class ApprovePassMaterialOutSheetVo implements BaseVo, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ApiModelProperty(value = "ID", required = true)
    @NotBlank(message = "ID不能为空！")
    private String id;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;
}
