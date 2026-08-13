package com.lframework.xingyun.shkb.service.dashboard;

import com.lframework.xingyun.shkb.vo.dashboard.QueryDashboardVo;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 看板服务接口
 */
public interface DashboardService {

    /**
     * 获取维修看板数据
     * 
     * @param vo 查询参数
     * @return 看板数据
     */
    Map<String, Object> getMaintenanceBoard(QueryDashboardVo vo);

    /**
     * 获取指定合同类型的维修工单数据
     * 
     * @param contractType 合同类型代码
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 包含图表数据、统计数据和表格数据的Map
     */
    Map<String, Object> getMaintenanceTypeData(Integer contractType, LocalDate startDate, LocalDate endDate);

    /**
     * 获取库存数据
     * 
     * @return 库存数据列表
     */
    List<Map<String, Object>> getInventoryData();

    /**
     * 获取工具设备数据
     * 
     * @return 工具设备数据列表
     */
    List<Map<String, Object>> getToolsDeviceData();
}
