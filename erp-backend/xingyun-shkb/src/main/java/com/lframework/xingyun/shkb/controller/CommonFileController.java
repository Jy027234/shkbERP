package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.xingyun.shkb.bo.file.FileUploadBo;
import com.lframework.xingyun.shkb.service.contract.CommonFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 通用文件上传控制器
 */
@Api(tags = "通用文件上传")
@Validated
@RestController
@RequestMapping("/file")
public class CommonFileController extends DefaultBaseController {

    @Autowired
    private CommonFileService commonFileService;

    /**
     * 上传文件
     * 
     * @param file 文件
     * @return 文件信息
     */
    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public InvokeResult<FileUploadBo> upload(@RequestParam("file") MultipartFile file) {
        // 调用服务层处理文件上传
        FileUploadBo result = commonFileService.upload(file);
        return InvokeResultBuilder.success(result);
    }
}
