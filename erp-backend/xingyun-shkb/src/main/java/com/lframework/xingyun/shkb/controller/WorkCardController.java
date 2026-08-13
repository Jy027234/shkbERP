package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.xingyun.shkb.bo.workcard.GetWorkCardBo;
import com.lframework.xingyun.shkb.bo.workcard.QueryWorkCardBo;
import com.lframework.xingyun.shkb.bo.workcard.WorkCardProductBo;
import com.lframework.xingyun.shkb.entity.WorkCardFile;
import com.lframework.xingyun.shkb.service.workcard.WorkCardFileService;
import com.lframework.xingyun.shkb.service.workcard.WorkCardProductService;
import com.lframework.xingyun.shkb.service.workcard.WorkCardService;
import com.lframework.xingyun.shkb.vo.workcard.BatchUpdateWorkCardProductVo;
import com.lframework.xingyun.shkb.vo.workcard.CreateWorkCardVo;
import com.lframework.xingyun.shkb.vo.workcard.QueryWorkCardVo;
import com.lframework.xingyun.shkb.vo.workcard.UpdateWorkCardVo;
import com.lframework.xingyun.shkb.vo.workcard.WorkCardProductVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 工卡管理
 *
 * @author kison
 */
@Api(tags = "工卡管理")
@Validated
@RestController
@RequestMapping("/shkb/work-card")
public class WorkCardController extends DefaultBaseController {

    @Autowired
    private WorkCardService workCardService;
    
    @Autowired
    private WorkCardProductService workCardProductService;
    
    @Autowired
    private WorkCardFileService workCardFileService;

    /**
     * 工卡列表
     */
    @ApiOperation("工卡列表")
    @HasPermission({"work-card"})
    @GetMapping("/query")
    public InvokeResult<PageResult<QueryWorkCardBo>> query(@Valid QueryWorkCardVo vo) {

        PageResult<QueryWorkCardBo> pageResult = workCardService.query(getPageIndex(vo), getPageSize(vo), vo);

        return InvokeResultBuilder.success(pageResult);
    }

    /**
     * 工卡详情
     */
    @ApiOperation("工卡详情")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
    @HasPermission({"work-card"})
    @GetMapping
    public InvokeResult<GetWorkCardBo> get(@NotBlank(message = "ID不能为空！") String id) {

        GetWorkCardBo result = workCardService.getDetail(id);
        if (result == null) {
            throw new DefaultClientException("工卡不存在！");
        }

        return InvokeResultBuilder.success(result);
    }

    /**
     * 新增工卡
     */
    @ApiOperation("新增工卡")
    @HasPermission({"work-card"})
    @PostMapping
    public InvokeResult<String> create(@Valid @RequestBody CreateWorkCardVo vo) {
        String id = workCardService.create(vo);

        return InvokeResultBuilder.success(id);
    }

    /**
     * 修改工卡
     */
    @ApiOperation("修改工卡")
    @HasPermission({"work-card"})
    @PostMapping("/update")
    public InvokeResult<Void> update(@Valid @RequestBody UpdateWorkCardVo vo) {
        workCardService.update(vo);

        return InvokeResultBuilder.success();
    }

    /**
     * 删除工卡
     */
    @ApiOperation("删除工卡")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
    @HasPermission({"work-card"})
    @GetMapping("/delete")
    public InvokeResult<Void> delete(@NotBlank(message = "ID不能为空！") String id) {

        workCardService.deleteById(id);

        return InvokeResultBuilder.success();
    }
    
    /**
     * 查询工卡必换件列表
     */
    @ApiOperation("查询工卡必换件列表")
    @ApiImplicitParam(value = "工卡ID", name = "workCardId", paramType = "query", required = true)
    @HasPermission({"work-card"})
    @GetMapping("/products")
    public InvokeResult<List<WorkCardProductBo>> getProducts(@NotBlank(message = "工卡ID不能为空！") String workCardId) {

        List<WorkCardProductBo> results = workCardProductService.getByWorkCardId(workCardId);

        return InvokeResultBuilder.success(results);
    }
    
    /**
     * 批量添加工卡必换件
     */
    @ApiOperation("批量添加工卡必换件")
    @HasPermission({"work-card"})
    @PostMapping("/product/add")
    public InvokeResult<Void> batchAddProducts(@Valid @RequestBody WorkCardProductVo vo) {

        workCardProductService.batchAdd(vo);

        return InvokeResultBuilder.success();
    }
    
    /**
     * 批量删除工卡必换件
     */
    @ApiOperation("批量删除工卡必换件")
    @HasPermission({"work-card"})
    @PostMapping("/product/delete")
    public InvokeResult<Void> batchDeleteProducts(@Valid @RequestBody WorkCardProductVo vo) {

        workCardProductService.batchDelete(vo);

        return InvokeResultBuilder.success();
    }
    
    /**
     * 批量修改工卡必换件数量
     */
    @ApiOperation("批量修改工卡必换件数量")
    @HasPermission({"work-card"})
    @PostMapping("/product/update-quantity")
    public InvokeResult<Void> batchUpdateProductQuantity(@Valid @RequestBody BatchUpdateWorkCardProductVo vo) {

        workCardProductService.batchUpdateQuantity(vo);

        return InvokeResultBuilder.success();
    }
    
    /**
     * 上传工卡附件
     */
    @ApiOperation("上传工卡附件")
    @ApiImplicitParam(value = "工卡ID", name = "workCardId", paramType = "query", required = true)
    @HasPermission({"work-card"})
    @PostMapping("/attachment/upload")
    public InvokeResult<List<String>> uploadWorkCardAttachments(
            @NotBlank(message = "工卡ID不能为空！") @RequestParam("workCardId") String workCardId,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        
        // 调用服务上传文件并获取文件ID列表
        List<String> fileIds = workCardFileService.uploadWorkCardFiles(workCardId, files);
        
        return InvokeResultBuilder.success(fileIds);
    }
    
    /**
     * 获取工卡附件列表
     */
    @ApiOperation("获取工卡附件列表")
    @ApiImplicitParam(value = "工卡ID", name = "workCardId", paramType = "query", required = true)
    @HasPermission({"work-card"})
    @GetMapping("/attachment/list")
    public InvokeResult<List<WorkCardFile>> getWorkCardAttachments(
            @NotBlank(message = "工卡ID不能为空！") @RequestParam("workCardId") String workCardId) {
        
        // 调用服务获取附件列表
        List<WorkCardFile> files = workCardFileService.getWorkCardFiles(workCardId);
        
        return InvokeResultBuilder.success(files);
    }
    
    /**
     * 删除工卡附件
     */
    @ApiOperation("删除工卡附件")
    @ApiImplicitParam(value = "附件ID", name = "id", paramType = "path", required = true)
    @HasPermission({"work-card"})
    @DeleteMapping("/attachment/{id}")
    public InvokeResult<Void> deleteWorkCardAttachment(
            @NotBlank(message = "工卡附件id不能为空！")
            @PathVariable("id") String id) {
        
        boolean success = workCardFileService.deleteWorkCardFile(id);
        if (!success) {
            throw new DefaultClientException("附件不存在或删除失败！");
        }
        
        return InvokeResultBuilder.success();
    }
    
    /**
     * 批量删除工卡附件
     */
    @ApiOperation("批量删除工卡附件")
    @HasPermission({"work-card"})
    @DeleteMapping("/attachment/batch")
    public InvokeResult<Integer> batchDeleteWorkCardAttachments(@RequestBody List<String> ids) {
        
        if (CollectionUtil.isEmpty(ids)) {
            throw new DefaultClientException("附件ID列表不能为空！");
        }
        
        int count = workCardFileService.batchDeleteWorkCardFiles(ids);
        
        return InvokeResultBuilder.success(count);
    }
}
