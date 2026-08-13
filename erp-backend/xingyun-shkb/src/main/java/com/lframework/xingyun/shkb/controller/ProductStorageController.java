package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.shkb.bo.productstorage.QueryProductStorageBo;
import com.lframework.xingyun.shkb.entity.ProductStorage;
import com.lframework.xingyun.shkb.entity.ProductStorageFile;
import com.lframework.xingyun.shkb.service.ProductStorageService;
import com.lframework.xingyun.shkb.service.ProductStorageFileService;
import com.lframework.xingyun.shkb.vo.productstorage.CreateProductStorageVo;
import com.lframework.xingyun.shkb.vo.productstorage.QueryProductStorageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "成品出入库管理")
@Validated
@RestController
@RequestMapping("/shkb/product/storage")
public class ProductStorageController extends DefaultBaseController {

  @Autowired
  private ProductStorageService productStorageService;

  @Autowired
  private ProductStorageFileService productStorageFileService;

  /**
   * 成品出入库列表（分页）
   */
  @ApiOperation("成品出入库列表")
  @HasPermission({"product:storage"})
  @GetMapping("/query")
  public InvokeResult<PageResult<QueryProductStorageBo>> query(@Valid QueryProductStorageVo vo) {
    PageResult<ProductStorage> pageResult = productStorageService.query(getPageIndex(vo), getPageSize(vo), vo);

    List<ProductStorage> datas = pageResult.getDatas();
    List<QueryProductStorageBo> results = null;
    if (!CollectionUtil.isEmpty(datas)) {
      results = datas.stream().map(QueryProductStorageBo::new).collect(Collectors.toList());
    }

    return InvokeResultBuilder.success(PageResultUtil.rebuild(pageResult, results));
  }

  /**
   * 新增成品出入库
   */
  @ApiOperation("新增成品出入库")
  @HasPermission({"product:storage"})
  @PostMapping
  public InvokeResult<String> create(@Valid @RequestBody CreateProductStorageVo vo) {
    String id = productStorageService.create(vo);
    return InvokeResultBuilder.success(id);
  }

  /**
   * 成品出入库详情
   */
  @ApiOperation("成品出入库详情")
  @HasPermission({"product:storage"})
  @GetMapping("/{id}")
  public InvokeResult<ProductStorage> get(@NotBlank(message = "ID不能为空") @PathVariable String id) {
    ProductStorage data = productStorageService.findById(id);
    if (data == null) {
      throw new com.lframework.starter.common.exceptions.impl.DefaultClientException("成品出入库不存在");
    }
    return InvokeResultBuilder.success(data);
  }

  /**
   * 修改成品出入库
   */
  @ApiOperation("修改成品出入库")
  @HasPermission({"product:storage"})
  @PutMapping
  public InvokeResult<Void> update(@Valid @RequestBody ProductStorage vo) {
    productStorageService.update(vo);
    return InvokeResultBuilder.success();
  }

  /**
   * 删除成品出入库
   */
  @ApiOperation("删除成品出入库")
  @HasPermission({"product:storage"})
  @DeleteMapping("/{id}")
  public InvokeResult<Void> delete(@NotBlank(message = "ID不能为空") @PathVariable String id) {
    productStorageService.deleteById(id);
    return InvokeResultBuilder.success();
  }

  /**
   * 批量删除成品出入库
   */
  @ApiOperation("批量删除成品出入库")
  @HasPermission({"product:storage"})
  @DeleteMapping("/batch")
  public InvokeResult<Void> batchDelete(
      @ApiParam(value = "ID", required = true) @NotEmpty(message = "请选择需要删除的数据") @RequestParam List<String> ids) {
    productStorageService.deleteByIds(ids);
    return InvokeResultBuilder.success();
  }

  /**
   * 上传客户接收单附件
   */
  @ApiOperation("上传客户接收单附件")
  @ApiImplicitParam(value = "成品出入库ID", name = "productStorageId", paramType = "query", required = true)
  @HasPermission({"product:storage"})
  @PostMapping("/attachment/upload")
  public InvokeResult<List<String>> uploadProductStorageAttachments(
      @NotBlank(message = "成品出入库ID不能为空！") @RequestParam("productStorageId") String productStorageId,
      @RequestParam(value = "files", required = false) List<MultipartFile> files) {

    List<String> fileIds = productStorageFileService.uploadProductStorageFiles(productStorageId, files);
    return InvokeResultBuilder.success(fileIds);
  }

  /**
   * 获取客户接收单附件列表
   */
  @ApiOperation("获取客户接收单附件列表")
  @ApiImplicitParam(value = "成品出入库ID", name = "productStorageId", paramType = "query", required = true)
  @HasPermission({"product:storage"})
  @GetMapping("/attachment/list")
  public InvokeResult<List<ProductStorageFile>> getProductStorageAttachments(
      @NotBlank(message = "成品出入库ID不能为空！") @RequestParam("productStorageId") String productStorageId) {

    List<ProductStorageFile> files = productStorageFileService.getProductStorageFiles(productStorageId);
    return InvokeResultBuilder.success(files);
  }

  /**
   * 删除客户接收单附件
   */
  @ApiOperation("删除客户接收单附件")
  @ApiImplicitParam(value = "附件ID", name = "id", paramType = "path", required = true)
  @HasPermission({"product:storage"})
  @DeleteMapping("/attachment/{id}")
  public InvokeResult<Void> deleteProductStorageAttachment(
      @NotBlank(message = "附件id不能为空！") @PathVariable("id") String id) {

    boolean success = productStorageFileService.deleteProductStorageFile(id);
    if (!success) {
      throw new DefaultClientException("附件不存在或删除失败！");
    }
    return InvokeResultBuilder.success();
  }

  /**
   * 批量删除客户接收单附件
   */
  @ApiOperation("批量删除客户接收单附件")
  @HasPermission({"product:storage"})
  @DeleteMapping("/attachment/batch")
  public InvokeResult<Integer> batchDeleteProductStorageAttachments(@RequestBody List<String> ids) {
    if (CollectionUtil.isEmpty(ids)) {
      throw new DefaultClientException("附件ID列表不能为空！");
    }
    int count = productStorageFileService.batchDeleteProductStorageFiles(ids);
    return InvokeResultBuilder.success(count);
  }
}
