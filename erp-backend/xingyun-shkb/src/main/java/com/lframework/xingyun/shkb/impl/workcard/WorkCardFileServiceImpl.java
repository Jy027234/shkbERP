package com.lframework.xingyun.shkb.impl.workcard;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.common.utils.FileUtil;
import com.lframework.starter.web.core.utils.UploadUtil;
import com.lframework.xingyun.shkb.entity.WorkCardFile;
import com.lframework.xingyun.shkb.mappers.WorkCardFileMapper;
import com.lframework.xingyun.shkb.service.workcard.WorkCardFileService;
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
* @description 针对表【shkb_work_card_file(工卡附件表)】的数据库操作Service实现
* @createDate 2025-05-15 15:52:38
*/
@Service
public class WorkCardFileServiceImpl extends BaseMpServiceImpl<WorkCardFileMapper, WorkCardFile>
    implements WorkCardFileService {

    @Autowired
    private ShkbUploadFileUtil shkbUploadFileUtil;

    /**
     * 上传工卡附件
     *
     * @param workCardId 工卡ID
     * @param files 文件列表
     * @return 上传成功的文件ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> uploadWorkCardFiles(String workCardId, List<MultipartFile> files) {
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
            WorkCardFile fileRecord = new WorkCardFile();
            fileRecord.setId(IdUtil.getId());
            fileRecord.setWorkCardId(workCardId);
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
     * 获取工卡附件列表
     *
     * @param workCardId 工卡ID
     * @return 附件列表
     */
    @Override
    public List<WorkCardFile> getWorkCardFiles(String workCardId) {
        LambdaQueryWrapper<WorkCardFile> queryWrapper = Wrappers.lambdaQuery(WorkCardFile.class)
                .eq(WorkCardFile::getWorkCardId, workCardId)
                .orderByDesc(WorkCardFile::getCreateTime);
        
        return this.list(queryWrapper);
    }
    
    /**
     * 删除工卡附件
     *
     * @param id 附件ID
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteWorkCardFile(String id) {
        // 获取附件信息
        WorkCardFile file = this.getById(id);
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
     * 批量删除工卡附件
     *
     * @param ids 附件ID列表
     * @return 成功删除的数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteWorkCardFiles(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return 0;
        }
        
        int count = 0;
        for (String id : ids) {
            if (deleteWorkCardFile(id)) {
                count++;
            }
        }
        
        return count;
    }
}
