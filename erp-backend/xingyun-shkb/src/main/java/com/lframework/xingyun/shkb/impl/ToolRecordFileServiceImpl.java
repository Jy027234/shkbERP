package com.lframework.xingyun.shkb.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.common.utils.FileUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.UploadUtil;
import com.lframework.xingyun.shkb.entity.ToolRecordFile;
import com.lframework.xingyun.shkb.mappers.ShkbToolRecordMapper;
import com.lframework.xingyun.shkb.service.ToolRecordFileService;
import com.lframework.xingyun.shkb.mappers.ToolRecordFileMapper;
import com.lframework.xingyun.shkb.utils.ShkbUploadFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author kison
* @description 针对表【shkb_tool_record_file(工具计量记录附件)】的数据库操作Service实现
* @createDate 2025-07-11 10:44:29
*/
@Service
public class ToolRecordFileServiceImpl extends BaseMpServiceImpl<ToolRecordFileMapper, ToolRecordFile>
    implements ToolRecordFileService {

    @Autowired
    private ShkbToolRecordMapper toolRecordMapper;

    @Autowired
    private ShkbUploadFileUtil shkbUploadFileUtil;

    /**
     * 上传工具计量记录附件
     *
     * @param recordId 计量记录ID
     * @param files 文件列表
     * @return 上传成功的文件ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> uploadToolRecordFiles(String recordId, List<MultipartFile> files) {
        List<String> fileIds = new ArrayList<>();

        if (toolRecordMapper.selectById(recordId) == null) {
            throw new DefaultClientException("工具计量记录不存在");
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
            ToolRecordFile fileRecord = new ToolRecordFile();
            fileRecord.setId(IdUtil.getId());
            fileRecord.setRecordId(recordId);
            fileRecord.setUrl(url);
            // createTime由框架自动填充
            
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
     * 获取工具计量记录附件列表
     *
     * @param recordId 计量记录ID
     * @return 附件列表
     */
    @Override
    public List<ToolRecordFile> getToolRecordFiles(String recordId) {
        LambdaQueryWrapper<ToolRecordFile> queryWrapper = Wrappers.lambdaQuery(ToolRecordFile.class)
                .eq(ToolRecordFile::getRecordId, recordId)
                .orderByDesc(ToolRecordFile::getCreateTime);
        
        return this.list(queryWrapper);
    }

    /**
     * 删除工具计量记录附件
     *
     * @param id 附件ID
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteToolRecordFile(String id) {
        // 获取附件信息
        ToolRecordFile file = this.getById(id);
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
     * 批量删除工具计量记录附件
     *
     * @param ids 附件ID列表
     * @return 成功删除的数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteToolRecordFiles(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return 0;
        }
        
        int count = 0;
        for (String id : ids) {
            if (deleteToolRecordFile(id)) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * 根据计量记录ID删除所有附件
     *
     * @param recordId 计量记录ID
     * @return 成功删除的数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByRecordId(String recordId) {
        // 查询该计量记录的所有附件
        List<ToolRecordFile> files = getToolRecordFiles(recordId);
        
        if (CollectionUtil.isEmpty(files)) {
            return 0;
        }
        
        // 提取附件ID列表
        List<String> fileIds = files.stream()
                .map(ToolRecordFile::getId)
                .collect(Collectors.toList());
        
        // 批量删除附件
        return batchDeleteToolRecordFiles(fileIds);
    }
}
