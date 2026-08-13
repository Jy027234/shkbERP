package com.lframework.xingyun.shkb.bo.contract.task;

import com.lframework.starter.web.core.bo.BaseBo;
import com.lframework.starter.web.core.dto.BaseDto;
import com.lframework.xingyun.shkb.entity.ContractTaskNonPartFile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContractTaskNonPartFileBo extends BaseBo<BaseDto> {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private String id;

    /**
     * 任务id
     */
    @ApiModelProperty("任务ID")
    private String taskId;

    /**
     * 非必换件id
     */
    @ApiModelProperty("非必换件ID")
    private String nonPartId;

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
     * 文件大小
     */
    @ApiModelProperty("文件大小")
    private String fileSize;

    /**
     * 文件名称
     */
    @ApiModelProperty("文件名称")
    private String fileName;

    /**
     * ContentType
     */
    @ApiModelProperty("ContentType")
    private String contentType;

    public ContractTaskNonPartFileBo() {
    }

    public ContractTaskNonPartFileBo(ContractTaskNonPartFile dto) {
        super(dto);
    }
}
