package com.lframework.xingyun.sc.controller.stock;

import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.components.excel.ExcelMultipartWriterSheetBuilder;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.ExcelUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.sc.bo.stock.product.QueryProductStockBo;
import com.lframework.xingyun.sc.entity.ProductStock;
import com.lframework.xingyun.sc.excel.stock.ProductStockExportModel;
import com.lframework.xingyun.sc.excel.stock.ProductStockViewExportModel;
import com.lframework.xingyun.sc.service.stock.ProductStockService;
import com.lframework.xingyun.sc.vo.stock.QueryProductStockVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品库存
 *
 * @author zmj
 */
@Api(tags = "商品库存")
@Validated
@RestController
@RequestMapping("/stock/product")
public class ProductStockController extends DefaultBaseController {

  @Autowired
  private ProductStockService productStockService;

  /**
   * 查询商品库存
   */
  @ApiOperation("查询商品库存")
  @HasPermission({"stock:product:query"})
  @GetMapping("/query")
  public InvokeResult<PageResult<QueryProductStockBo>> query(@Valid QueryProductStockVo vo) {

    PageResult<ProductStock> pageResult = productStockService.query(getPageIndex(vo),
        getPageSize(vo), vo);
    List<QueryProductStockBo> results = null;

    List<ProductStock> datas = pageResult.getDatas();
    if (!CollectionUtil.isEmpty(datas)) {
      results = datas.stream().map(QueryProductStockBo::new).collect(Collectors.toList());
    }

    return InvokeResultBuilder.success(PageResultUtil.rebuild(pageResult, results));
  }

  /**
   * 导出商品库存
   */
  @ApiOperation("导出商品库存")
  @HasPermission({"stock:product:export"})
  @GetMapping("/export")
  public void export(@Valid QueryProductStockVo vo) {

    ExcelMultipartWriterSheetBuilder builder = ExcelUtil.multipartExportXls("商品库存信息",
        ProductStockExportModel.class);

    try {
      int pageIndex = 1;
      while (true) {
        PageResult<ProductStock> pageResult = productStockService.query(pageIndex, com.lframework.starter.web.core.controller.ExportSizeSupport.DEFAULT_EXPORT_SIZE,
            vo);
        List<ProductStock> datas = pageResult.getDatas();
        List<ProductStockExportModel> models = datas.stream().map(ProductStockExportModel::new)
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
   * 导出航材库存（按前端列表视图）
   */
  @ApiOperation("导出航材库存（列表视图）")
  @HasPermission({"stock:product:export"})
  @GetMapping("/export-view")
  public void exportView(@Valid QueryProductStockVo vo) {

    ExcelMultipartWriterSheetBuilder builder = ExcelUtil.multipartExportXls("航材库存信息",
        ProductStockViewExportModel.class);

    try {
      int pageIndex = 1;
      while (true) {
        PageResult<ProductStock> pageResult = productStockService.query(pageIndex, com.lframework.starter.web.core.controller.ExportSizeSupport.DEFAULT_EXPORT_SIZE, vo);
        List<ProductStock> datas = pageResult.getDatas();
        List<ProductStockViewExportModel> models = datas.stream()
            .map(ProductStockViewExportModel::new)
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
}
