package com.lframework.xingyun.shkb.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.common.utils.FileUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.UploadUtil;
import com.lframework.xingyun.shkb.entity.ShkbToolFile;
import com.lframework.xingyun.shkb.mappers.ShkbToolMapper;
import com.lframework.xingyun.shkb.service.ShkbToolFileService;
import com.lframework.xingyun.shkb.mappers.ShkbToolFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
* @author kison
* @description 针对表【shkb_tool_file(工具计量证书)】的数据库操作Service实现
* @createDate 2025-06-06 10:07:22
*/
@Service
public class ShkbToolFileServiceImpl extends BaseMpServiceImpl<ShkbToolFileMapper, ShkbToolFile>
    implements ShkbToolFileService {

    @Autowired
    private ShkbToolMapper toolMapper;

    /**
     * 上传工具附件
     *
     * @param toolId 工具ID
     * @param files  文件列表
     * @return 上传成功的文件ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> uploadToolFiles(String toolId, List<MultipartFile> files) {
        List<String> fileIds = new ArrayList<>();

        if (toolMapper.selectById(toolId) == null) {
            throw new DefaultClientException("工具不存在");
        }
        
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
            ShkbToolFile fileRecord = new ShkbToolFile();
            fileRecord.setId(IdUtil.getId());
            fileRecord.setToolId(toolId);
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
     * 获取工具附件列表
     *
     * @param toolId 工具ID
     * @return 附件列表
     */
    @Override
    public List<ShkbToolFile> getToolFiles(String toolId) {
        LambdaQueryWrapper<ShkbToolFile> queryWrapper = Wrappers.lambdaQuery(ShkbToolFile.class)
                .eq(ShkbToolFile::getToolId, toolId)
                .orderByDesc(ShkbToolFile::getCreateTime);
        
        return this.list(queryWrapper);
    }

    /**
     * 删除工具附件
     *
     * @param id 附件ID
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteToolFile(String id) {
        // 获取附件信息
        ShkbToolFile file = this.getById(id);
        if (file == null) {
            return false;
        }
        
        // 删除文件
        try {
            // 如果需要删除物理文件，这里可以调用UploadUtil的删除方法
            // UploadUtil.deleteFile(file.getUrl());
            
            // 删除数据库记录
            return this.removeById(id);
        } catch (Exception e) {
            throw new RuntimeException("删除附件失败：" + e.getMessage(), e);
        }
    }

    /**
     * 批量删除工具附件
     *
     * @param ids 附件ID列表
     * @return 成功删除的数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteToolFiles(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return 0;
        }
        
        int count = 0;
        for (String id : ids) {
            if (deleteToolFile(id)) {
                count++;
            }
        }
        
        return count;
    }
}
