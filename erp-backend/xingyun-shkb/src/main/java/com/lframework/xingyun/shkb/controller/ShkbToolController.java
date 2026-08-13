package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.shkb.bo.tool.GetShkbToolBo;
import com.lframework.xingyun.shkb.bo.tool.QueryShkbToolBo;
import com.lframework.xingyun.shkb.entity.ShkbTool;
import com.lframework.xingyun.shkb.entity.ShkbToolFile;
import com.lframework.xingyun.shkb.service.ShkbToolFileService;
import com.lframework.xingyun.shkb.service.ShkbToolService;
import com.lframework.xingyun.shkb.vo.tool.CreateShkbToolVo;
import com.lframework.xingyun.shkb.vo.tool.QueryShkbToolVo;
import com.lframework.xingyun.shkb.vo.tool.UpdateShkbToolVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工具管理 Controller
 *
 * @author kison
 */
@Api(tags = "工具管理")
@Validated
@RestController
@RequestMapping("/shkb/tool")
public class ShkbToolController extends DefaultBaseController {

    @Autowired
    private ShkbToolService shkbToolService;
    
    @Autowired
    private ShkbToolFileService shkbToolFileService;

    /**
     * 查询工具列表
     */
    @ApiOperation("查询工具列表")
    @HasPermission({"equipment:tool"})
    @GetMapping("/query")
    public InvokeResult<PageResult<QueryShkbToolBo>> query(@Valid QueryShkbToolVo vo) {
        PageResult<ShkbTool> pageResult = shkbToolService.query(getPageIndex(vo), getPageSize(vo), vo);

        List<ShkbTool> datas = pageResult.getDatas();
        List<QueryShkbToolBo> results = null;

        if (!CollectionUtil.isEmpty(datas)) {
            results = datas.stream().map(QueryShkbToolBo::new).collect(Collectors.toList());
        }

        return InvokeResultBuilder.success(PageResultUtil.rebuild(pageResult, results));
    }

    /**
     * 根据ID查询工具
     */
    @ApiOperation("根据ID查询工具")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "path", required = true)
    @HasPermission({"equipment:tool"})
    @GetMapping("/{id}")
    public InvokeResult<GetShkbToolBo> get(@NotBlank(message = "ID不能为空！") @PathVariable String id) {
        ShkbTool tool = shkbToolService.findById(id);
        if (tool == null) {
            return InvokeResultBuilder.success(null);
        }

        return InvokeResultBuilder.success(new GetShkbToolBo(tool));
    }
    
    /**
     * 新增工具
     */
    @ApiOperation("新增工具")
    @HasPermission({"equipment:tool"})
    @PostMapping
    public InvokeResult<String> create(
            @RequestParam(value = "managementArea", required = true) String managementArea,
            @RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "code", required = true) String code,
            @RequestParam(value = "certificateNumber", required = true) String certificateNumber,
            @RequestParam(value = "specification", required = true) String specification,
            @RequestParam(value = "model", required = true) String model,
            @RequestParam(value = "standard", required = true) String standard,
            @RequestParam(value = "precision", required = false) String precision,
            @RequestParam(value = "storageLocation", required = true) String storageLocation,
            @RequestParam(value = "lastMaintenanceTime", required = true) String lastMaintenanceTimeStr,
            @RequestParam(value = "nextMaintenanceTime", required = true) String nextMaintenanceTimeStr,
            @RequestParam(value = "calibrationPeriod", required = true) Integer calibrationPeriod,
            @RequestParam(value = "lastMaintenanceUnit", required = true) String lastMaintenanceUnit,
            @RequestParam(value = "maintenancenUser", required = true) String maintenancenUser,
            @RequestParam(value = "recordCertificateNumber", required = true) String recordCertificateNumber,
            @RequestParam(value = "available", required = true) Boolean available,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        
        // 创建CreateShkbToolVo对象
        CreateShkbToolVo vo = new CreateShkbToolVo();
        vo.setManagementArea(managementArea);
        vo.setName(name);
        vo.setCode(code);
        vo.setCertificateNumber(certificateNumber);
        vo.setSpecification(specification);
        vo.setModel(model);
        vo.setStandard(standard);
        vo.setPrecision(precision);
        vo.setStorageLocation(storageLocation);
        
        // 处理日期格式转换
        try {
            LocalDate lastMaintenanceTime = LocalDate.parse(lastMaintenanceTimeStr);
            vo.setLastMaintenanceTime(lastMaintenanceTime);
        } catch (Exception e) {
            throw new DefaultClientException("上次计量日期格式不正确，请使用YYYY-MM-DD格式");
        }
        
        try {
            LocalDate nextMaintenanceTime = LocalDate.parse(nextMaintenanceTimeStr);
            vo.setNextMaintenanceTime(nextMaintenanceTime);
        } catch (Exception e) {
            throw new DefaultClientException("下次计量日期格式不正确，请使用YYYY-MM-DD格式");
        }
        
        vo.setCalibrationPeriod(calibrationPeriod);
        vo.setLastMaintenanceUnit(lastMaintenanceUnit);
        vo.setMaintenancenUser(maintenancenUser);
        vo.setRecordCertificateNumber(recordCertificateNumber);
        vo.setAvailable(available);
        vo.setDescription(description);
        
        // 创建工具并初始化计量记录
        String id = shkbToolService.create(vo, files);
        
        return InvokeResultBuilder.success(id);
    }

    /**
     * 修改工具
     */
    @ApiOperation("修改工具")
    @HasPermission({"equipment:tool"})
    @PutMapping
    public InvokeResult<Void> update(@Valid @RequestBody UpdateShkbToolVo vo) {
        shkbToolService.update(vo);

        return InvokeResultBuilder.success();
    }

    /**
     * 根据ID删除工具
     */
    @ApiOperation("根据ID删除工具")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
    @HasPermission({"equipment:tool"})
    @DeleteMapping
    public InvokeResult<Void> delete(@NotBlank(message = "ID不能为空！") String id) {
        shkbToolService.deleteById(id);

        return InvokeResultBuilder.success();
    }

    /**
     * 批量删除工具
     */
    @ApiOperation("批量删除工具")
    @HasPermission({"equipment:tool"})
    @DeleteMapping("/batch")
    public InvokeResult<Void> batchDelete(
            @ApiParam(value = "ID", required = true) @NotEmpty(message = "ID不能为空！") @RequestParam(value = "ids") List<String> ids) {
        shkbToolService.deleteByIds(ids);

        return InvokeResultBuilder.success();
    }
    
    /**
     * 上传工具附件
     */
    @ApiOperation("上传工具附件")
    @ApiImplicitParam(value = "工具ID", name = "toolId", paramType = "query", required = true)
    @HasPermission({"equipment:tool"})
    @PostMapping("/attachment/upload")
    public InvokeResult<List<String>> uploadToolAttachments(
            @NotBlank(message = "工具ID不能为空！") @RequestParam("toolId") String toolId,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        
        // 调用服务上传文件并获取文件ID列表
        List<String> fileIds = shkbToolFileService.uploadToolFiles(toolId, files);
        
        return InvokeResultBuilder.success(fileIds);
    }
    
    /**
     * 获取工具附件列表
     */
    @ApiOperation("获取工具附件列表")
    @ApiImplicitParam(value = "工具ID", name = "toolId", paramType = "query", required = true)
    @HasPermission({"equipment:tool"})
    @GetMapping("/attachment/list")
    public InvokeResult<List<ShkbToolFile>> getToolAttachments(
            @NotBlank(message = "工具ID不能为空！") @RequestParam("toolId") String toolId) {
        
        // 调用服务获取附件列表
        List<ShkbToolFile> files = shkbToolFileService.getToolFiles(toolId);
        
        return InvokeResultBuilder.success(files);
    }
    
    /**
     * 删除工具附件
     */
    @ApiOperation("删除工具附件")
    @ApiImplicitParam(value = "附件ID", name = "id", paramType = "path", required = true)
    @HasPermission({"equipment:tool"})
    @DeleteMapping("/attachment/{id}")
    public InvokeResult<Void> deleteToolAttachment(
            @NotBlank(message = "工具附件id不能为空！")
            @PathVariable("id") String id) {
        
        boolean success = shkbToolFileService.deleteToolFile(id);
        if (!success) {
            throw new DefaultClientException("附件不存在或删除失败！");
        }
        
        return InvokeResultBuilder.success();
    }
    
    /**
     * 批量删除工具附件
     */
    @ApiOperation("批量删除工具附件")
    @HasPermission({"equipment:tool"})
    @DeleteMapping("/attachment/batch")
    public InvokeResult<Integer> batchDeleteToolAttachments(@RequestBody List<String> ids) {
        
        if (CollectionUtil.isEmpty(ids)) {
            throw new DefaultClientException("附件ID列表不能为空！");
        }
        
        int count = shkbToolFileService.batchDeleteToolFiles(ids);
        
        return InvokeResultBuilder.success(count);
    }
}
