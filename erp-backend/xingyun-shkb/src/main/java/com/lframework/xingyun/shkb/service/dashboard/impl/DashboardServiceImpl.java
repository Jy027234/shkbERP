package com.lframework.xingyun.shkb.service.dashboard.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.xingyun.basedata.entity.MachineType;
import com.lframework.xingyun.basedata.entity.PartNumber;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.mappers.ProductMapper;
import com.lframework.xingyun.basedata.service.machineType.MachineTypeService;
import com.lframework.xingyun.basedata.service.partNumber.PartNumberService;
import com.lframework.xingyun.basedata.service.product.ProductService;
import com.lframework.xingyun.sc.entity.ProductStock;
import com.lframework.xingyun.sc.service.stock.ProductStockService;
import com.lframework.xingyun.sc.vo.stock.QueryProductStockVo;
import com.lframework.xingyun.shkb.entity.Contract;
import com.lframework.xingyun.shkb.entity.ContractTask;
import com.lframework.xingyun.shkb.entity.ShkbDevice;
import com.lframework.xingyun.shkb.entity.ShkbTool;
import com.lframework.xingyun.shkb.enums.ContractStatus;
import com.lframework.xingyun.shkb.enums.ContractType;
import com.lframework.xingyun.shkb.enums.RepairStatus;
import com.lframework.xingyun.shkb.mappers.ContractMapper;
import com.lframework.xingyun.shkb.service.ShkbDeviceService;
import com.lframework.xingyun.shkb.service.ShkbToolService;
import com.lframework.xingyun.shkb.service.contract.ContractService;
import com.lframework.xingyun.shkb.service.contract.ContractTaskService;
import com.lframework.xingyun.shkb.service.dashboard.DashboardService;
import com.lframework.xingyun.shkb.vo.dashboard.QueryDashboardVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 看板服务实现类
 */
@Service
public class DashboardServiceImpl extends BaseMpServiceImpl<ContractMapper, Contract> implements DashboardService {

    @Autowired
    private ContractService contractService;

    @Autowired
    private ContractTaskService contractTaskService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductStockService productStockService;

    @Autowired
    private MachineTypeService machineTypeService;

    @Autowired
    private ShkbToolService shkbToolService;

    @Autowired
    private ShkbDeviceService shkbDeviceService;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public Map<String, Object> getMaintenanceBoard(QueryDashboardVo vo) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取日期范围，默认为最近30天
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        if (vo != null && vo.getStartDate() != null && vo.getEndDate() != null) {
            startDate = vo.getStartDate();
            endDate = vo.getEndDate();
        }
        
        // 根据查询类型返回不同的数据
        if (vo == null || vo.getQueryType() == null || vo.getQueryType() == 1) {
            // 民航维修工单（A类）数据
            result.put("aTypeData", getMaintenanceTypeData(ContractType.AVIATION.getCode(), startDate, endDate));
        }

        if (vo == null || vo.getQueryType() == null || vo.getQueryType() == 2) {
            // 返厂WB维修工单（B类）数据
            result.put("bTypeData", getMaintenanceTypeData(ContractType.RECEIVE_WB.getCode(), startDate, endDate));
        }

        if (vo == null || vo.getQueryType() == null || vo.getQueryType() == 3) {
            // 返厂L维修工单（C类）数据
            result.put("cTypeData", getMaintenanceTypeData(ContractType.RECEIVE_L.getCode(), startDate, endDate));
        }

        if (vo == null || vo.getQueryType() == null || vo.getQueryType() == 4) {
            // 设备与库存管理模块数据
            Map<String, Object> equipmentStockData = new HashMap<>();
            
            // 航空库存提醒数据
            equipmentStockData.put("inventoryData", getInventoryData());
            
            // 工具设备管理数据
            equipmentStockData.put("toolsData", getToolsDeviceData());
            
            result.put("equipmentStockData", equipmentStockData);
        }

        return result;
    }

    @Override
    public Map<String, Object> getMaintenanceTypeData(Integer contractType, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取合同任务数据（限制时间范围，避免全表扫描）
        java.time.LocalDateTime startDateTime = startDate.atStartOfDay();
        java.time.LocalDateTime endDateTime = endDate.atTime(java.time.LocalTime.MAX);
        List<ContractTask> tasks = contractTaskService.lambdaQuery()
            .between(ContractTask::getCreateTime, startDateTime, endDateTime)
            .list();
        List<ContractTask> filteredTasks = new ArrayList<>();
        
        // 获取所有机型信息，用于统计
        List<MachineType> machineTypes = machineTypeService.query().list();
        Map<String, MachineType> machineTypeMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(machineTypes)) {
            for (MachineType machineType : machineTypes) {
                machineTypeMap.put(machineType.getId(), machineType);
            }
        }
        
        // 按机型ID分组的任务列表
        Map<String, List<ContractTask>> tasksByMachineType = new HashMap<>();
        
        if (!CollectionUtils.isEmpty(tasks)) {
            for (ContractTask task : tasks) {
                Contract contract = contractService.findById(task.getContractId());
                if (contract == null) {
                    continue;
                }

                // 合同停用则不统计
                if (contract.getAvailable() == null || !contract.getAvailable()) {
                    continue;
                }
                // 根据合同类型过滤
                if (contractType != contract.getContractType()) {
                    continue;
                }
                // 时间范围已在SQL中过滤，这里无需再次判断
                filteredTasks.add(task);
                
                // 获取件号信息
                if (contract.getPartNumberId() != null) {
                    Product product = productMapper.selectById(contract.getPartNumberId());
                    if (product != null && product.getMachineTypeId() != null) {
                        String machineTypeId = product.getMachineTypeId();
                        if (!tasksByMachineType.containsKey(machineTypeId)) {
                            tasksByMachineType.put(machineTypeId, new ArrayList<>());
                        }
                        tasksByMachineType.get(machineTypeId).add(task);
                    }
                }
            }
        }
        
        // 1. 生成图表数据 - 按机型统计
        List<Map<String, Object>> chartData = new ArrayList<>();
        
        // 定义饼图颜色
        String[] colors = {"#1890FF", "#13C2C2", "#52C41A", "#FAAD14", "#F5222D", "#722ED1", "#EB2F96"};
        int colorIndex = 0;
        
        // 按机型生成饼图数据
        for (Map.Entry<String, List<ContractTask>> entry : tasksByMachineType.entrySet()) {
            String machineTypeId = entry.getKey();
            List<ContractTask> machineTypeTasks = entry.getValue();
            
            if (machineTypeMap.containsKey(machineTypeId)) {
                MachineType machineType = machineTypeMap.get(machineTypeId);
                Map<String, Object> item = new HashMap<>();
                
                // 使用机型名称作为饼图项名称
                String machineName = machineType.getName();
                if (!StringUtils.hasText(machineName)) {
                    machineName = machineType.getCode();
                }
                
                item.put("name", machineName);
                item.put("value", machineTypeTasks.size());
                item.put("color", colors[colorIndex % colors.length]);
                colorIndex++;
                
                chartData.add(item);
            }
        }
        
        // 如果没有按机型分类的数据，则按维修状态统计
        if (chartData.isEmpty() && !filteredTasks.isEmpty()) {
            // 按维修状态统计任务数量
            Map<String, Integer> repairStatusCount = new HashMap<>();
            for (ContractTask task : filteredTasks) {
                String repairStatus = task.getRepairStatus();
                repairStatusCount.put(repairStatus, repairStatusCount.getOrDefault(repairStatus, 0) + 1);
            }
            
            // 生成饼图数据
            colorIndex = 0;
            for (Map.Entry<String, Integer> entry : repairStatusCount.entrySet()) {
                Map<String, Object> item = new HashMap<>();
                String statusName = "未知状态";
                
                // 根据维修状态代码获取状态名称
                String statusCode = entry.getKey();
                if (statusCode != null) {
                    try {
                        // 尝试使用RepairStatus枚举获取状态名称
                        for (RepairStatus status : RepairStatus.values()) {
                            if (status.getCode().equals(statusCode)) {
                                statusName = status.getDesc();
                                break;
                            }
                        }
                    } catch (Exception e) {
                        // 如果无法匹配枚举，使用简单映射
                        if ("WAIT_CHECK".equals(statusCode)) {
                            statusName = "待检查";
                        } else if ("CHECKING".equals(statusCode)) {
                            statusName = "检查中";
                        } else if ("REPAIRING".equals(statusCode)) {
                            statusName = "维修中";
                        } else if ("WAITING_FOR_PARTS".equals(statusCode)) {
                            statusName = "等料暂停";
                        } else if ("PAUSED_OTHER".equals(statusCode)) {
                            statusName = "其他暂停";
                        } else if ("WAIT_ASSEMBLY".equals(statusCode)) {
                            statusName = "待装配";
                        } else if ("TESTING".equals(statusCode)) {
                            statusName = "测试中";
                        } else if ("COMPLETED".equals(statusCode)) {
                            statusName = "完工";
                        }
                    }
                }
                
                item.put("name", statusName);
                item.put("value", entry.getValue());
                item.put("color", colors[colorIndex % colors.length]);
                colorIndex++;
                
                chartData.add(item);
            }
        }
        
        result.put("chartData", chartData);
        
        // 2. 生成统计数据 - 直接按机型统计
        List<Map<String, Object>> statisticsData = new ArrayList<>();
        
        // 获取所有合同
        List<Contract> contracts = contractService.query().list();
        
        // 按机型分组的合同
        Map<String, List<Contract>> contractsByMachineType = new HashMap<>();
        
        if (!CollectionUtils.isEmpty(contracts)) {
            for (Contract contract : contracts) {
                // 根据合同类型过滤
                if (contractType != contract.getContractType()) {
                    continue;
                }
                
                // 根据日期范围过滤
                LocalDate contractCreateDate = contract.getCreateTime().toLocalDate();
                if (contractCreateDate.isBefore(startDate) || contractCreateDate.isAfter(endDate)) {
                    continue;
                }
                
                // 获取件号信息
                if (contract.getPartNumberId() != null) {
                    Product product = productMapper.selectById(contract.getPartNumberId());
                    if (product != null && product.getMachineTypeId() != null) {
                        String machineTypeId = product.getMachineTypeId();
                        if (!contractsByMachineType.containsKey(machineTypeId)) {
                            contractsByMachineType.put(machineTypeId, new ArrayList<>());
                        }
                        contractsByMachineType.get(machineTypeId).add(contract);
                    }
                }
            }
        }
        
        // 生成统计表格数据
        for (Map.Entry<String, List<Contract>> entry : contractsByMachineType.entrySet()) {
            String machineTypeId = entry.getKey();
            List<Contract> machineTypeContracts = entry.getValue();
            
            if (machineTypeMap.containsKey(machineTypeId)) {
                MachineType machineType = machineTypeMap.get(machineTypeId);
                Map<String, Object> item = new HashMap<>();
                
                // 使用机型名称
                String machineName = machineType.getName();
                if (!StringUtils.hasText(machineName)) {
                    machineName = machineType.getCode();
                }
                
                // 接收量为该机型总的合同数
                int receivedCount = machineTypeContracts.size();
                
                // 完工量改为：合同任务的维修状态为 COMPLETED 的数量
                int completedCount = 0;
                // 异常量是合同任务退修量
                int abnormalCount = 0;
                
                for (Contract contract : machineTypeContracts) {
                    ContractStatus contractStatus = contract.getContractStatus();
                    if (contractStatus != null) {
                        if (contractStatus == ContractStatus.TASK_RETURN) {
                            abnormalCount++;
                        }
                    }
                }

                // 统计该机型下任务的完工数量（维修状态 COMPLETED）
                List<ContractTask> machineTypeTasks = tasksByMachineType.get(machineTypeId);
                if (machineTypeTasks != null && !machineTypeTasks.isEmpty()) {
                    for (ContractTask t : machineTypeTasks) {
                        if ("COMPLETED".equals(t.getRepairStatus())) {
                            completedCount++;
                        }
                    }
                }
                
                item.put("aircraftType", machineName);
                item.put("received", receivedCount);
                item.put("completed", completedCount);
                item.put("abnormal", abnormalCount);
                
                statisticsData.add(item);
            }
        }
        
        result.put("statisticsData", statisticsData);
        
        // 3. 生成表格数据
        List<Map<String, Object>> tableData = new ArrayList<>();
        for (ContractTask task : filteredTasks) {
            Contract contract = contractService.findById(task.getContractId());
            if (contract == null) {
                continue;
            }
            
            Map<String, Object> item = new HashMap<>();
            item.put("key", task.getId());
            item.put("taskId", task.getId());
            item.put("contractId", contract.getId());
            item.put("contractCode", contract.getCode());
            
            // 获取件号信息
            String partNumber = "";
            String machineTypeInfo = "";
            if (contract.getPartNumberId() != null) {
                Product product = productMapper.selectById(contract.getPartNumberId());
                if (product != null) {
                    partNumber = product.getCode();
                    // 获取机型信息
                    if (product.getMachineTypeId() != null) {
                        MachineType machineType = machineTypeService.findById(product.getMachineTypeId());
                        if (machineType != null) {
                            machineTypeInfo = machineType.getName();
                        }
                    }
                }
            }
            item.put("partNumber", partNumber);
            item.put("serialNumber", contract.getSerialNumber());
            item.put("machineType", machineTypeInfo);
            
            // 获取维修状态
            String repairStatusName = "未知状态";
            String repairStatus = task.getRepairStatus();
            if (repairStatus != null) {
                try {
                    // 尝试使用RepairStatus枚举获取状态名称
                    for (RepairStatus status : RepairStatus.values()) {
                        if (status.getCode().equals(repairStatus)) {
                            repairStatusName = status.getDesc();
                            break;
                        }
                    }
                } catch (Exception e) {
                    // 如果无法匹配枚举，使用简单映射
                    if ("WAIT_CHECK".equals(repairStatus)) {
                        repairStatusName = "待检查";
                    } else if ("CHECKING".equals(repairStatus)) {
                        repairStatusName = "检查中";
                    } else if ("REPAIRING".equals(repairStatus)) {
                        repairStatusName = "维修中";
                    } else if ("WAITING_FOR_PARTS".equals(repairStatus)) {
                        repairStatusName = "等料暂停";
                    } else if ("PAUSED_OTHER".equals(repairStatus)) {
                        repairStatusName = "其他暂停";
                    } else if ("WAIT_ASSEMBLY".equals(repairStatus)) {
                        repairStatusName = "待装配";
                    } else if ("TESTING".equals(repairStatus)) {
                        repairStatusName = "测试中";
                    } else if ("COMPLETED".equals(repairStatus)) {
                        repairStatusName = "完工";
                    }
                }
            }
            item.put("statusNode", repairStatusName);
            
            // 时间信息
            item.put("createTime", task.getCreateTime());
            item.put("reason", contract.getDescription());
            // 入库时间
            item.put("storageTime", contract.getStorageTime());
            
            // 计算超时天数
            LocalDateTime storageTime = contract.getStorageTime();
            LocalDateTime plannedCompletionTime = contract.getPlannedCompletionTime();
            
            // 处理超时天数逻辑
            if (plannedCompletionTime == null || storageTime == null) {
                // 如果计划完成时间或入库时间其中一个为空，则显示"-"
                item.put("overdueDays", "-");
            } else if (storageTime.isBefore(plannedCompletionTime)) {
                // 如果入库时间 < 计划完成时间，显示"未超"
                item.put("overdueDays", "未超");
            } else {
                // 计算超出天数
                long days = ChronoUnit.DAYS.between(plannedCompletionTime, storageTime);
                item.put("overdueDays", days);
            }
            
            // 如果入库时间为空但计划完成时间不为空，且当前时间已超过计划完成时间
            if (storageTime == null && plannedCompletionTime != null && 
                LocalDateTime.now().isAfter(plannedCompletionTime)) {
                // 按当前时间计算超时天数
                long days = ChronoUnit.DAYS.between(plannedCompletionTime, LocalDateTime.now());
                item.put("overdueDays", days);
            }

            tableData.add(item);
        }
        result.put("tableData", tableData);
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getInventoryData() {
        List<Map<String, Object>> result = new ArrayList<>();
        // 通过专用查询获取库存≤10且分类为“航材”的商品库存（按商品汇总）
        List<ProductStock> stocks = productStockService.queryLowInventoryAviation();
        if (CollectionUtils.isEmpty(stocks)) {
            return result;
        }

        for (ProductStock stock : stocks) {
            // 根据商品ID获取商品信息（主要是件号）
            Product product = productService.findById(stock.getProductId());
            if (product == null) {
                continue;
            }

            int inventory = stock.getStockNum();

            // 根据库存数量判断紧急程度
            String urgency = "低";
            if (inventory <= 5) {
                urgency = "高";
            } else if (inventory <= 10) {
                urgency = "中";
            }

            Map<String, Object> item = new HashMap<>();
            item.put("key", product.getId());
            item.put("partNumber", product.getCode());
            item.put("inventory", inventory);
            item.put("urgency", urgency);

            result.add(item);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getToolsDeviceData() {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        // 获取工具数据：查询“已过期”与“未来30天内到期”的记录，按 expirationTime 进行筛选
        List<ShkbTool> tools = shkbToolService.lambdaQuery()
                .isNotNull(ShkbTool::getExpirationTime)
                // 包含所有已过期（expirationTime < today）以及未来30天内到期（< today+30）的记录
                .lt(ShkbTool::getExpirationTime, today.plusDays(30))
                // 启用状态
                .eq(ShkbTool::getAvailable, true)
                .orderByAsc(ShkbTool::getExpirationTime)
                .list();
        if (!CollectionUtils.isEmpty(tools)) {
            for (ShkbTool tool : tools) {
                // 计算可用天数
                if (tool.getExpirationTime() == null) {
                    // 无下次维护时间，无法计算可用天数，忽略
                    continue;
                }

                long availableDays = ChronoUnit.DAYS.between(today, tool.getExpirationTime());

                // 仅保留可用天数<=30天（包含已过期的负数）
                if (availableDays <= 30) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("key", tool.getId());
                    item.put("area", tool.getManagementArea());
                    item.put("equipmentCode", tool.getCode());
                    item.put("availableDays", availableDays);

                    result.add(item);
                }
            }
        }
        
        // 获取设备数据（仅查询未来30天内到期的记录）
        List<ShkbDevice> devices = shkbDeviceService.lambdaQuery()
                .isNotNull(ShkbDevice::getNextMaintenanceTime)
                .lt(ShkbDevice::getNextMaintenanceTime, today.plusDays(30))
                .eq(ShkbDevice::getAvailable, true)
                .list();
        if (!CollectionUtils.isEmpty(devices)) {
            for (ShkbDevice device : devices) {
                // 计算可用天数
                if (device.getNextMaintenanceTime() == null) {
                    // 无下次维护时间，无法计算可用天数，忽略
                    continue;
                }

                long availableDays = ChronoUnit.DAYS.between(today, device.getNextMaintenanceTime());

                // 仅保留可用天数<=30天（包含已过期的负数）
                if (availableDays <= 30) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("key", device.getId());
                    item.put("area", device.getManagementArea());
                    item.put("equipmentCode", device.getCode());
                    item.put("availableDays", availableDays);

                    result.add(item);
                }
            }
        }
        
        return result;
    }
}
