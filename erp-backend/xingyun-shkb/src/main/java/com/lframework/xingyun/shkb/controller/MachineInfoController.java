package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.shkb.bo.machineinfo.QueryMachineInfoBo;
import com.lframework.xingyun.shkb.entity.MachineInfo;
import com.lframework.xingyun.shkb.service.MachineInfoService;
import com.lframework.xingyun.shkb.vo.machineinfo.QueryMachineInfoVo;
import com.lframework.xingyun.shkb.vo.machineinfo.UpdateMachineInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自动化设备 管理
 */
@Api(tags = "自动化设备")
@Validated
@RestController
@RequestMapping("/machine/info")
public class MachineInfoController extends DefaultBaseController {

    @Autowired
    private MachineInfoService machineInfoService;

    /**
     * 获取设备列表（分页）
     */
    @ApiOperation("获取设备列表")
    @HasPermission({"machine-task:machine-info"})
    @GetMapping("/query")
    public InvokeResult<PageResult<QueryMachineInfoBo>> query(@Valid QueryMachineInfoVo vo) {
        PageResult<MachineInfo> pageResult = machineInfoService.query(getPageIndex(vo), getPageSize(vo), vo);
        List<MachineInfo> datas = pageResult.getDatas();
        List<QueryMachineInfoBo> results = null;
        if (!CollectionUtil.isEmpty(datas)) {
            results = datas.stream().map(QueryMachineInfoBo::new).collect(Collectors.toList());
        }
        return InvokeResultBuilder.success(PageResultUtil.rebuild(pageResult, results));
    }

    /**
     * 修改设备信息（仅允许修改名称和IP）
     */
    @ApiOperation("修改设备信息")
    @HasPermission({"machine-task:machine-info"})
    @PostMapping("/update")
    public InvokeResult<Void> update(@Valid @RequestBody UpdateMachineInfoVo vo) {
        machineInfoService.updateNameAndIp(vo.getId(), vo.getMachineName(), vo.getIpAddress());
        return InvokeResultBuilder.success();
    }
}
