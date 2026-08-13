package com.lframework.xingyun.basedata.controller;

import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.utils.ExcelUtil;
import com.lframework.starter.web.core.components.excel.ExcelMultipartWriterSheetBuilder;
import com.lframework.xingyun.basedata.bo.product.info.GetProductBo;
import com.lframework.xingyun.basedata.bo.product.info.QueryProductBo;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.excel.product.ProductImportListener;
import com.lframework.xingyun.basedata.excel.product.ProductAviationBatchUpdateModel;
import com.lframework.xingyun.basedata.excel.product.ProductAviationBatchUpdateListener;
import com.lframework.xingyun.basedata.excel.product.ProductCustomImportModel;
import com.lframework.xingyun.basedata.excel.product.ProductCustomImportListener;
import com.lframework.xingyun.basedata.excel.product.ProductImportModel;
import com.lframework.xingyun.basedata.excel.product.ProductCustomExportModel;
import com.lframework.xingyun.basedata.service.product.ProductBundleService;
import com.lframework.xingyun.basedata.service.product.ProductPropertyRelationService;
import com.lframework.xingyun.basedata.service.product.ProductService;
import com.lframework.xingyun.basedata.vo.product.info.CreateProductVo;
import com.lframework.xingyun.basedata.vo.product.info.QueryProductVo;
import com.lframework.xingyun.basedata.vo.product.info.UpdateProductVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品管理
 *
 * @author zmj
 */
@Api(tags = "商品管理")
@Validated
@RestController
@RequestMapping("/basedata/product")
public class ProductController extends DefaultBaseController {

  @Autowired
  private ProductService productService;

  @Autowired
  private ProductBundleService productBundleService;

  @Autowired
  private ProductPropertyRelationService productPropertyRelationService;

  /**
   * 商品列表
   * <p>
   * 支持按机型ID进行查询过滤（暂停使用件号表）
   * </p>
   */
  @ApiOperation(value = "商品列表", notes = "支持按机型ID进行查询过滤（暂停使用件号表）")
  @HasPermission({"base-data:product:info:query", "base-data:product:info:add",
      "base-data:product:info:modify"})
  @GetMapping("/query")
  public InvokeResult<PageResult<QueryProductBo>> query(@Valid QueryProductVo vo) {

    PageResult<Product> pageResult = productService.query(getPageIndex(vo), getPageSize(vo), vo);

    List<Product> datas = pageResult.getDatas();
    List<QueryProductBo> results = null;

    if (!CollectionUtil.isEmpty(datas)) {

      results = datas.stream().map(QueryProductBo::new).collect(Collectors.toList());
    }

    return InvokeResultBuilder.success(PageResultUtil.rebuild(pageResult, results));
  }

  /**
   * 商品详情
   */
  @ApiOperation("商品详情")
  @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
  @HasPermission({"base-data:product:info:query", "base-data:product:info:add",
      "base-data:product:info:modify"})
  @GetMapping
  public InvokeResult<GetProductBo> get(@NotBlank(message = "ID不能为空！") String id) {

    Product data = productService.findById(id);

    GetProductBo result = new GetProductBo(data);

    return InvokeResultBuilder.success(result);
  }

  /**
   * 新增商品
   */
  @ApiOperation("新增商品")
  @HasPermission({"base-data:product:info:add"})
  @PostMapping
  public InvokeResult<Void> create(@Valid @RequestBody CreateProductVo vo) {

    productService.create(vo);

    return InvokeResultBuilder.success();
  }

  /**
   * 修改商品
   */
  @ApiOperation("修改商品")
  @HasPermission({"base-data:product:info:modify"})
  @PutMapping
  public InvokeResult<Void> update(@Valid @RequestBody UpdateProductVo vo) {

    productService.update(vo);

    productService.cleanCacheByKey(vo.getId());

    productPropertyRelationService.cleanCacheByKey(vo.getId());

    productBundleService.cleanCacheByKey(vo.getId());

    return InvokeResultBuilder.success();
  }

  @ApiOperation("下载导入模板")
  @HasPermission({"base-data:product:info:import"})
  @GetMapping("/import/template")
  public void downloadImportTemplate() {
    ExcelUtil.exportXls("商品导入模板", ProductImportModel.class);
  }

  @ApiOperation("下载航材导入模板")
  @HasPermission({"base-data:product:info:import"})
  @GetMapping("/import/custom/template")
  public void downloadCustomImportTemplate() {
    ExcelUtil.exportXls("航材导入模板", ProductCustomImportModel.class);
  }

  @ApiOperation("导入")
  @HasPermission({"base-data:product:info:import"})
  @PostMapping("/import")
  public InvokeResult<Void> importExcel(@NotBlank(message = "ID不能为空") String id,
      @NotNull(message = "请上传文件") MultipartFile file) {

    ProductImportListener listener = new ProductImportListener();
    listener.setTaskId(id);
    ExcelUtil.read(file, ProductImportModel.class, listener).sheet().doRead();

    return InvokeResultBuilder.success();
  }

  @ApiOperation("航材导入")
  @HasPermission({"base-data:product:info:import"})
  @PostMapping("/import/custom")
  public InvokeResult<Map<String, Object>> importAviation(
      @NotBlank(message = "ID不能为空") String id,
      @NotNull(message = "请上传文件") MultipartFile file) {

    ProductCustomImportListener listener = new ProductCustomImportListener();
    listener.setTaskId(id);
    try {
      ExcelUtil.read(file, ProductCustomImportModel.class, listener).sheet().doRead();
    } catch (org.springframework.transaction.IllegalTransactionStateException ex) {
      // 忽略基类在事务状态已完成时再次回滚抛出的异常，继续返回导入明细
    } catch (Exception ex) {
      // 其他异常也不抛出，统一在明细中体现
    }

    Map<String, Object> data = new HashMap<>();
    data.put("successDetails", listener.getSuccessDetails());
    data.put("failureDetails", listener.getFailureDetails());
    data.put("success", listener.getSuccessDetails().size());
    data.put("failed", listener.getFailureDetails().size());
    data.put("total", listener.getSuccessDetails().size() + listener.getFailureDetails().size());

    return InvokeResultBuilder.success(data);
  }

  /**
   * 航材导出
   */
  @ApiOperation("航材导出")
  @HasPermission({"base-data:product:info:query", "base-data:product:info:add",
      "base-data:product:info:modify"})
  @PostMapping("/export/custom")
  public void exportAviation(@Valid @RequestBody QueryProductVo vo) {

    ExcelMultipartWriterSheetBuilder builder = ExcelUtil.multipartExportXls("航材信息",
        ProductCustomExportModel.class);

    try {
      int pageIndex = 1;
      while (true) {
        PageResult<Product> pageResult = productService.query(pageIndex, com.lframework.starter.web.core.controller.ExportSizeSupport.DEFAULT_EXPORT_SIZE, vo);
        List<Product> datas = pageResult.getDatas();
        List<ProductCustomExportModel> models = datas.stream().map(ProductCustomExportModel::new)
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

  @ApiOperation("下载航材批量修改信息模板")
  @HasPermission({"base-data:product:info:import"})
  @GetMapping("/import/custom/batch-update/template")
  public void downloadAviationBatchUpdateTemplate() {
    ExcelUtil.exportXls("航材批量修改信息模板", ProductAviationBatchUpdateModel.class);
  }

  @ApiOperation("航材批量修改信息导入")
  @HasPermission({"base-data:product:info:import"})
  @PostMapping("/import/custom/batch-update")
  public InvokeResult<Map<String, Object>> importAviationBatchUpdate(
      @NotBlank(message = "ID不能为空") String id,
      @NotNull(message = "请上传文件") MultipartFile file) {

    ProductAviationBatchUpdateListener listener = new ProductAviationBatchUpdateListener();
    listener.setTaskId(id);
    try {
      ExcelUtil.read(file, ProductAviationBatchUpdateModel.class, listener).sheet().doRead();
    } catch (org.springframework.transaction.IllegalTransactionStateException ex) {
      // 忽略基类在事务状态已完成时再次回滚抛出的异常，继续返回导入明细
    } catch (Exception ex) {
      // 其他异常也不抛出，统一在明细中体现
    }

    Map<String, Object> data = new HashMap<>();
    data.put("successDetails", listener.getSuccessDetails());
    data.put("failureDetails", listener.getFailureDetails());
    data.put("success", listener.getSuccessDetails().size());
    data.put("failed", listener.getFailureDetails().size());
    data.put("total", listener.getSuccessDetails().size() + listener.getFailureDetails().size());

    return InvokeResultBuilder.success(data);
  }
}
