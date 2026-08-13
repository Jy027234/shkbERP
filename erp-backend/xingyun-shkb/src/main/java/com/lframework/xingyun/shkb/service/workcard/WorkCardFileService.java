package com.lframework.xingyun.shkb.service.workcard;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.WorkCardFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_work_card_file(工卡附件表)】的数据库操作Service
* @createDate 2025-05-15 15:52:38
*/
public interface WorkCardFileService extends BaseMpService<WorkCardFile> {

    /**
     * 上传工卡附件
     * 
     * @param workCardId 工卡ID
     * @param files 文件列表
     * @return 上传成功的文件ID列表
     */
    List<String> uploadWorkCardFiles(String workCardId, List<MultipartFile> files);
    
    /**
     * 获取工卡附件列表
     * 
     * @param workCardId 工卡ID
     * @return 附件列表
     */
    List<WorkCardFile> getWorkCardFiles(String workCardId);
    
    /**
     * 删除工卡附件
     * 
     * @param id 附件ID
     * @return 是否删除成功
     */
    boolean deleteWorkCardFile(String id);
    
    /**
     * 批量删除工卡附件
     * 
     * @param ids 附件ID列表
     * @return 成功删除的数量
     */
    int batchDeleteWorkCardFiles(List<String> ids);
}
