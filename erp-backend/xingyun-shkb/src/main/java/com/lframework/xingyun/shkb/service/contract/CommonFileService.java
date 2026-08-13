package com.lframework.xingyun.shkb.service.contract;

import com.lframework.xingyun.shkb.bo.file.FileUploadBo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 通用文件服务接口
 */
public interface CommonFileService {

    /**
     * 上传文件
     * 
     * @param file 文件
     * @return 文件信息
     */
    FileUploadBo upload(MultipartFile file);
}
