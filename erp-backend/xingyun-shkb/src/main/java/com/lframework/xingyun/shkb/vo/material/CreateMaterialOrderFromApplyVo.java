package com.lframework.xingyun.shkb.vo.material;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 基于发料申请单创建发料单Vo
 *
 * @author kison
 */
@Data
public class CreateMaterialOrderFromApplyVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 发料申请单ID
     */
    @ApiModelProperty(value = "发料申请单ID", required = true)
    @NotBlank(message = "发料申请单ID不能为空！")
    private String materialApplyId;

    /**
     * 仓库ID
     */
    @ApiModelProperty(value = "仓库ID", required = true)
    @NotBlank(message = "仓库ID不能为空！")
    private String scId;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;
}
