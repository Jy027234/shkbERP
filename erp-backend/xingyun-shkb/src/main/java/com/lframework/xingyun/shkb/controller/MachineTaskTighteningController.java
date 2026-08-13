package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.shkb.bo.machinetask.QueryMachineTaskTighteningBo;
import com.lframework.xingyun.shkb.bo.machinetask.GetMachineTaskTighteningBo;
import com.lframework.xingyun.shkb.entity.MachineTaskTightening;
import com.lframework.xingyun.shkb.service.MachineTaskTighteningService;
import com.lframework.xingyun.shkb.service.MachineInfoService;
import com.lframework.xingyun.shkb.vo.machinetask.QueryMachineTaskTighteningVo;
import com.lframework.xingyun.shkb.vo.machinetask.ReportMachineTaskTighteningVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lframework.starter.web.core.annotations.openapi.OpenApi;
import com.lframework.xingyun.template.inner.simpleapi.annotation.SimpleOpenApi;
import org.springframework.web.bind.annotation.RequestParam;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;

import jakarta.validation.Valid;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 拧紧机设备任务 管理
 */
@Api(tags = "拧紧机设备任务")
@Validated
@RestController
@RequestMapping("/machine/task/tightening")
public class MachineTaskTighteningController extends DefaultBaseController {

    @Autowired
    private MachineTaskTighteningService machineTaskTighteningService;

    @Autowired
    private MachineInfoService machineInfoService;

    /**
     * 获取拧紧机设备任务列表（分页）
     */
    @ApiOperation("获取拧紧机设备任务列表")
    @HasPermission({"machine-task:tightening"})
    @GetMapping("/query")
    public InvokeResult<PageResult<QueryMachineTaskTighteningBo>> query(@Valid QueryMachineTaskTighteningVo vo) {
        PageResult<MachineTaskTightening> pageResult = machineTaskTighteningService.query(getPageIndex(vo), getPageSize(vo), vo);

        List<MachineTaskTightening> datas = pageResult.getDatas();
        List<QueryMachineTaskTighteningBo> results = null;
        if (!CollectionUtil.isEmpty(datas)) {
            results = datas.stream().map(QueryMachineTaskTighteningBo::new).collect(Collectors.toList());
        }
        return InvokeResultBuilder.success(PageResultUtil.rebuild(pageResult, results));
    }

    /**
     * 设备任务上报：根据 taskId 将任务置为已完成并写入上报数据
     */
    @ApiOperation("设备任务上报")
    @OpenApi
    @SimpleOpenApi
    @PostMapping("/report")
    public InvokeResult<Void> report(@Valid @RequestBody ReportMachineTaskTighteningVo vo) {
        machineTaskTighteningService.report(vo);
        return InvokeResultBuilder.success();
    }

    /**
     * 根据任务ID获取设备任务详情
     */
    @ApiOperation("根据任务ID获取设备任务详情")
    @GetMapping("/detail")
    public InvokeResult<GetMachineTaskTighteningBo> detail(@RequestParam("id") String id) {
        if (StringUtil.isBlank(id)) {
            throw new DefaultClientException("ID不能为空！");
        }
        GetMachineTaskTighteningBo bo = machineTaskTighteningService.getDetailById(id);
        return InvokeResultBuilder.success(bo);
    }

    /**
     * 获取待装配任务（开放接口，不分页）
     */
    @ApiOperation("获取待装配任务（开放接口）")
    @OpenApi
    @SimpleOpenApi
    @GetMapping("/pending")
    public InvokeResult<List<QueryMachineTaskTighteningBo>> pending() {
        List<MachineTaskTightening> datas = machineTaskTighteningService.queryPending();
        List<QueryMachineTaskTighteningBo> results = new ArrayList<>();
        if (!CollectionUtil.isEmpty(datas)) {
            results = datas.stream().map(QueryMachineTaskTighteningBo::new).collect(Collectors.toList());
        }
        return InvokeResultBuilder.success(results);
    }

    /**
     * 拧紧机心跳上报
     */
    @ApiOperation("拧紧机心跳")
    @OpenApi
    @SimpleOpenApi
    @GetMapping("/ping")
    public InvokeResult<String> simplePing() {
        machineInfoService.updateVisitTimeForTightening();
        return InvokeResultBuilder.success("pong");
    }
}
