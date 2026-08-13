package com.lframework.xingyun.shkb.impl.contract;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.FileUtil;
import com.lframework.starter.web.core.utils.UploadUtil;
import com.lframework.xingyun.shkb.bo.file.FileUploadBo;
import com.lframework.xingyun.shkb.service.contract.CommonFileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 通用文件服务实现类
 */
@Service
public class CommonFileServiceImpl implements CommonFileService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件信息
     */
    @Override
    public FileUploadBo upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new DefaultClientException("上传文件不能为空！");
        }

        // 上传文件并获取URL
        String url = UploadUtil.upload(file).getUrl();

        // 创建返回对象
        FileUploadBo result = new FileUploadBo();
        
        // 设置文件访问路径
        result.setUrl(url);
        
        // 设置文件后缀
        result.setFileSuffix(FileUtil.getSuffix(file.getOriginalFilename()));
        
        // 设置文件大小（可读格式）
        result.setFileSize(FileUtil.readableFileSize(file.getSize()));
        
        // 设置原始文件大小（字节）
        result.setFileSizeBytes(file.getSize());
        
        // 设置文件名称
        result.setFileName(file.getOriginalFilename());
        
        // 设置ContentType
        result.setContentType(file.getContentType());

        return result;
    }
}
