package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.xingyun.shkb.bo.device.GetDeviceRecordBo;
import com.lframework.xingyun.shkb.bo.device.QueryDeviceRecordBo;
import com.lframework.xingyun.shkb.entity.DeviceRecord;
import com.lframework.xingyun.shkb.service.DeviceRecordService;
import com.lframework.xingyun.shkb.vo.device.CreateDeviceRecordVo;
import com.lframework.xingyun.shkb.vo.device.QueryDeviceRecordVo;
import com.lframework.xingyun.shkb.vo.device.UpdateDeviceRecordVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备维修记录 Controller
 */
@Api(tags = "设备维修记录")
@Validated
@RestController
@RequestMapping("/shkb/device/record")
public class DeviceRecordController extends DefaultBaseController {

    @Autowired
    private DeviceRecordService deviceRecordService;

    /**
     * 查询设备维修记录列表
     */
    @ApiOperation("查询设备维修记录列表")
    @HasPermission({"equipment:device"})
    @GetMapping("/query")
    public InvokeResult<PageResult<QueryDeviceRecordBo>> query(@Valid QueryDeviceRecordVo vo) {
        PageResult<DeviceRecord> pageResult = deviceRecordService.query(getPageIndex(vo), getPageSize(vo), vo);

        List<DeviceRecord> datas = pageResult.getDatas();
        List<QueryDeviceRecordBo> results = null;

        if (!CollectionUtil.isEmpty(datas)) {
            results = datas.stream().map(QueryDeviceRecordBo::new).collect(Collectors.toList());
        }

        return InvokeResultBuilder.success(PageResultUtil.rebuild(pageResult, results));
    }

    /**
     * 查询设备维修记录详情
     */
    @ApiOperation("查询设备维修记录详情")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "path", required = true)
    @HasPermission({"equipment:device"})
    @GetMapping("/{id}")
    public InvokeResult<GetDeviceRecordBo> get(@NotBlank(message = "ID不能为空") @PathVariable String id) {
        DeviceRecord data = deviceRecordService.findById(id);
        if (data == null) {
            throw new DefaultClientException("设备维修记录不存在");
        }

        return InvokeResultBuilder.success(new GetDeviceRecordBo(data));
    }

    /**
     * 创建设备维修记录
     */
    @ApiOperation("创建设备维修记录")
    @HasPermission({"equipment:device"})
    @PostMapping
    public InvokeResult<String> create(@Valid @RequestBody CreateDeviceRecordVo vo) {
        String id = deviceRecordService.create(vo);

        return InvokeResultBuilder.success(id);
    }

    /**
     * 修改设备维修记录
     */
    @ApiOperation("修改设备维修记录")
    @HasPermission({"equipment:device"})
    @PutMapping
    public InvokeResult<Void> update(@Valid @RequestBody UpdateDeviceRecordVo vo) {
        deviceRecordService.update(vo);

        return InvokeResultBuilder.success();
    }

    /**
     * 删除设备维修记录
     */
    @ApiOperation("删除设备维修记录")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "path", required = true)
    @HasPermission({"equipment:device"})
    @DeleteMapping("/{id}")
    public InvokeResult<Void> delete(@NotBlank(message = "ID不能为空") @PathVariable String id) {
        deviceRecordService.deleteById(id);

        return InvokeResultBuilder.success();
    }

    /**
     * 批量删除设备维修记录
     */
    @ApiOperation("批量删除设备维修记录")
    @HasPermission({"equipment:device"})
    @DeleteMapping("/batch")
    public InvokeResult<Void> batchDelete(
            @ApiParam(value = "ID", required = true) @NotEmpty(message = "请选择需要删除的设备维修记录") @RequestParam List<String> ids) {
        deviceRecordService.deleteByIds(ids);

        return InvokeResultBuilder.success();
    }
}
