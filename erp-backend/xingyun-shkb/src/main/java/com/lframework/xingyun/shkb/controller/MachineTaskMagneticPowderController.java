package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.xingyun.shkb.entity.MachineTaskMagneticPowder;
import com.lframework.xingyun.shkb.service.MachineTaskMagneticPowderService;
import com.lframework.xingyun.shkb.dto.RemoteFolderDto;
import com.lframework.xingyun.shkb.dto.RemoteFileDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Api(tags = "磁粉机任务")
@Validated
@RestController
@RequestMapping("/machine/task/magnetic")
public class MachineTaskMagneticPowderController extends DefaultBaseController {

    @Autowired
    private MachineTaskMagneticPowderService machineTaskMagneticPowderService;

    @ApiOperation("获取磁粉机任务列表（分页）")
    @HasPermission({"machine-task:magnetic"})
    @GetMapping("/query")
    public InvokeResult<PageResult<MachineTaskMagneticPowder>> query(
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(value = "taskId", required = false) String taskId,
            @RequestParam(value = "contractNo", required = false) String contractNo,
            @RequestParam(value = "partNo", required = false) String partNo,
            @RequestParam(value = "serialNo", required = false) String serialNo,
            @RequestParam(value = "machineTaskStatus", required = false) Integer machineTaskStatus
    ) {
        PageResult<MachineTaskMagneticPowder> result = machineTaskMagneticPowderService.query(pageIndex, pageSize,
                taskId, contractNo, partNo, serialNo, machineTaskStatus);
        return InvokeResultBuilder.success(result);
    }

    @ApiOperation("执行下发任务")
    @HasPermission({"machine-task:magnetic"})
    @PostMapping("/send")
    public InvokeResult<Void> send(@RequestParam("taskId") String taskId) {
        machineTaskMagneticPowderService.send(taskId);
        return InvokeResultBuilder.success();
    }

    @ApiOperation("获取远程设备文件夹列表")
    @HasPermission({"machine-task:magnetic"})
    @GetMapping("/folders")
    public InvokeResult<List<RemoteFolderDto>> folders() {
        List<RemoteFolderDto> list = machineTaskMagneticPowderService.getRemoteFolders();
        return InvokeResultBuilder.success(list);
    }

    @ApiOperation("获取远程设备指定文件夹的文件列表")
    @HasPermission({"machine-task:magnetic"})
    @GetMapping("/files")
    public InvokeResult<List<RemoteFileDto>> files(@RequestParam("folder") String folder) {
        List<RemoteFileDto> list = machineTaskMagneticPowderService.getRemoteFiles(folder);
        return InvokeResultBuilder.success(list);
    }

    @ApiOperation("获取远程设备图片文件")
    @HasPermission({"machine-task:magnetic"})
    @GetMapping("/image")
    public ResponseEntity<byte[]> image(@RequestParam("path") String path,
                                        @RequestParam(value = "overlay", required = false) Boolean overlay,
                                        @RequestParam(value = "thumb", required = false) Boolean thumb) {
        return machineTaskMagneticPowderService.getRemoteImage(path, overlay, thumb);
    }
}
