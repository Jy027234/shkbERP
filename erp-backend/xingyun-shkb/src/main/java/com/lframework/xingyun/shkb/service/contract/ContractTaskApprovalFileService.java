package com.lframework.xingyun.shkb.service.contract;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.ContractTaskApprovalFile;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_contract_task_approval_file(合同任务放行文件)】的数据库操作Service
* @createDate 2025-05-09 11:47:33
*/
public interface ContractTaskApprovalFileService extends BaseMpService<ContractTaskApprovalFile> {

    /**
     * 上传放行文件
     */
    List<String> uploadApprovalFiles(String taskId, List<MultipartFile> files);

    /**
     * 获取放行文件列表
     */
    List<ContractTaskApprovalFile> getApprovalFiles(String taskId);

    /**
     * 删除放行文件
     */
    boolean deleteApprovalFile(String id);
}
