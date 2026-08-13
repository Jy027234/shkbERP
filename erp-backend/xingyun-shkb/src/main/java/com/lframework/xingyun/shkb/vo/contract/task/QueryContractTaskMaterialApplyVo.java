package com.lframework.xingyun.shkb.vo.contract.task;

import com.lframework.starter.web.core.vo.PageVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QueryContractTaskMaterialApplyVo extends PageVo {

    private static final long serialVersionUID = 1L;

    /**
     * 合同编号
     */
    @ApiModelProperty("合同编号")
    private String contractCode;

    /**
     * 申请编号
     */
    @ApiModelProperty("申请编号")
    private String applyCode;

    /**
     * 申请时间起始
     */
    @ApiModelProperty("申请时间起始")
    private String createTimeStart;

    /**
     * 申请时间截止
     */
    @ApiModelProperty("申请时间截止")
    private String createTimeEnd;

    /**
     * 是否已创建发料单
     * true：已创建发料单；false：未创建发料单；null：不过滤
     */
    @ApiModelProperty("是否已创建发料单")
    private Boolean hasMaterialOrder;
}
