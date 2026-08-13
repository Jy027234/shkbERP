package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.xingyun.shkb.service.dashboard.DashboardService;
import com.lframework.xingyun.shkb.vo.dashboard.QueryDashboardVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 看板控制器
 */
@Api(tags = {"看板管理"})
@Slf4j
@Validated
@RestController
@RequestMapping("/shkb/dashboard")
public class DashboardController extends DefaultBaseController {
    
    @Autowired
    private DashboardService dashboardService;
    
    /**
     * 获取维修看板数据
     */
    @ApiOperation("获取维修看板数据")
    @GetMapping("/maintenance-board")
    public InvokeResult<Map<String, Object>> getMaintenanceBoardApi(QueryDashboardVo vo) {
        Map<String, Object> result = dashboardService.getMaintenanceBoard(vo);
        return InvokeResultBuilder.success(result);
    }
    
    /**
     * 获取维修类型数据
     */
    @ApiOperation("获取维修类型数据")
    @GetMapping("/maintenance-type-data")
    public InvokeResult<Map<String, Object>> getMaintenanceTypeDataApi(
            @ApiParam(value = "合同类型", required = true) @RequestParam("contractType") Integer contractType,
            @ApiParam(value = "开始日期", required = false) @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam(value = "结束日期", required = false) @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        // 默认最近30天
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (startDate == null) {
            startDate = endDate.minusDays(29); // 共30天区间
        }

        Map<String, Object> result = dashboardService.getMaintenanceTypeData(contractType, startDate, endDate);
        return InvokeResultBuilder.success(result);
    }
    
    /**
     * 获取库存数据
     */
    @ApiOperation("获取库存数据")
    @GetMapping("/inventory-data")
    public InvokeResult<List<Map<String, Object>>> getInventoryDataApi() {
        List<Map<String, Object>> result = dashboardService.getInventoryData();
        return InvokeResultBuilder.success(result);
    }
    
    /**
     * 获取工具设备数据
     */
    @ApiOperation("获取工具设备数据")
    @GetMapping("/tools-device-data")
    public InvokeResult<List<Map<String, Object>>> getToolsDeviceDataApi() {
        List<Map<String, Object>> result = dashboardService.getToolsDeviceData();
        return InvokeResultBuilder.success(result);
    }
}
