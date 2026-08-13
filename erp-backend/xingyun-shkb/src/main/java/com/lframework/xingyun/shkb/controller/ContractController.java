package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.ExcelUtil;
import com.lframework.starter.web.core.components.excel.ExcelMultipartWriterSheetBuilder;
import com.lframework.xingyun.shkb.bo.contract.GetContractBo;
import com.lframework.xingyun.shkb.bo.contract.QueryContractBo;
import com.lframework.xingyun.shkb.service.contract.ContractService;
import com.lframework.xingyun.shkb.service.contract.ContractTaskService;
import com.lframework.xingyun.shkb.service.contract.ContractFileService;
import com.lframework.xingyun.shkb.entity.ContractFile;
import com.lframework.xingyun.shkb.vo.contract.CreateContractTaskVo;
import com.lframework.xingyun.shkb.vo.contract.CreateContractVo;
import com.lframework.xingyun.shkb.vo.contract.QueryContractVo;
import com.lframework.xingyun.shkb.vo.contract.UpdateContractVo;
import com.lframework.xingyun.shkb.vo.contract.UpdateContractStatusVo;
import com.lframework.xingyun.shkb.enums.ContractStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import com.lframework.xingyun.shkb.excel.contract.ContractExportModel;

/**
 * 合同管理
 *
 * @author kison
 */
@Api(tags = "合同管理")
@Validated
@RestController
@RequestMapping("/shkb/contract")
public class ContractController extends DefaultBaseController {

    @Autowired
    private ContractService contractService;
    
    @Autowired
    private ContractTaskService contractTaskService;
    
    @Autowired
    private ContractFileService contractFileService;

    /**
     * 合同列表
     */
    @ApiOperation("合同列表")
    @HasPermission({"contract:factory-wb","contract:aviation","contract:factory-l"})
    @GetMapping("/query")
    public InvokeResult<PageResult<QueryContractBo>> query(@Valid QueryContractVo vo) {

        PageResult<QueryContractBo> pageResult = contractService.query(getPageIndex(vo), getPageSize(vo), vo);

        return InvokeResultBuilder.success(pageResult);
    }

    /**
     * 导出合同
     */
    @ApiOperation("导出合同")
    @HasPermission({"contract:factory-wb","contract:aviation","contract:factory-l"})
    @PostMapping("/export")
    public void export(@Valid @RequestBody QueryContractVo vo) {
        ExcelMultipartWriterSheetBuilder builder = ExcelUtil.multipartExportXls("合同信息",
                ContractExportModel.class);

        try {
            // 如果前端传入了合同ID列表（多个用逗号分隔），则优先按ID列表导出
            if (vo.getIds() != null && !vo.getIds().trim().isEmpty()) {
                List<String> idList = java.util.Arrays.stream(vo.getIds().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                if (!idList.isEmpty()) {
                    // 逐个ID查询详情并导出，避免修改现有Mapper
                    List<ContractExportModel> models = idList.stream()
                            .map(id -> contractService.getDetail(id))
                            .filter(java.util.Objects::nonNull)
                            .map(ContractExportModel::new)
                            .collect(Collectors.toList());
                    builder.doWrite(models);
                    return;
                }
            }

            int pageIndex = 1;
            while (true) {
                PageResult<QueryContractBo> pageResult = contractService.query(pageIndex, com.lframework.starter.web.core.controller.ExportSizeSupport.DEFAULT_EXPORT_SIZE, vo);
                List<QueryContractBo> datas = pageResult.getDatas();
                List<ContractExportModel> models = datas.stream().map(ContractExportModel::new)
                        .collect(Collectors.toList());
                builder.doWrite(models);

                if (!pageResult.isHasNext()) {
                    break;
                }
                pageIndex++;
            }
        } finally {
            builder.finish();
        }
    }

    /**
     * 合同详情
     */
    @ApiOperation("合同详情")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
    @HasPermission({"contract:factory-wb","contract:aviation","contract:factory-l"})
    @GetMapping
    public InvokeResult<GetContractBo> get(@NotBlank(message = "ID不能为空！") String id) {

        GetContractBo result = contractService.getDetail(id);
        if (result == null) {
            throw new DefaultClientException("合同不存在！");
        }

        return InvokeResultBuilder.success(result);
    }

    /**
     * 新增合同
     */
    @ApiOperation("新增合同")
    @HasPermission({"contract:factory-wb","contract:aviation","contract:factory-l"})
    @PostMapping
    public InvokeResult<Void> create(@Valid @RequestBody CreateContractVo vo) {

        contractService.create(vo);

        return InvokeResultBuilder.success();
    }

    /**
     * 修改合同
     */
    @ApiOperation("修改合同")
    @HasPermission({"contract:factory-wb","contract:aviation","contract:factory-l"})
    @PutMapping
    public InvokeResult<Void> update(@Valid @RequestBody UpdateContractVo vo) {

        contractService.update(vo);

        contractService.cleanCacheByKey(vo.getId());

        return InvokeResultBuilder.success();
    }
    
    /**
     * 生成合同任务
     */
    @ApiOperation("生成合同任务")
    @HasPermission({"contract:factory-wb","contract:aviation","contract:factory-l"})
    @PostMapping("/create-task")
    public InvokeResult<Void> createContractTask(@Valid @RequestBody CreateContractTaskVo vo) {
        
        contractTaskService.createContractTask(vo.getContractId());
        
        return InvokeResultBuilder.success();
    }
    
    /**
     * 上传合同附件
     */
    @ApiOperation("上传合同附件")
    @ApiImplicitParam(value = "合同ID", name = "contractId", paramType = "query", required = true)
    @HasPermission({"contract:factory-wb","contract:aviation","contract:factory-l"})
    @PostMapping("/attachment/upload")
    public InvokeResult<List<String>> uploadContractAttachments(
            @NotBlank(message = "合同ID不能为空！") @RequestParam("contractId") String contractId,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        
        // 调用服务上传文件并获取文件ID列表
        List<String> fileIds = contractFileService.uploadContractFiles(contractId, files);
        
        return InvokeResultBuilder.success(fileIds);
    }
    
    /**
     * 获取合同附件列表
     */
    @ApiOperation("获取合同附件列表")
    @ApiImplicitParam(value = "合同ID", name = "contractId", paramType = "query", required = true)
    @HasPermission({"contract:factory-wb","contract:aviation","contract:factory-l"})
    @GetMapping("/attachment/list")
    public InvokeResult<List<ContractFile>> getContractAttachments(
            @NotBlank(message = "合同ID不能为空！") @RequestParam("contractId") String contractId) {
        
        // 调用服务获取附件列表
        List<ContractFile> files = contractFileService.getContractFiles(contractId);
        
        return InvokeResultBuilder.success(files);
    }
    
    /**
     * 删除合同附件
     */
    @ApiOperation("删除合同附件")
    @ApiImplicitParam(value = "附件ID", name = "id", paramType = "query", required = true)
    @HasPermission({"contract:factory-wb","contract:aviation","contract:factory-l"})
    @DeleteMapping("/attachment/{id}")
    public InvokeResult<Void> deleteContractAttachment(
            @NotBlank(message = "合同附件id不能为空！")
            @PathVariable("id") String id) {
        
        boolean success = contractFileService.deleteContractFile(id);
        if (!success) {
            throw new DefaultClientException("附件不存在或删除失败！");
        }
        
        return InvokeResultBuilder.success();
    }
    
    /**
     * 批量删除合同附件
     */
    @ApiOperation("批量删除合同附件")
    @HasPermission({"contract:factory-wb","contract:aviation","contract:factory-l"})
    @DeleteMapping("/attachment/batch")
    public InvokeResult<Integer> batchDeleteContractAttachments(@RequestBody List<String> ids) {
        
        if (CollectionUtil.isEmpty(ids)) {
            throw new DefaultClientException("附件ID列表不能为空！");
        }
        
        int count = contractFileService.batchDeleteContractFiles(ids);
        
        return InvokeResultBuilder.success(count);
    }
    
    /**
     * 修改合同状态
     */
    @ApiOperation("修改合同状态")
    @HasPermission({"contract:factory-wb","contract:aviation","contract:factory-l"})
    @PutMapping("/status")
    public InvokeResult<Void> updateContractStatus(@Valid @RequestBody UpdateContractStatusVo vo) {
        
        contractService.updateStatus(vo);
        
        return InvokeResultBuilder.success();
    }
}
