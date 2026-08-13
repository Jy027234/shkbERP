package com.lframework.xingyun.shkb.vo.contract.task;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContractTaskNonPartFileVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件访问路径
     */
    @ApiModelProperty(value = "文件访问路径", required = true)
    @NotBlank(message = "文件访问路径不能为空！")
    private String url;

    /**
     * 文件后缀
     */
    @ApiModelProperty(value = "文件后缀", required = true)
    @NotBlank(message = "文件后缀不能为空！")
    private String fileSuffix;

    /**
     * 文件大小
     */
    @ApiModelProperty(value = "文件大小", required = true)
    @NotBlank(message = "文件大小不能为空！")
    private String fileSize;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称", required = true)
    @NotBlank(message = "文件名称不能为空！")
    private String fileName;

    /**
     * ContentType
     */
    @ApiModelProperty(value = "ContentType", required = true)
    @NotBlank(message = "ContentType不能为空！")
    private String contentType;
}
