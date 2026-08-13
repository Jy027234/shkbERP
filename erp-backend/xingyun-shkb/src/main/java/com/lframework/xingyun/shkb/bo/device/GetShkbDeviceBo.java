package com.lframework.xingyun.shkb.bo.device;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.xingyun.shkb.entity.ShkbDevice;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetShkbDeviceBo extends BaseBo<ShkbDevice> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 设备编号
     */
    @ApiModelProperty("设备编号")
    private String code;

    /**
     * 设备名称
     */
    @ApiModelProperty("设备名称")
    private String name;

    /**
     * 管理区域
     */
    @ApiModelProperty("管理区域")
    private String managementArea;

    /**
     * 维保项目
     */
    @ApiModelProperty("维保项目")
    private String maintenanceProject;

    /**
     * 维保间隔（月）
     */
    @ApiModelProperty("维保间隔（月）")
    private Integer maintenanceInterval;

    /**
     * 维保工卡
     */
    @ApiModelProperty("维保工卡")
    private String maintenanceCard;

    /**
     * 上次维保时间
     */
    @ApiModelProperty("上次维保时间")
    @JsonFormat(pattern = StringPool.DATE_PATTERN)
    private LocalDate lastMaintenanceTime;

    /**
     * 下次维保时间
     */
    @ApiModelProperty("下次维保时间")
    @JsonFormat(pattern = StringPool.DATE_PATTERN)
    private LocalDate nextMaintenanceTime;

    /**
     * 设备状态
     * false-停用 true-启用
     */
    @ApiModelProperty("设备状态")
    private Boolean available;

    /**
     * 状态文本
     */
    @ApiModelProperty("状态文本")
    private String availableText;

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

    public GetShkbDeviceBo() {
    }

    public GetShkbDeviceBo(ShkbDevice dto) {
        super(dto);
    }

    @Override
    protected void afterInit(ShkbDevice dto) {
        this.availableText = dto.getAvailable() ? "启用" : "停用";
    }
}
