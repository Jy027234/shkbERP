package com.lframework.xingyun.shkb.bo.file;

import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.dto.BaseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件上传返回信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileUploadBo extends BaseBo<BaseDto> {

    /**
     * 文件访问路径
     */
    @ApiModelProperty("文件访问路径")
    private String url;

    /**
     * 文件后缀
     */
    @ApiModelProperty("文件后缀")
    private String fileSuffix;

    /**
     * 文件大小（可读格式）
     */
    @ApiModelProperty("文件大小（可读格式）")
    private String fileSize;

    /**
     * 文件大小（字节）
     */
    @ApiModelProperty("文件大小（字节）")
    private Long fileSizeBytes;

    /**
     * 文件名称
     */
    @ApiModelProperty("文件名称")
    private String fileName;

    /**
     * 文件内容类型
     */
    @ApiModelProperty("文件内容类型")
    private String contentType;
}
