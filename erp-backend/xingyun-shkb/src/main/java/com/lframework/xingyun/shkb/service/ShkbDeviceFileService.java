package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.ShkbDeviceFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_device_file(设备附件管理)】的数据库操作Service
* @createDate 2025-12-23 14:29:16
*/
public interface ShkbDeviceFileService extends BaseMpService<ShkbDeviceFile> {

    /**
     * 上传设备附件
     *
     * @param deviceId 设备ID
     * @param files    文件列表
     * @return 上传成功的文件ID列表
     */
    List<String> uploadDeviceFiles(String deviceId, List<MultipartFile> files);

    /**
     * 获取设备附件列表
     *
     * @param deviceId 设备ID
     * @return 附件列表
     */
    List<ShkbDeviceFile> getDeviceFiles(String deviceId);

    /**
     * 删除设备附件
     *
     * @param id 附件ID
     * @return 是否删除成功
     */
    boolean deleteDeviceFile(String id);

    /**
     * 批量删除设备附件
     *
     * @param ids 附件ID列表
     * @return 成功删除的数量
     */
    int batchDeleteDeviceFiles(List<String> ids);
}
