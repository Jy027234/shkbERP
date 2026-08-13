package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.ShkbToolFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_tool_file(工具计量证书)】的数据库操作Service
* @createDate 2025-06-06 10:07:22
*/
public interface ShkbToolFileService extends BaseMpService<ShkbToolFile> {

    /**
     * 上传工具附件
     * 
     * @param toolId 工具ID
     * @param files 文件列表
     * @return 上传成功的文件ID列表
     */
    List<String> uploadToolFiles(String toolId, List<MultipartFile> files);
    
    /**
     * 获取工具附件列表
     * 
     * @param toolId 工具ID
     * @return 附件列表
     */
    List<ShkbToolFile> getToolFiles(String toolId);
    
    /**
     * 删除工具附件
     * 
     * @param id 附件ID
     * @return 是否删除成功
     */
    boolean deleteToolFile(String id);
    
    /**
     * 批量删除工具附件
     * 
     * @param ids 附件ID列表
     * @return 成功删除的数量
     */
    int batchDeleteToolFiles(List<String> ids);
}
