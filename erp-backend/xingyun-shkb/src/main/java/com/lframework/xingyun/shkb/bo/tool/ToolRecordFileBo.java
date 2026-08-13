package com.lframework.xingyun.shkb.bo.tool;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.xingyun.shkb.entity.ToolRecordFile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工具计量记录附件Bo
 */
@Data
public class ToolRecordFileBo extends BaseBo<ToolRecordFile> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 计量记录ID
     */
    @ApiModelProperty("计量记录ID")
    private String recordId;

    /**
     * 文件名
     */
    @ApiModelProperty("文件名")
    private String fileName;

    /**
     * 文件后缀
     */
    @ApiModelProperty("文件后缀")
    private String fileSuffix;

    /**
     * 文件大小
     */
    @ApiModelProperty("文件大小")
    private String fileSize;

    /**
     * 文件类型
     */
    @ApiModelProperty("文件类型")
    private String contentType;

    /**
     * 文件URL
     */
    @ApiModelProperty("文件URL")
    private String url;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private LocalDateTime createTime;

    public ToolRecordFileBo() {
    }

    public ToolRecordFileBo(ToolRecordFile dto) {
        super(dto);
    }
}
