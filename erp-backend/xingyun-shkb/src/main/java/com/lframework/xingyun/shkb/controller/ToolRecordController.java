package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.xingyun.shkb.bo.tool.GetToolRecordBo;
import com.lframework.xingyun.shkb.bo.tool.QueryToolRecordBo;
import com.lframework.xingyun.shkb.bo.tool.ToolRecordFileBo;
import com.lframework.xingyun.shkb.entity.ShkbToolRecord;
import com.lframework.xingyun.shkb.entity.ToolRecordFile;
import com.lframework.xingyun.shkb.service.ShkbToolRecordService;
import com.lframework.xingyun.shkb.service.ToolRecordFileService;
import com.lframework.xingyun.shkb.service.ShkbToolService;
import org.springframework.web.multipart.MultipartFile;
import com.lframework.xingyun.shkb.vo.tool.CreateToolRecordVo;
import com.lframework.xingyun.shkb.vo.tool.QueryToolRecordVo;
import com.lframework.xingyun.shkb.vo.tool.UpdateToolRecordVo;
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
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工具计量记录 Controller
 */
@Api(tags = "工具计量记录")
@Validated
@RestController
@RequestMapping("/shkb/tool/record")
public class ToolRecordController extends DefaultBaseController {

    @Autowired
    private ShkbToolRecordService toolRecordService;
    
    @Autowired
    private ToolRecordFileService toolRecordFileService;
    
    @Autowired
    private ShkbToolService toolService;

    /**
     * 查询工具计量记录列表
     */
    @ApiOperation("查询工具计量记录列表")
    @HasPermission({"equipment:tool"})
    @GetMapping("/query")
    public InvokeResult<PageResult<QueryToolRecordBo>> query(@Valid QueryToolRecordVo vo) {
        PageResult<ShkbToolRecord> pageResult = toolRecordService.query(getPageIndex(vo), getPageSize(vo), vo);

        List<ShkbToolRecord> datas = pageResult.getDatas();
        List<QueryToolRecordBo> results = null;

        if (!CollectionUtil.isEmpty(datas)) {
            results = datas.stream().map(record -> {
                QueryToolRecordBo bo = new QueryToolRecordBo(record);
                
                // 查询附件列表
                List<ToolRecordFile> files = toolRecordFileService.getToolRecordFiles(record.getId());
                if (!CollectionUtil.isEmpty(files)) {
                    List<ToolRecordFileBo> fileBos = files.stream()
                            .map(ToolRecordFileBo::new)
                            .collect(Collectors.toList());
                    bo.setAttachments(fileBos);
                }
                
                return bo;
            }).collect(Collectors.toList());
        }

        return InvokeResultBuilder.success(PageResultUtil.rebuild(pageResult, results));
    }

    /**
     * 查询工具计量记录详情
     */
    @ApiOperation("查询工具计量记录详情")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "path", required = true)
    @HasPermission({"equipment:tool"})
    @GetMapping("/{id}")
    public InvokeResult<GetToolRecordBo> get(@NotBlank(message = "ID不能为空") @PathVariable String id) {
        ShkbToolRecord data = toolRecordService.findById(id);
        if (data == null) {
            throw new DefaultClientException("工具计量记录不存在");
        }

        return InvokeResultBuilder.success(new GetToolRecordBo(data));
    }

    /**
     * 创建工具计量记录
     */
    @ApiOperation("创建工具计量记录")
    @HasPermission({"equipment:tool"})
    @PostMapping
    public InvokeResult<String> create(
            @RequestParam(value = "toolId", required = true) String toolId,
            @RequestParam(value = "maintenancenUser", required = true) String maintenancenUser,
            @RequestParam(value = "maintenanceTime", required = true) String maintenanceTimeStr,
            @RequestParam(value = "certificateNumber", required = true) String certificateNumber,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        // 创建CreateToolRecordVo对象
        CreateToolRecordVo vo = new CreateToolRecordVo();
        vo.setToolId(toolId);
        vo.setMaintenancenUser(maintenancenUser);
        
        // 处理日期格式转换
        try {
            LocalDate maintenanceTime = LocalDate.parse(maintenanceTimeStr);
            vo.setMaintenanceTime(maintenanceTime);
        } catch (Exception e) {
            throw new DefaultClientException("计量时间格式不正确，请使用YYYY-MM-DD格式");
        }
        
        vo.setCertificateNumber(certificateNumber);
        vo.setDescription(description);
        
        // 创建工具计量记录
        String id = toolRecordService.create(vo);
        
        // 上传附件（如果有）
        if (!CollectionUtil.isEmpty(files)) {
            toolRecordFileService.uploadToolRecordFiles(id, files);
        }
        
        // 同步更新计量工具的证书编号
        toolService.updateCertificateNumber(vo.getToolId(), vo.getCertificateNumber());

        return InvokeResultBuilder.success(id);
    }

    /**
     * 修改工具计量记录
     */
    @ApiOperation("修改工具计量记录")
    @HasPermission({"equipment:tool"})
    @PostMapping("/update")
    public InvokeResult<Void> update(
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "toolId", required = true) String toolId,
            @RequestParam(value = "maintenancenUser", required = true) String maintenancenUser,
            @RequestParam(value = "maintenanceTime", required = true) String maintenanceTimeStr,
            @RequestParam(value = "certificateNumber", required = true) String certificateNumber,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        
        // 创建UpdateToolRecordVo对象
        UpdateToolRecordVo vo = new UpdateToolRecordVo();
        vo.setId(id);
        vo.setToolId(toolId);
        vo.setMaintenancenUser(maintenancenUser);
        
        // 处理日期格式转换
        try {
            LocalDate maintenanceTime = LocalDate.parse(maintenanceTimeStr);
            vo.setMaintenanceTime(maintenanceTime);
        } catch (Exception e) {
            throw new DefaultClientException("计量时间格式不正确，请使用YYYY-MM-DD格式");
        }
        
        vo.setCertificateNumber(certificateNumber);
        vo.setDescription(description);
        
        // 更新工具计量记录
        toolRecordService.update(vo);
        
        // 先删除原有附件，再重新上传新附件（如果有）
        // toolRecordFileService.deleteByRecordId(id); 手动删除
        if (!CollectionUtil.isEmpty(files)) {
            toolRecordFileService.uploadToolRecordFiles(id, files);
        }
        
        // 检查是否是最新的一条计量记录，如果是则同步更新计量工具的证书编号
        boolean isLatest = toolRecordService.isLatestRecord(vo.getId());
        if (isLatest) {
            toolService.updateCertificateNumber(vo.getToolId(), vo.getCertificateNumber());
        }

        return InvokeResultBuilder.success();
    }

    /**
     * 删除工具计量记录
     */
    @ApiOperation("删除工具计量记录")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "path", required = true)
    @HasPermission({"equipment:tool"})
    @DeleteMapping("/{id}")
    public InvokeResult<Void> delete(@NotBlank(message = "ID不能为空") @PathVariable String id) {
        toolRecordService.deleteById(id);

        return InvokeResultBuilder.success();
    }
    
    /**
     * 删除工具计量记录附件
     */
    @ApiOperation("删除工具计量记录附件")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "path", required = true)
    @HasPermission({"equipment:tool"})
    @DeleteMapping("/attachment/{id}")
    public InvokeResult<Void> deleteAttachment(@NotBlank(message = "附件ID不能为空") @PathVariable String id) {
        boolean success = toolRecordFileService.deleteToolRecordFile(id);
        if (!success) {
            throw new DefaultClientException("附件不存在或已被删除");
        }

        return InvokeResultBuilder.success();
    }

    /**
     * 批量删除工具计量记录
     */
    @ApiOperation("批量删除工具计量记录")
    @HasPermission({"equipment:tool"})
    @DeleteMapping("/batch")
    public InvokeResult<Void> batchDelete(
            @ApiParam(value = "ID", required = true) @NotEmpty(message = "请选择需要删除的工具计量记录") @RequestParam List<String> ids) {
        toolRecordService.deleteByIds(ids);

        return InvokeResultBuilder.success();
    }
}
