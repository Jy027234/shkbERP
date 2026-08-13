package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.ToolRecordFile;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_tool_record_file(工具计量记录附件)】的数据库操作Service
* @createDate 2025-07-11 10:44:29
*/
public interface ToolRecordFileService extends BaseMpService<ToolRecordFile> {

    /**
     * 上传工具计量记录附件
     * 
     * @param recordId 计量记录ID
     * @param files 文件列表
     * @return 上传成功的文件ID列表
     */
    List<String> uploadToolRecordFiles(String recordId, List<MultipartFile> files);
    
    /**
     * 获取工具计量记录附件列表
     * 
     * @param recordId 计量记录ID
     * @return 附件列表
     */
    List<ToolRecordFile> getToolRecordFiles(String recordId);
    
    /**
     * 删除工具计量记录附件
     * 
     * @param id 附件ID
     * @return 是否删除成功
     */
    boolean deleteToolRecordFile(String id);
    
    /**
     * 批量删除工具计量记录附件
     * 
     * @param ids 附件ID列表
     * @return 成功删除的数量
     */
    int batchDeleteToolRecordFiles(List<String> ids);
    
    /**
     * 根据计量记录ID删除所有附件
     * 
     * @param recordId 计量记录ID
     * @return 成功删除的数量
     */
    int deleteByRecordId(String recordId);
}
