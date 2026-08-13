package com.lframework.xingyun.shkb.vo.material.out;

import com.lframework.starter.web.core.vo.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 创建发料出库单VO
 */
@Data
public class CreateMaterialOutSheetVo implements BaseVo, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 仓库ID
     */
    @ApiModelProperty(value = "仓库ID", required = true)
    @NotBlank(message = "仓库ID不能为空！")
    private String scId;

    /**
     * 供应商ID
     */
    @ApiModelProperty("供应商ID")
    private String supplierId;

    /**
     * 发料员ID
     */
    @ApiModelProperty("发料员ID")
    private String materialUserId;

    /**
     * 发料日期
     */
    @ApiModelProperty("发料日期")
    private LocalDate materialDate;

    /**
     * 发料单ID
     */
    @ApiModelProperty("发料单ID")
    private String materialOrderId;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 明细
     */
    @ApiModelProperty(value = "明细", required = true)
    @Valid
    @NotEmpty(message = "明细不能为空！")
    private List<CreateMaterialOutSheetDetailVo> details;

    @Data
    public static class CreateMaterialOutSheetDetailVo implements BaseVo, Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 商品ID
         */
        @ApiModelProperty(value = "商品ID", required = true)
        @NotBlank(message = "商品ID不能为空！")
        private String productId;

        /**
         * 出库数量
         */
        @ApiModelProperty(value = "出库数量", required = true)
        @NotNull(message = "出库数量不能为空！")
        @Positive(message = "出库数量必须大于0！")
        private Integer outNum;

        /**
         * 出库单明细出库数量
         */
        @ApiModelProperty(value = "出库单明细出库数量", required = true)
        @NotNull(message = "发料数量不能为空！")
        @Positive(message = "发料数量必须大于0！")
        private Integer orderNum;

        /**
         * 含税价格
         */
        @ApiModelProperty("含税价格")
        private java.math.BigDecimal taxPrice;

        /**
         * 备注
         */
        @ApiModelProperty("备注")
        private String description;

        /**
         * 批次库存ID
         */
        @ApiModelProperty("批次库存ID")
        private String stockBatchId;

        /**
         * 序列号库存ID列表
         */
        @ApiModelProperty("序列号库存ID列表")
        private List<String> serials;

        /**
         * 序列号列表
         */
        @ApiModelProperty("序列号列表")
        private String serialNumbers;



        /**
         * 出库单明细id
         */
        @ApiModelProperty("出库单明细id")
        private String materialOrderDetailId;

    }



    /**
     * 验证方法
     */
    public void validate() {
        // 基本验证逻辑
        if (details != null) {
            for (CreateMaterialOutSheetDetailVo detail : details) {
                if (detail.getOutNum() == null || detail.getOutNum() <= 0) {
                    throw new IllegalArgumentException("出库数量必须大于0！");
                }
            }
        }
    }
}
