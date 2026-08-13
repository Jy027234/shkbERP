package com.lframework.xingyun.shkb.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.common.utils.FileUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.UploadUtil;
import com.lframework.xingyun.shkb.entity.ShkbDeviceFile;
import com.lframework.xingyun.shkb.mappers.ShkbDeviceFileMapper;
import com.lframework.xingyun.shkb.service.ShkbDeviceFileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author kison
* @description 针对表【shkb_device_file(设备附件管理)】的数据库操作Service实现
* @createDate 2025-12-23 14:29:16
*/
@Service
public class ShkbDeviceFileServiceImpl extends BaseMpServiceImpl<ShkbDeviceFileMapper, ShkbDeviceFile>
    implements ShkbDeviceFileService{

    /**
     * 上传设备附件
     *
     * @param deviceId 设备ID
     * @param files    文件列表
     * @return 上传成功的文件ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> uploadDeviceFiles(String deviceId, List<MultipartFile> files) {
        List<String> fileIds = new ArrayList<>();

        if (CollectionUtil.isEmpty(files)) {
            return fileIds;
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            String url = UploadUtil.upload(file).getUrl();

            ShkbDeviceFile fileRecord = new ShkbDeviceFile();
            fileRecord.setId(IdUtil.getId());
            fileRecord.setDeviceId(deviceId);
            fileRecord.setUrl(url);
            fileRecord.setCreateTime(new Date());

            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                int lastDotIndex = originalFilename.lastIndexOf('.');
                if (lastDotIndex > 0) {
                    fileRecord.setFileSuffix(originalFilename.substring(lastDotIndex + 1));
                }
            }

            fileRecord.setFileSize(FileUtil.readableFileSize(file.getSize()));
            fileRecord.setFileName(file.getOriginalFilename());
            fileRecord.setContentType(file.getContentType());

            this.save(fileRecord);
            fileIds.add(fileRecord.getId());
        }

        return fileIds;
    }

    /**
     * 获取设备附件列表
     *
     * @param deviceId 设备ID
     * @return 附件列表
     */
    @Override
    public List<ShkbDeviceFile> getDeviceFiles(String deviceId) {
        LambdaQueryWrapper<ShkbDeviceFile> queryWrapper = Wrappers.lambdaQuery(ShkbDeviceFile.class)
                .eq(ShkbDeviceFile::getDeviceId, deviceId)
                .orderByDesc(ShkbDeviceFile::getCreateTime);

        return this.list(queryWrapper);
    }

    /**
     * 删除设备附件
     *
     * @param id 附件ID
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDeviceFile(String id) {
        ShkbDeviceFile file = this.getById(id);
        if (file == null) {
            return false;
        }

        // 如需删除物理文件，可在此调用 UploadUtil 删除
        return this.removeById(id);
    }

    /**
     * 批量删除设备附件
     *
     * @param ids 附件ID列表
     * @return 成功删除的数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteDeviceFiles(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return 0;
        }

        int count = 0;
        for (String id : ids) {
            if (deleteDeviceFile(id)) {
                count++;
            }
        }

        return count;
    }
}

