package com.lframework.xingyun.shkb.impl.contract;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.common.utils.FileUtil;
import com.lframework.starter.web.core.utils.UploadUtil;
import com.lframework.xingyun.shkb.entity.ContractFile;
import com.lframework.xingyun.shkb.mappers.ContractFileMapper;
import com.lframework.xingyun.shkb.mappers.ContractMapper;
import com.lframework.xingyun.shkb.service.contract.ContractFileService;
import com.lframework.xingyun.shkb.utils.ShkbUploadFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
* @author kison
* @description 针对表【shkb_contract_file(合同附件表)】的数据库操作Service实现
* @createDate 2025-05-09 11:47:33
*/
@Service
public class ContractFileServiceImpl extends BaseMpServiceImpl<ContractFileMapper, ContractFile>
    implements ContractFileService{

    private final ContractMapper contractMapper;

    @Autowired
    private ShkbUploadFileUtil shkbUploadFileUtil;

    public ContractFileServiceImpl(ContractMapper contractMapper) {
        this.contractMapper = contractMapper;
    }

    /**
     * 上传合同附件
     *
     * @param contractId 合同ID
     * @param files 文件列表
     * @return 上传成功的文件ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> uploadContractFiles(String contractId, List<MultipartFile> files) {
        if (contractMapper.selectById(contractId) == null) {
            throw new DefaultClientException("合同不存在！");
        }

        List<String> fileIds = new ArrayList<>();
        
        if (CollectionUtil.isEmpty(files)) {
            return fileIds;
        }
        
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            
            // 上传文件并获取URL
            String url = UploadUtil.upload(file).getUrl();
            
            // 创建附件记录
            ContractFile fileRecord = new ContractFile();
            fileRecord.setId(IdUtil.getId());
            fileRecord.setContractId(contractId);
            fileRecord.setUrl(url);
            fileRecord.setCreateTime(LocalDateTime.now());
            
            // 获取文件后缀
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                int lastDotIndex = originalFilename.lastIndexOf('.');
                if (lastDotIndex > 0) {
                    fileRecord.setFileSuffix(originalFilename.substring(lastDotIndex + 1));
                }
            }
            
            // 设置文件大小
            fileRecord.setFileSize(FileUtil.readableFileSize(file.getSize()));
            
            // 设置文件名称
            fileRecord.setFileName(file.getOriginalFilename());
            
            // 设置ContentType
            fileRecord.setContentType(file.getContentType());
            
            this.save(fileRecord);
            fileIds.add(fileRecord.getId());
        }
        
        return fileIds;
    }
    
    /**
     * 获取合同附件列表
     *
     * @param contractId 合同ID
     * @return 附件列表
     */
    @Override
    public List<ContractFile> getContractFiles(String contractId) {
        LambdaQueryWrapper<ContractFile> queryWrapper = Wrappers.lambdaQuery(ContractFile.class)
                .eq(ContractFile::getContractId, contractId)
                .orderByDesc(ContractFile::getCreateTime);
        
        return this.list(queryWrapper);
    }
    
    /**
     * 删除合同附件
     *
     * @param id 附件ID
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteContractFile(String id) {
        // 获取附件信息
        ContractFile file = this.getById(id);
        if (file == null) {
            return false;
        }
        
        // 删除文件（统一物理清理：严格限制在上传根目录内，拒绝 .. 逃逸与外部 ://）
        try {
            this.shkbUploadFileUtil.deletePhysicalFile(file.getUrl());

            // 删除数据库记录
            return this.removeById(id);
        } catch (Exception e) {
            throw new RuntimeException("删除附件失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 批量删除合同附件
     *
     * @param ids 附件ID列表
     * @return 成功删除的数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteContractFiles(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return 0;
        }
        
        int count = 0;
        for (String id : ids) {
            if (deleteContractFile(id)) {
                count++;
            }
        }
        
        return count;
    }
}
