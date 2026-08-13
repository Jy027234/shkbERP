package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.ApplicationUtil;
import com.lframework.starter.web.core.utils.ExcelUtil;
import com.lframework.starter.web.core.components.excel.ExcelMultipartWriterSheetBuilder;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.lframework.xingyun.shkb.bo.contract.task.*;
import com.lframework.xingyun.shkb.entity.ContractTaskWorkCard;
import com.lframework.xingyun.shkb.service.contract.*;
import com.lframework.xingyun.shkb.service.workcard.WorkCardProductService;
import com.lframework.xingyun.shkb.vo.contract.task.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import com.lframework.xingyun.shkb.entity.ContractTaskApprovalFile;
import com.lframework.xingyun.shkb.entity.ContractTask;
import com.lframework.xingyun.shkb.entity.Contract;
import com.lframework.xingyun.shkb.excel.contract.task.ContractTaskReplacementPartExportModel;

/**
 * 合同任务管理
 *
 * @author kison
 */
@Api(tags = "合同任务管理")
@Validated
@RestController
@RequestMapping("/shkb/contract-task")
public class ContractTaskController extends DefaultBaseController {

    @Autowired
    private ContractTaskService contractTaskService;

    @Autowired
    private ContractTaskWorkCardService contractTaskWorkCardService;

    @Autowired
    private ContractTaskWorkCardProductService contractTaskWorkCardProductService;

    @Autowired
    private ContractTaskNonPartProductService contractTaskNonPartProductService;

    @Autowired
    private WorkCardProductService workCardProductService;
    
    @Autowired
    private ContractTaskRepairStatusRecordService contractTaskRepairStatusRecordService;
    
    @Autowired
    private ContractTaskMaterialApplyService contractTaskMaterialApplyService;

    @Autowired
    private ContractTaskApprovalFileService contractTaskApprovalFileService;

    /**
     * 合同任务列表
     */
    @ApiOperation("合同任务列表")
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @GetMapping("/query")
    public InvokeResult<PageResult<QueryContractTaskBo>> query(@Valid QueryContractTaskVo vo) {

        PageResult<QueryContractTaskBo> pageResult = contractTaskService.query(getPageIndex(vo), getPageSize(vo), vo);

        return InvokeResultBuilder.success(pageResult);
    }

    /**
     * 合同任务详情
     */
    @ApiOperation("合同任务详情")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @GetMapping
    public InvokeResult<GetContractTaskBo> get(@NotBlank(message = "ID不能为空！") String id) {

        GetContractTaskBo result = contractTaskService.getDetail(id);
        if (result == null) {
            throw new DefaultClientException("合同任务不存在！");
        }

        return InvokeResultBuilder.success(result);
    }

    /**
     * 修改合同任务。
     */
    @ApiOperation("修改合同任务")
    @HasPermission({"maintenance:contract-task:modify"})
    @PutMapping
    public InvokeResult<Void> update(@Valid @RequestBody UpdateContractTaskVo vo) {
        contractTaskService.update(vo);
        return InvokeResultBuilder.success();
    }

    /**
     * 线下鉴定
     */
    @ApiOperation("线下鉴定")
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @PostMapping("/offline-appraisal")
    public InvokeResult<Void> offlineAppraisal(@Valid @RequestBody OfflineAppraisalVo vo) {
        contractTaskService.offlineAppraisal(vo);

        return InvokeResultBuilder.success();
    }

    /**
     * 任务派发
     */
    @ApiOperation("任务派发")
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @PostMapping("/dispatch")
    public InvokeResult<String> dispatchTask(@Valid @RequestBody DispatchTaskVo vo) {
        String newContractId = contractTaskService.dispatchTask(vo);

        return InvokeResultBuilder.success(newContractId);
    }

    /**
     * 查询任务工卡列表
     */
    @ApiOperation("查询任务工卡列表")
    @ApiImplicitParam(value = "任务ID", name = "taskId", paramType = "query", required = true)
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @GetMapping("/work-cards")
    public InvokeResult<List<ContractTaskWorkCardBo>> getWorkCards(@NotBlank(message = "任务ID不能为空！") String taskId) {
        // 查询任务工卡列表
        List<ContractTaskWorkCard> results = contractTaskWorkCardService.getByTaskId(taskId);

        // 转换为Bo对象
        List<ContractTaskWorkCardBo> bos = results.stream()
                .map(ContractTaskWorkCardBo::new)
                .collect(Collectors.toList());

        return InvokeResultBuilder.success(bos);
    }

    /**
     * 批量添加任务工卡
     */
    @ApiOperation("批量添加任务工卡")
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @PostMapping("/work-card/add")
    public InvokeResult<Void> batchAddWorkCards(@Valid @RequestBody ContractTaskWorkCardVo vo) {
        contractTaskWorkCardService.batchAdd(vo);

        return InvokeResultBuilder.success();
    }

    /**
     * 批量删除任务工卡
     */
    @ApiOperation("批量删除任务工卡")
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @PostMapping("/work-card/delete")
    public InvokeResult<Void> batchDeleteWorkCards(@Valid @RequestBody ContractTaskWorkCardVo vo) {
        contractTaskWorkCardService.batchDelete(vo);

        return InvokeResultBuilder.success();
    }

    /**
     * 获取任务必换件列表
     */
    @ApiOperation("获取任务必换件列表")
    @ApiImplicitParam(value = "任务ID", name = "taskId", paramType = "query", required = true)
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @GetMapping("/replacement-parts")
    public InvokeResult<List<ContractTaskProductBo>> getTaskReplacementParts(@NotBlank(message = "任务ID不能为空！") String taskId) {
        // 优先查询任务自身记录的必换件
        List<ContractTaskProductBo> taskSpecificParts = contractTaskWorkCardProductService.getTaskSpecificReplacementParts(taskId);

        // 如果任务自身有记录的必换件，则返回这些必换件
        if (!CollectionUtil.isEmpty(taskSpecificParts)) {
            return InvokeResultBuilder.success(taskSpecificParts);
        }

        // 如果任务自身没有记录必换件，则查询任务关联的工卡必换件
        List<ContractTaskProductBo> workCardParts = workCardProductService.getTaskReplacementParts(taskId);
        return InvokeResultBuilder.success(workCardParts);
    }

    /**
     * 导出任务必换件清单
     */
    @ApiOperation("导出任务必换件清单")
    @ApiImplicitParam(value = "任务ID", name = "taskId", paramType = "query", required = true)
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @PostMapping("/replacement-parts/export")
    public void exportTaskReplacementParts(@NotBlank(message = "任务ID不能为空！") @RequestParam("taskId") String taskId,
                                           HttpServletResponse response) {
        // 1. 查询任务与合同基础信息
        ContractTask task = contractTaskService.getById(taskId);
        if (task == null) {
            throw new DefaultClientException("合同任务不存在！");
        }

        // 使用局部final变量，避免在lambda中使用时的effectively final限制
        final Contract contract = (task.getContractId() != null && !task.getContractId().trim().isEmpty())
                ? ApplicationUtil.getBean(com.lframework.xingyun.shkb.service.contract.ContractService.class)
                    .getById(task.getContractId())
                : null;

        // 2. 复用获取任务必换件列表的业务逻辑
        List<ContractTaskProductBo> taskSpecificParts = contractTaskWorkCardProductService
                .getTaskSpecificReplacementParts(taskId);

        List<ContractTaskProductBo> parts;
        if (!CollectionUtil.isEmpty(taskSpecificParts)) {
            parts = taskSpecificParts;
        } else {
            parts = workCardProductService.getTaskReplacementParts(taskId);
        }

        // 3. 使用模板导出：头部使用占位符填充，明细使用列表填充
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");

        try {
            String fileName = java.net.URLEncoder.encode("必换件清单", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

            // 3.1 头部占位符数据
            Map<String, Object> header = new HashMap<>();
            String contractCode = contract != null ? contract.getCode() : task.getContractCode();
            header.put("contractCode", contractCode);
            header.put("replacementPartCode", contractCode != null ? contractCode + "BHJ" : "");

            // 机型：优先从明细中取，其次任务
            String headerMachineTypeName = null;
            if (!CollectionUtil.isEmpty(parts)) {
                ContractTaskProductBo first = parts.get(0);
                if (first.getMachineTypeName() != null && !first.getMachineTypeName().isEmpty()) {
                    headerMachineTypeName = first.getMachineTypeName();
                } else if (first.getProductMachineTypeName() != null && !first.getProductMachineTypeName().isEmpty()) {
                    headerMachineTypeName = first.getProductMachineTypeName();
                }
            }
            if (headerMachineTypeName == null) {
                headerMachineTypeName = task.getMachineTypeName();
            }
            header.put("machineTypeName", headerMachineTypeName != null ? headerMachineTypeName : "");

            String serialNumber = contract != null ? contract.getSerialNumber() : task.getSerialNumber();
            header.put("serialNumber", serialNumber != null ? serialNumber : "");

            // 3.2 明细数据
            java.util.List<ContractTaskReplacementPartExportModel> models = new java.util.ArrayList<>();
            int index = 1;
            if (!CollectionUtil.isEmpty(parts)) {
                for (ContractTaskProductBo p : parts) {
                    models.add(new ContractTaskReplacementPartExportModel(task, contract, p, index++));
                }
            }

            // 3.3 使用模板填充
            java.io.InputStream templateStream = this.getClass()
                    .getResourceAsStream("/excel/contract/contract_task_replacement_part_simple_template.xlsx");

            if (templateStream == null) {
                throw new DefaultClientException("导出必换件清单失败：模板文件不存在，请检查路径 /excel/contract/contract_task_replacement_part_template.xlsx");
            }

            ExcelWriter writer = null;
            try {
                writer = EasyExcel.write(response.getOutputStream())
                        .withTemplate(templateStream)
                        .autoCloseStream(Boolean.FALSE)
                        .build();

                WriteSheet sheet = EasyExcel.writerSheet(0).build();

                // ===== 调试信息：打印头部与明细基础数据 =====
                System.out.println("[exportTaskReplacementParts] header = " + header);
                System.out.println("[exportTaskReplacementParts] models size = " + models.size());
                if (!models.isEmpty()) {
                    System.out.println("[exportTaskReplacementParts] first model = " + models.get(0));
                }

                // 先单独填充头部，便于区分异常来源
                try {
                    writer.fill(header, sheet);
                    System.out.println("[exportTaskReplacementParts] header fill success");
                } catch (Exception headEx) {
                    System.out.println("[exportTaskReplacementParts] header fill error: " + headEx.getClass().getName() + ", msg=" + headEx.getMessage());
                    headEx.printStackTrace();
                    throw headEx;
                }

                // 再填充明细，forceNewRow=true 表示按占位符行向下追加数据
                if (!models.isEmpty()) {
                    try {
                        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
                        writer.fill(models, fillConfig, sheet);
                        System.out.println("[exportTaskReplacementParts] detail fill success");
                    } catch (Exception detailEx) {
                        System.out.println("[exportTaskReplacementParts] detail fill error: " + detailEx.getClass().getName() + ", msg=" + detailEx.getMessage());
                        detailEx.printStackTrace();
                        throw detailEx;
                    }
                } else {
                    System.out.println("[exportTaskReplacementParts] models is empty, skip detail fill");
                }
            } finally {
                if (writer != null) {
                    writer.finish();
                }
                if (templateStream != null) {
                    templateStream.close();
                }
            }
        } catch (Exception e) {
            // 打印完整堆栈到日志，便于排查真实原因
            e.printStackTrace();
            String msg = e.getMessage();
            if (msg == null || msg.isEmpty()) {
                // 如果没有message，就带上异常类名
                msg = e.getClass().getName();
            }
            throw new DefaultClientException("导出必换件清单失败！" + msg);
        }
    }

    /**
     * 保存任务必换件数量
     */
    @ApiOperation("保存任务必换件数量")
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @PostMapping("/replacement-parts/save")
    public InvokeResult<Void> saveTaskReplacementPartsQuantity(@Valid @RequestBody ContractTaskWorkCardProductVo vo) {
        contractTaskWorkCardProductService.saveTaskReplacementPartsQuantity(vo);
        return InvokeResultBuilder.success();
    }

    /**
     * 保存任务非必换件记录
     */
    @ApiOperation(value = "保存任务非必换件记录")
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @PostMapping("/non-part/save")
    public InvokeResult<String> saveTaskNonPartProduct(
            @NotBlank(message = "任务ID不能为空！") @RequestParam("taskId") String taskId,
            @NotBlank(message = "商品ID不能为空！") @RequestParam("productId") String productId,
            @NotNull(message = "数量不能为空！") @Positive(message = "数量必须大于0！")
            @RequestParam("quantity") Integer quantity,
            @RequestParam(value = "reason", required = false) String reason,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        // 直接调用Service层方法处理文件上传和保存非必换件记录
        String id = contractTaskNonPartProductService.saveNonPartProductWithFiles(taskId, productId, quantity, reason, files);

        return InvokeResultBuilder.success(id);
    }

    /**
     * 获取任务非必换件记录列表
     */
    @ApiOperation(value = "获取任务非必换件记录列表")
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @GetMapping("/non-part/list")
    public InvokeResult<List<ContractTaskNonPartProductBo>> getTaskNonPartProducts(
            @NotBlank(message = "任务ID不能为空！") String taskId) {

        List<ContractTaskNonPartProductBo> results = contractTaskNonPartProductService.getTaskNonPartProducts(taskId);

        return InvokeResultBuilder.success(results);
    }

    /**
     * 删除任务非必换件记录
     */
    @ApiOperation(value = "删除任务非必换件记录")
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @DeleteMapping("/non-part/delete/{id}")
    public InvokeResult<Void> deleteTaskNonPartProduct(
            @ApiParam(value = "非必换件记录ID", required = true)
            @NotBlank(message = "非必换件记录ID不能为空！")
            @PathVariable("id") String id) {

        contractTaskNonPartProductService.deleteNonPartProduct(id);

        return InvokeResultBuilder.success();
    }

    /**
     * 修改任务非必换件数量
     */
    @ApiOperation(value = "修改任务非必换件数量")
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @PutMapping("/non-part/update/quantity")
    public InvokeResult<Void> updateTaskNonPartProductQuantity(
            @Valid @RequestBody UpdateContractTaskNonPartProductVo vo) {

        contractTaskNonPartProductService.updateNonPartProductQuantity(vo);

        return InvokeResultBuilder.success();
    }
    
    /**
     * 批量修改任务非必换件数量
     */
    @ApiOperation(value = "批量修改任务非必换件数量")
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @PutMapping("/non-part/batch-update/quantity")
    public InvokeResult<Void> batchUpdateTaskNonPartProductQuantity(
            @Valid @RequestBody BatchUpdateContractTaskNonPartProductVo vo) {

        contractTaskNonPartProductService.batchUpdateNonPartProductQuantity(vo);
        
        return InvokeResultBuilder.success();
    }
    
    /**
     * 添加维修状态记录
     */
    @ApiOperation(value = "添加维修状态记录")
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @PostMapping("/repair-status/add")
    public InvokeResult<String> addRepairStatusRecord(
            @Valid @RequestBody CreateContractTaskRepairStatusRecordVo vo) {

        String id = contractTaskRepairStatusRecordService.create(vo);

        return InvokeResultBuilder.success(id);
    }

    /**
     * 获取任务维修执行状态记录列表
     */
    @ApiOperation(value = "获取任务维修执行状态记录列表")
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @GetMapping("/repair-status/list")
    public InvokeResult<List<ContractTaskRepairStatusRecordBo>> getRepairStatusRecords(
            @NotBlank(message = "任务ID不能为空") @RequestParam("taskId") String taskId) {

        List<ContractTaskRepairStatusRecordBo> records = contractTaskRepairStatusRecordService.getByTaskId(taskId);

        return InvokeResultBuilder.success(records);
    }
    
    /**
     * 发起领料申请
     */
    @ApiOperation(value = "发起领料申请")
    @HasPermission({"material:apply"})
    @PostMapping("/material-apply/create")
    public InvokeResult<String> createMaterialApply(@Valid @RequestBody CreateContractTaskMaterialApplyVo vo) {
        
        String id = contractTaskMaterialApplyService.create(vo);
        
        return InvokeResultBuilder.success(id);
    }

    /**
     * 发料单申请补提重审：按申请编号将已审核申请重置为待审
     */
    @ApiOperation(value = "发料单申请补提重审（按申请编号）")
    @ApiImplicitParam(value = "申请编号", name = "applyCode", paramType = "query", required = true)
    @HasPermission({"material:apply"})
    @PostMapping("/material-apply/reopen")
    public InvokeResult<Void> reopenMaterialApply(
            @NotBlank(message = "申请编号不能为空！") @RequestParam("applyCode") String applyCode) {

        contractTaskMaterialApplyService.resetToPendingByApplyCode(applyCode);
        return InvokeResultBuilder.success();
    }

    
    /**
     * 查询领料申请列表
     */
    @ApiOperation(value = "查询领料申请列表")
    @HasPermission({"material:apply"})
    @GetMapping("/material-apply/query")
    public InvokeResult<PageResult<QueryContractTaskMaterialApplyBo>> queryMaterialApply(@Valid QueryContractTaskMaterialApplyVo vo) {
        
        PageResult<QueryContractTaskMaterialApplyBo> pageResult = contractTaskMaterialApplyService.query(getPageIndex(vo), getPageSize(vo), vo);
        
        return InvokeResultBuilder.success(pageResult);
    }
    
    /**
     * 审批领料申请
     */
    @ApiOperation(value = "审批领料申请")
    @HasPermission({"material:apply"})
    @PostMapping("/material-apply/approve")
    public InvokeResult<Void> approveMaterialApply(@Valid @RequestBody ApproveContractTaskMaterialApplyVo vo) {
        
        contractTaskMaterialApplyService.approve(vo);
        
        return InvokeResultBuilder.success();
    }
    
    /**
     * 发料出库
     */
    @ApiOperation("发料出库")
    @ApiImplicitParams({
        @ApiImplicitParam(value = "任务ID", name = "taskId", paramType = "body", required = true),
        @ApiImplicitParam(value = "仓库ID", name = "scId", paramType = "body", required = true),
        @ApiImplicitParam(value = "备注", name = "remark", paramType = "body")
    })
    @HasPermission({"material:apply"})
    @PostMapping("/issue-material")
    public InvokeResult<String> issueMaterial(@Valid @RequestBody IssueMaterialVo vo) {
        
        String materialOrderId = contractTaskService.issueMaterial(vo);
        
        return InvokeResultBuilder.success(materialOrderId);
    }

    /**
     * 获取任务换件清单列表
     */
    @ApiOperation("获取任务换件清单列表")
    @ApiImplicitParams({
        @ApiImplicitParam(value = "任务ID", name = "taskId", paramType = "query", required = true),
        @ApiImplicitParam(value = "仓库ID", name = "scId", paramType = "query", required = true)
    })
    @HasPermission({"material:apply"})
    @GetMapping("/parts-list")
    public InvokeResult<Map<String, List<TaskPartListBo>>> getTaskPartList(
            @NotBlank(message = "任务ID不能为空") @RequestParam("taskId") String taskId,
            @NotBlank(message = "仓库ID不能为空") @RequestParam("scId") String scId) {
        return InvokeResultBuilder.success(contractTaskService.getTaskPartList(taskId, scId));
    }

    /**
     * 上传放行文件
     */
    @ApiOperation("上传放行文件")
    @ApiImplicitParam(value = "任务ID", name = "taskId", paramType = "query", required = true)
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @PostMapping("/approval-file/upload")
    public InvokeResult<List<String>> uploadApprovalFiles(
            @NotBlank(message = "任务ID不能为空！") @RequestParam("taskId") String taskId,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        List<String> fileIds = contractTaskApprovalFileService.uploadApprovalFiles(taskId, files);
        return InvokeResultBuilder.success(fileIds);
    }

    /**
     * 获取放行文件列表
     */
    @ApiOperation("获取放行文件列表")
    @ApiImplicitParam(value = "任务ID", name = "taskId", paramType = "query", required = true)
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @GetMapping("/approval-file/list")
    public InvokeResult<List<ContractTaskApprovalFile>> getApprovalFiles(
            @NotBlank(message = "任务ID不能为空！") @RequestParam("taskId") String taskId) {

        List<ContractTaskApprovalFile> files = contractTaskApprovalFileService.getApprovalFiles(taskId);
        return InvokeResultBuilder.success(files);
    }

    /**
     * 删除放行文件
     */
    @ApiOperation("删除放行文件")
    @ApiImplicitParam(value = "放行文件ID", name = "id", paramType = "path", required = true)
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @DeleteMapping("/approval-file/{id}")
    public InvokeResult<Void> deleteApprovalFile(
            @NotBlank(message = "放行文件ID不能为空！") @PathVariable("id") String id) {

        boolean success = contractTaskApprovalFileService.deleteApprovalFile(id);
        if (!success) {
            throw new DefaultClientException("文件不存在或删除失败！");
        }
        return InvokeResultBuilder.success();
    }
    
    /**
     * 修改合同任务状态
     */
    @ApiOperation("修改合同任务状态")
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @PutMapping("/status")
    public InvokeResult<Void> updateTaskStatus(@Valid @RequestBody UpdateTaskStatusVo vo) {
        contractTaskService.updateTaskStatus(vo);
        
        return InvokeResultBuilder.success();
    }

    /**
     * 修改合同任务放行文件编号
     */
    @ApiOperation("修改合同任务放行文件编号")
    @HasPermission({"maintenance:contract-task","maintenance:aviation","maintenance:factory-wb","maintenance:factory-l"})
    @PutMapping("/approval-file-number")
    public InvokeResult<Void> updateApprovalFileNumber(@Valid @RequestBody UpdateContractTaskApprovalFileNumberVo vo) {
        contractTaskService.updateApprovalFileNumber(vo);

        return InvokeResultBuilder.success();
    }
}
