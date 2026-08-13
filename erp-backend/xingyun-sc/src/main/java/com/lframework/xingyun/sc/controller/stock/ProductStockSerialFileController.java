package com.lframework.xingyun.sc.controller.stock;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.xingyun.sc.entity.ProductStockSerialFile;
import com.lframework.xingyun.sc.service.stock.ProductStockSerialFileService;
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
 * 商品序列号库存附件 Controller
 *
 * @author kison
 */
@Api(tags = "商品序列号库存附件")
@Validated
@RestController
@RequestMapping("/stock/product/serial/file")
public class ProductStockSerialFileController extends DefaultBaseController {

    @Autowired
    private ProductStockSerialFileService productStockSerialFileService;

    /**
     * 上传商品序列号库存附件
     */
    @ApiOperation("上传商品序列号库存附件")
    @ApiImplicitParam(value = "序列号库存ID", name = "serialId", paramType = "query", required = true)
    @HasPermission({"stock:product-serial"})
    @PostMapping("/upload")
    public InvokeResult<List<String>> upload(
            @NotBlank(message = "序列号库存ID不能为空！") @RequestParam("serialId") String serialId,
            @RequestParam("files") MultipartFile[] files) {

        if (files == null || files.length == 0) {
            throw new DefaultClientException("请选择要上传的文件！");
        }

        List<String> uploadedFileNames = productStockSerialFileService.uploadSerialFiles(serialId, files);

        return InvokeResultBuilder.success(uploadedFileNames);
    }

    /**
     * 获取商品序列号库存附件列表
     */
    @ApiOperation("获取商品序列号库存附件列表")
    @ApiImplicitParam(value = "序列号库存ID", name = "serialId", paramType = "query", required = true)
    @HasPermission({"stock:product-serial"})
    @GetMapping("/list")
    public InvokeResult<List<ProductStockSerialFile>> list(
            @NotBlank(message = "序列号库存ID不能为空！") @RequestParam("serialId") String serialId) {

        List<ProductStockSerialFile> attachments = productStockSerialFileService.getBySerialId(serialId);

        return InvokeResultBuilder.success(attachments);
    }

    /**
     * 删除商品序列号库存附件
     */
    @ApiOperation("删除商品序列号库存附件")
    @ApiImplicitParam(value = "附件ID", name = "id", paramType = "path", required = true)
    @HasPermission({"stock:product-serial"})
    @DeleteMapping("/{id}")
    public InvokeResult<Void> delete(
            @NotBlank(message = "附件ID不能为空！") @PathVariable("id") String id) {

        productStockSerialFileService.deleteSerialFile(id);

        return InvokeResultBuilder.success();
    }

    /**
     * 批量删除商品序列号库存附件
     */
    @ApiOperation("批量删除商品序列号库存附件")
    @HasPermission({"stock:product-serial"})
    @DeleteMapping("/batch")
    public InvokeResult<Integer> batchDelete(@RequestBody List<String> ids) {
        
        if (CollectionUtil.isEmpty(ids)) {
            throw new DefaultClientException("请选择要删除的附件！");
        }

        Integer deletedCount = productStockSerialFileService.batchDeleteSerialFiles(ids);

        return InvokeResultBuilder.success(deletedCount);
    }
}
