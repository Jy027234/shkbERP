package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.MachineTaskMagneticPowder;
import com.lframework.xingyun.shkb.dto.RemoteFolderDto;
import com.lframework.xingyun.shkb.dto.RemoteFileDto;

import java.util.List;
import org.springframework.http.ResponseEntity;

/**
* @author kison
* @description 针对表【shkb_machine_task_magnetic_powder(磁粉机任务表)】的数据库操作Service
* @createDate 2025-11-04 08:38:31
*/
public interface MachineTaskMagneticPowderService extends BaseMpService<MachineTaskMagneticPowder> {

    PageResult<MachineTaskMagneticPowder> query(Integer pageIndex, Integer pageSize,
                                                String taskId, String contractNo, String partNo, String serialNo,
                                                Integer machineTaskStatus);

    void send(String taskId);

    /**
     * 获取远程磁粉机设备的文件夹列表
     */
    List<RemoteFolderDto> getRemoteFolders();

    /**
     * 获取远程磁粉机设备指定文件夹下的文件列表
     */
    List<RemoteFileDto> getRemoteFiles(String folder);

    /**
     * 代理获取远程设备图片字节流
     */
    ResponseEntity<byte[]> getRemoteImage(String path, Boolean overlay, Boolean thumb);
}
