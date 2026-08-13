package com.lframework.xingyun.shkb.vo.material.out;

import com.lframework.starter.web.core.vo.PageVo;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 查询发料出库单VO
 */
@Data
public class QueryMaterialOutSheetVo extends PageVo {

    private static final long serialVersionUID = 1L;

    /**
     * 单号
     */
    @ApiModelProperty("单号")
    private String code;

    /**
     * 仓库ID
     */
    @ApiModelProperty("仓库ID")
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
     * 发料单ID
     */
    @ApiModelProperty("发料单ID")
    private String materialOrderId;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private Integer status;

    /**
     * 审核人
     */
    @ApiModelProperty("审核人")
    private String approveBy;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createBy;

    /**
     * 创建时间-开始
     */
    @ApiModelProperty("创建时间-开始")
    private LocalDateTime createTimeStart;

    /**
     * 创建时间-结束
     */
    @ApiModelProperty("创建时间-结束")
    private LocalDateTime createTimeEnd;

    /**
     * 审核时间-开始
     */
    @ApiModelProperty("审核时间-开始")
    private LocalDateTime approveTimeStart;

    /**
     * 审核时间-结束
     */
    @ApiModelProperty("审核时间-结束")
    private LocalDateTime approveTimeEnd;

    /**
     * 发料单号（用于模糊查询）
     */
    @ApiModelProperty("发料单号")
    private String materialOrderCode;
}
