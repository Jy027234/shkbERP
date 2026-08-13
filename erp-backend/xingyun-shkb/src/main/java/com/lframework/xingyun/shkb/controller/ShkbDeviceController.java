package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.xingyun.shkb.bo.device.GetShkbDeviceBo;
import com.lframework.xingyun.shkb.bo.device.QueryShkbDeviceBo;
import com.lframework.xingyun.shkb.entity.ShkbDevice;
import com.lframework.xingyun.shkb.entity.ShkbDeviceFile;
import com.lframework.xingyun.shkb.service.ShkbDeviceService;
import com.lframework.xingyun.shkb.service.ShkbDeviceFileService;
import com.lframework.xingyun.shkb.vo.device.CreateShkbDeviceVo;
import com.lframework.xingyun.shkb.vo.device.QueryShkbDeviceVo;
import com.lframework.xingyun.shkb.vo.device.UpdateShkbDeviceVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备管理 Controller
 */
@Api(tags = "设备管理")
@Validated
@RestController
@RequestMapping("/shkb/device")
public class ShkbDeviceController extends DefaultBaseController {

    @Autowired
    private ShkbDeviceService deviceService;

    @Autowired
    private ShkbDeviceFileService deviceFileService;

    /**
     * 查询设备列表
     */
    @ApiOperation("查询设备列表")
    @HasPermission({"equipment:device"})
    @GetMapping("/query")
    public InvokeResult<PageResult<QueryShkbDeviceBo>> query(@Valid QueryShkbDeviceVo vo) {
        PageResult<ShkbDevice> pageResult = deviceService.query(getPageIndex(vo), getPageSize(vo), vo);

        List<ShkbDevice> datas = pageResult.getDatas();
        List<QueryShkbDeviceBo> results = null;

        if (!CollectionUtil.isEmpty(datas)) {
            results = datas.stream().map(QueryShkbDeviceBo::new).collect(Collectors.toList());
        }

        return InvokeResultBuilder.success(PageResultUtil.rebuild(pageResult, results));
    }

    /**
     * 查询设备详情
     */
    @ApiOperation("查询设备详情")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "path", required = true)
    @HasPermission({"equipment:device"})
    @GetMapping("/{id}")
    public InvokeResult<GetShkbDeviceBo> get(@NotBlank(message = "ID不能为空") @PathVariable String id) {
        ShkbDevice data = deviceService.findById(id);
        if (data == null) {
            throw new DefaultClientException("设备不存在");
        }

        return InvokeResultBuilder.success(new GetShkbDeviceBo(data));
    }

    /**
     * 创建设备
     */
    @ApiOperation("创建设备")
    @HasPermission({"equipment:device"})
    @PostMapping
    public InvokeResult<Void> create(@Valid @RequestBody CreateShkbDeviceVo vo) {
        deviceService.create(vo);

        return InvokeResultBuilder.success();
    }

    /**
     * 修改设备
     */
    @ApiOperation("修改设备")
    @HasPermission({"equipment:device"})
    @PutMapping
    public InvokeResult<Void> update(@Valid @RequestBody UpdateShkbDeviceVo vo) {
        deviceService.update(vo);

        return InvokeResultBuilder.success();
    }

    /**
     * 删除设备
     */
    @ApiOperation("删除设备")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "path", required = true)
    @HasPermission({"equipment:device"})
    @DeleteMapping("/{id}")
    public InvokeResult<Void> delete(@NotBlank(message = "ID不能为空") @PathVariable String id) {
        deviceService.deleteById(id);

        return InvokeResultBuilder.success();
    }

    /**
     * 批量删除设备
     */
    @ApiOperation("批量删除设备")
    @HasPermission({"equipment:device"})
    @DeleteMapping("/batch")
    public InvokeResult<Void> batchDelete(
            @ApiParam(value = "ID", required = true) @NotEmpty(message = "请选择需要删除的设备") @RequestParam List<String> ids) {
        deviceService.deleteByIds(ids);

        return InvokeResultBuilder.success();
    }

    /**
     * 上传设备附件
     */
    @ApiOperation("上传设备附件")
    @ApiImplicitParam(value = "设备ID", name = "deviceId", paramType = "query", required = true)
    @HasPermission({"equipment:device"})
    @PostMapping("/attachment/upload")
    public InvokeResult<List<String>> uploadDeviceAttachments(
            @NotBlank(message = "设备ID不能为空！") @RequestParam("deviceId") String deviceId,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        List<String> fileIds = deviceFileService.uploadDeviceFiles(deviceId, files);

        return InvokeResultBuilder.success(fileIds);
    }

    /**
     * 获取设备附件列表
     */
    @ApiOperation("获取设备附件列表")
    @ApiImplicitParam(value = "设备ID", name = "deviceId", paramType = "query", required = true)
    @HasPermission({"equipment:device"})
    @GetMapping("/attachment/list")
    public InvokeResult<List<ShkbDeviceFile>> getDeviceAttachments(
            @NotBlank(message = "设备ID不能为空！") @RequestParam("deviceId") String deviceId) {

        List<ShkbDeviceFile> files = deviceFileService.getDeviceFiles(deviceId);

        return InvokeResultBuilder.success(files);
    }

    /**
     * 删除设备附件
     */
    @ApiOperation("删除设备附件")
    @ApiImplicitParam(value = "附件ID", name = "id", paramType = "path", required = true)
    @HasPermission({"equipment:device"})
    @DeleteMapping("/attachment/{id}")
    public InvokeResult<Void> deleteDeviceAttachment(
            @NotBlank(message = "设备附件id不能为空！")
            @PathVariable("id") String id) {

        boolean success = deviceFileService.deleteDeviceFile(id);
        if (!success) {
            throw new DefaultClientException("附件不存在或删除失败！");
        }

        return InvokeResultBuilder.success();
    }
}
