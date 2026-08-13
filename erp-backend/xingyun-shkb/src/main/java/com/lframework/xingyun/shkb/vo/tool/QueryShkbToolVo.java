package com.lframework.xingyun.shkb.vo.tool;

import com.lframework.starter.web.core.vo.PageVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class QueryShkbToolVo extends PageVo {

    private static final long serialVersionUID = 1L;

    /**
     * 管理区域
     */
    @ApiModelProperty("管理区域")
    private String managementArea;

    /**
     * 管理编号
     */
    @ApiModelProperty("管理编号")
    private String code;

    /**
     * 工具名称
     */
    @ApiModelProperty("工具名称")
    private String name;

    /**
     * 证书编号
     */
    @ApiModelProperty("证书编号")
    private String certificateNumber;

    /**
     * 型号
     */
    @ApiModelProperty("型号")
    private String model;

    /**
     * 规格
     */
    @ApiModelProperty("规格")
    private String specification;

    /**
     * 计量标准
     */
    @ApiModelProperty("计量标准")
    private String standard;

    /**
     * 精度
     */
    @ApiModelProperty("精度")
    private String precision;

    /**
     * 存放位置
     */
    @ApiModelProperty("存放位置")
    private String storageLocation;

    /**
     * 上次维保时间起始
     */
    @ApiModelProperty("上次维保时间起始")
    private LocalDateTime lastMaintenanceTimeStart;

    /**
     * 上次维保时间截止
     */
    @ApiModelProperty("上次维保时间截止")
    private LocalDateTime lastMaintenanceTimeEnd;

    /**
     * 下次维保时间起始
     */
    @ApiModelProperty("下次维保时间起始")
    private LocalDateTime nextMaintenanceTimeStart;

    /**
     * 下次维保时间截止
     */
    @ApiModelProperty("下次维保时间截止")
    private LocalDateTime nextMaintenanceTimeEnd;

    /**
     * 计量周期
     */
    @ApiModelProperty("计量周期")
    private String calibrationPeriod;

    /**
     * 有效期（文本，已废弃）
     */
    @ApiModelProperty("有效期（文本，已废弃）")
    private String validity;

    /**
     * 有效期起始（维保到期日期起始）
     */
    @ApiModelProperty("有效期起始（维保到期日期起始）")
    private LocalDateTime expirationTimeStart;

    /**
     * 有效期截止（维保到期日期截止）
     */
    @ApiModelProperty("有效期截止（维保到期日期截止）")
    private LocalDateTime expirationTimeEnd;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private Boolean available;

    /**
     * 创建时间起始
     */
    @ApiModelProperty("创建时间起始")
    private LocalDateTime createTimeStart;

    /**
     * 创建时间截止
     */
    @ApiModelProperty("创建时间截止")
    private LocalDateTime createTimeEnd;

    /**
     * 按维保到期日期排序（asc 或 desc）
     */
    @ApiModelProperty("按维保到期日期排序（asc 或 desc）")
    private String expirationTimeOrder;

    /**
     * 按创建时间排序（asc 或 desc）
     */
    @ApiModelProperty("按创建时间排序（asc 或 desc）")
    private String createTimeOrder;
}
