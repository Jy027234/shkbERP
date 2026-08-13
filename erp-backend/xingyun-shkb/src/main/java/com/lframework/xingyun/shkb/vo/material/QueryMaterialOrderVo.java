package com.lframework.xingyun.shkb.vo.material;

import com.lframework.starter.web.core.vo.PageVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 发料单查询Vo
 *
 * @author kison
 */
@Data
public class QueryMaterialOrderVo extends PageVo {

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
     * 合同编号
     */
    @ApiModelProperty("合同编号")
    private String contractCode;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createBy;

    /**
     * 创建时间-起
     */
    @ApiModelProperty("创建时间-起")
    private LocalDateTime createTimeStart;

    /**
     * 创建时间-止
     */
    @ApiModelProperty("创建时间-止")
    private LocalDateTime createTimeEnd;

    /**
     * 发料申请单ID
     */
    @ApiModelProperty("发料申请单ID")
    private String materialApplyId;

    /**
     * 是否已完成出库
     */
    @ApiModelProperty("是否已完成出库")
    private Boolean isOutFinish;
}
