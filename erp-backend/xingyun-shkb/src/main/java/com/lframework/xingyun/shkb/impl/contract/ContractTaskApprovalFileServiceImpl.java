package com.lframework.xingyun.shkb.impl.contract;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.xingyun.shkb.entity.ContractTaskApprovalFile;
import com.lframework.xingyun.shkb.service.contract.ContractTaskApprovalFileService;
import com.lframework.xingyun.shkb.mappers.ContractTaskApprovalFileMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.common.utils.FileUtil;
import com.lframework.starter.web.core.utils.UploadUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
* @author kison
* @description 针对表【shkb_contract_task_approval_file(合同任务放行文件)】的数据库操作Service实现
* @createDate 2025-05-09 11:47:33
*/
@Service
public class ContractTaskApprovalFileServiceImpl extends BaseMpServiceImpl<ContractTaskApprovalFileMapper, ContractTaskApprovalFile>
    implements ContractTaskApprovalFileService{
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> uploadApprovalFiles(String taskId, List<MultipartFile> files) {
        List<String> fileIds = new ArrayList<>();
        if (CollectionUtil.isEmpty(files)) {
            return fileIds;
        }
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            String url = UploadUtil.upload(file).getUrl();
            ContractTaskApprovalFile rec = new ContractTaskApprovalFile();
            rec.setId(IdUtil.getId());
            rec.setTaskId(taskId);
            rec.setUrl(url);
            rec.setCreateTime(LocalDateTime.now());
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                int lastDotIndex = originalFilename.lastIndexOf('.');
                if (lastDotIndex > 0) {
                    rec.setFileSuffix(originalFilename.substring(lastDotIndex + 1));
                }
            }
            rec.setFileSize(FileUtil.readableFileSize(file.getSize()));
            rec.setFileName(file.getOriginalFilename());
            rec.setContentType(file.getContentType());
            this.save(rec);
            fileIds.add(rec.getId());
        }
        return fileIds;
    }

    @Override
    public List<ContractTaskApprovalFile> getApprovalFiles(String taskId) {
        LambdaQueryWrapper<ContractTaskApprovalFile> wrapper = Wrappers.lambdaQuery(ContractTaskApprovalFile.class)
            .eq(ContractTaskApprovalFile::getTaskId, taskId)
            .orderByDesc(ContractTaskApprovalFile::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteApprovalFile(String id) {
        ContractTaskApprovalFile file = this.getById(id);
        if (file == null) {
            return false;
        }
        // 如需删除物理文件，可在此调用 UploadUtil 删除
        return this.removeById(id);
    }
}




