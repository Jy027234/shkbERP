package com.lframework.xingyun.shkb.bo.tool;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.xingyun.shkb.entity.ShkbTool;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetShkbToolBo extends BaseBo<ShkbTool> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

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
     * 管理区域
     */
    @ApiModelProperty("管理区域")
    private String managementArea;

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
     * 上次维保时间
     */
    @ApiModelProperty("上次维保时间")
    private LocalDate lastMaintenanceTime;

    /**
     * 下次维保时间
     */
    @ApiModelProperty("下次维保时间")
    private LocalDate nextMaintenanceTime;

    /**
     * 维保到期日期
     */
    @ApiModelProperty("维保到期日期")
    private LocalDate expirationTime;

    /**
     * 计量周期
     */
    @ApiModelProperty("计量周期")
    private String calibrationPeriod;

    /**
     * 有效期
     */
    @ApiModelProperty("有效期")
    private String validity;

    /**
     * 上次维保单位
     */
    @ApiModelProperty("上次维保单位")
    private String lastMaintenanceUnit;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private Boolean available;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String description;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private LocalDateTime createTime;

    /**
     * 修改人
     */
    @ApiModelProperty("修改人")
    private String updateBy;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private LocalDateTime updateTime;

    public GetShkbToolBo() {
    }

    public GetShkbToolBo(ShkbTool dto) {
        super(dto);
    }
}
