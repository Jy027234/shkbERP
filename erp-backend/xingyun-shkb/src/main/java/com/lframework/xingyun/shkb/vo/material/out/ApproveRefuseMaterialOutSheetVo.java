package com.lframework.xingyun.shkb.vo.material.out;

import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 审核拒绝发料出库单VO
 */
@Data
public class ApproveRefuseMaterialOutSheetVo implements BaseVo, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ApiModelProperty(value = "ID", required = true)
    @NotBlank(message = "ID不能为空！")
    private String id;

    /**
     * 拒绝原因
     */
    @ApiModelProperty(value = "拒绝原因", required = true)
    @NotBlank(message = "拒绝原因不能为空！")
    private String refuseReason;
}
