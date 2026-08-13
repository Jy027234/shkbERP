package com.lframework.xingyun.sc.controller.stock;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.xingyun.sc.entity.ProductStockBatchFile;
import com.lframework.xingyun.sc.service.stock.ProductStockBatchFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * 商品批次库存附件 Controller
 *
 * @author kison
 */
@Api(tags = "商品批次库存附件")
@Validated
@RestController
@RequestMapping("/stock/product/batch/file")
public class ProductStockBatchFileController extends DefaultBaseController {

    @Autowired
    private ProductStockBatchFileService productStockBatchFileService;

    /**
     * 上传商品批次库存附件
     */
    @ApiOperation("上传商品批次库存附件")
    @ApiImplicitParam(value = "批次库存ID", name = "batchId", paramType = "query", required = true)
    @HasPermission({"stock:product-batch"})
    @PostMapping("/upload")
    public InvokeResult<List<String>> upload(
            @NotBlank(message = "批次库存ID不能为空！") @RequestParam("batchId") String batchId,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        
        if (CollectionUtil.isEmpty(files)) {
            throw new DefaultClientException("请选择上传的文件！");
        }
        
        // 需要在ProductStockBatchFileService中实现该方法
        List<String> fileIds = productStockBatchFileService.uploadBatchFiles(batchId, files);
        
        return InvokeResultBuilder.success(fileIds);
    }
    
    /**
     * 获取商品批次库存附件列表
     */
    @ApiOperation("获取商品批次库存附件列表")
    @ApiImplicitParam(value = "批次库存ID", name = "batchId", paramType = "query", required = true)
    @HasPermission({"stock:product-batch"})
    @GetMapping("/list")
    public InvokeResult<List<ProductStockBatchFile>> list(
            @NotBlank(message = "批次库存ID不能为空！") @RequestParam("batchId") String batchId) {
        
        // 需要在ProductStockBatchFileService中实现该方法
        List<ProductStockBatchFile> files = productStockBatchFileService.getBatchFiles(batchId);
        
        return InvokeResultBuilder.success(files);
    }
    
    /**
     * 删除商品批次库存附件
     */
    @ApiOperation("删除商品批次库存附件")
    @ApiImplicitParam(value = "附件ID", name = "id", paramType = "path", required = true)
    @HasPermission({"stock:product-batch"})
    @DeleteMapping("/{id}")
    public InvokeResult<Void> delete(
            @NotBlank(message = "附件ID不能为空！") @PathVariable("id") String id) {
        
        // 需要在ProductStockBatchFileService中实现该方法
        boolean success = productStockBatchFileService.deleteBatchFile(id);
        if (!success) {
            throw new DefaultClientException("附件不存在或删除失败！");
        }
        
        return InvokeResultBuilder.success();
    }
    
    /**
     * 批量删除商品批次库存附件
     */
    @ApiOperation("批量删除商品批次库存附件")
    @HasPermission({"stock:product-batch"})
    @DeleteMapping("/batch")
    public InvokeResult<Integer> batchDelete(@RequestBody List<String> ids) {
        
        if (CollectionUtil.isEmpty(ids)) {
            throw new DefaultClientException("附件ID列表不能为空！");
        }
        
        // 需要在ProductStockBatchFileService中实现该方法
        int count = productStockBatchFileService.batchDeleteBatchFiles(ids);
        
        return InvokeResultBuilder.success(count);
    }
}
