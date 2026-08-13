package com.lframework.xingyun.shkb.service.contract;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.ContractFile;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_contract_file(合同附件表)】的数据库操作Service
* @createDate 2025-05-09 11:47:33
*/
public interface ContractFileService extends BaseMpService<ContractFile> {

    /**
     * 上传合同附件
     * 
     * @param contractId 合同ID
     * @param files 文件列表
     * @return 上传成功的文件ID列表
     */
    List<String> uploadContractFiles(String contractId, List<MultipartFile> files);
    
    /**
     * 获取合同附件列表
     * 
     * @param contractId 合同ID
     * @return 附件列表
     */
    List<ContractFile> getContractFiles(String contractId);
    
    /**
     * 删除合同附件
     * 
     * @param id 附件ID
     * @return 是否删除成功
     */
    boolean deleteContractFile(String id);
    
    /**
     * 批量删除合同附件
     * 
     * @param ids 附件ID列表
     * @return 成功删除的数量
     */
    int batchDeleteContractFiles(List<String> ids);
}
