package com.lframework.xingyun.shkb.vo.contract.task;

import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;
import java.util.List;

/**
 * 合同任务必换件数量VO
 *
 * @author kison
 */
@Data
public class ContractTaskWorkCardProductVo implements BaseVo, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @ApiModelProperty(value = "任务ID", required = true)
    @NotBlank(message = "任务ID不能为空！")
    private String taskId;

    /**
     * 必换件列表
     */
    @ApiModelProperty(value = "必换件列表", required = true)
    @NotEmpty(message = "必换件列表不能为空！")
    @Valid
    private List<ReplacementPartProduct> products;

    /**
     * 必换件商品
     */
    @Data
    public static class ReplacementPartProduct implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 工卡ID
         */
        @ApiModelProperty(value = "工卡ID", required = true)
        @NotBlank(message = "工卡ID不能为空！")
        private String workCardId;

        /**
         * 商品ID
         */
        @ApiModelProperty(value = "商品ID", required = true)
        @NotBlank(message = "商品ID不能为空！")
        private String productId;

        /**
         * 数量
         */
        @ApiModelProperty(value = "数量", required = true)
        @NotNull(message = "数量不能为空！")
        @Positive(message = "数量必须大于0！")
        private Integer quantity;
    }
}
