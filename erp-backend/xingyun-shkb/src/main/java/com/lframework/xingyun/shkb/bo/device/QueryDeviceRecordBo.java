package com.lframework.xingyun.shkb.bo.device;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.xingyun.shkb.entity.DeviceRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class QueryDeviceRecordBo extends BaseBo<DeviceRecord> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 设备ID
     */
    @ApiModelProperty("设备ID")
    private String deviceId;

    /**
     * 维保人
     */
    @ApiModelProperty("维保人")
    private String maintenancenUser;

    /**
     * 维保时间
     */
    @ApiModelProperty("维保时间")
    @JsonFormat(pattern = StringPool.DATE_PATTERN)
    private LocalDate maintenanceTime;

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

    public QueryDeviceRecordBo() {
    }

    public QueryDeviceRecordBo(DeviceRecord dto) {
        super(dto);
    }
}
