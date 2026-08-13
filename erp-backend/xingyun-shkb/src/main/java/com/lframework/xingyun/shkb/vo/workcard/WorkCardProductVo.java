package com.lframework.xingyun.shkb.vo.workcard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 工卡必换件 VO
 *
 * @author kison
 */
@Data
public class WorkCardProductVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工卡ID
     */
    @ApiModelProperty(value = "工卡ID", required = true)
    @NotBlank(message = "工卡ID不能为空！")
    private String workCardId;

    /**
     * 商品ID列表
     */
    @ApiModelProperty("商品ID列表")
    private List<String> productIds;
}
