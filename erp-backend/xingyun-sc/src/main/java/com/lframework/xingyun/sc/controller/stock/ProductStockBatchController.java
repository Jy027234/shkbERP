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
import com.lframework.xingyun.sc.bo.stock.batch.QueryProductStockBatchBo;
import com.lframework.xingyun.sc.bo.stock.batch.GetProductStockBatchBo;
import com.lframework.xingyun.sc.excel.stock.ProductStockBatchViewExportModel;
import com.lframework.xingyun.sc.entity.ProductStockBatch;
import com.lframework.xingyun.sc.service.stock.ProductStockBatchService;
import com.lframework.xingyun.sc.vo.stock.batch.QueryProductStockBatchVo;
import com.lframework.xingyun.sc.vo.stock.batch.UpdateProductStockBatchVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商品批次库存
 *
 * @author kison
 */
@Api(tags = "商品批次库存")
@Validated
@RestController
@RequestMapping("/stock/product/batch")
public class ProductStockBatchController extends DefaultBaseController {

    @Autowired
    private ProductStockBatchService productStockBatchService;

    /**
     * 查询商品批次库存
     */
    @ApiOperation("查询商品批次库存")
    @HasPermission({"stock:product-batch"})
    @GetMapping("/query")
    public InvokeResult<PageResult<QueryProductStockBatchBo>> query(@Valid QueryProductStockBatchVo vo) {

        PageResult<ProductStockBatch> pageResult = productStockBatchService.query(getPageIndex(vo),
                getPageSize(vo), vo);
        List<QueryProductStockBatchBo> results = null;

        List<ProductStockBatch> datas = pageResult.getDatas();
        if (!CollectionUtil.isEmpty(datas)) {
            results = datas.stream().map(QueryProductStockBatchBo::new).collect(Collectors.toList());
        }

        return InvokeResultBuilder.success(PageResultUtil.rebuild(pageResult, results));
    }

    /**
     * 根据ID查询商品批次库存
     */
    @ApiOperation("根据ID查询商品批次库存")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "path", required = true)
    @HasPermission({"stock:product-batch"})
    @GetMapping("/{id}")
    public InvokeResult<GetProductStockBatchBo> get(@PathVariable String id) {

        ProductStockBatch data = productStockBatchService.findById(id);
        if (data == null) {
            return InvokeResultBuilder.success(null);
        }

        GetProductStockBatchBo result = new GetProductStockBatchBo(data);

        return InvokeResultBuilder.success(result);
    }

    /**
     * 修改商品批次库存信息
     */
    @ApiOperation("修改商品批次库存信息")
    @HasPermission({"stock:product-batch"})
    @PostMapping("/modify")
    public InvokeResult<Void> updateInfo(@RequestBody @Valid UpdateProductStockBatchVo vo) {

        productStockBatchService.updateInfo(vo);

        return InvokeResultBuilder.success();
    }

    /**
     * 导出航材批次库存（按前端列表视图）
     */
    @ApiOperation("导出航材批次库存（列表视图）")
    @HasPermission({"stock:product-batch"})
    @GetMapping("/export-view")
    public void exportView(@Valid QueryProductStockBatchVo vo) {

        ExcelMultipartWriterSheetBuilder builder = ExcelUtil.multipartExportXls("航材批次库存信息",
                ProductStockBatchViewExportModel.class);

        try {
            int pageIndex = 1;
            while (true) {
                PageResult<ProductStockBatch> pageResult = productStockBatchService.query(pageIndex, com.lframework.starter.web.core.controller.ExportSizeSupport.DEFAULT_EXPORT_SIZE, vo);
                List<ProductStockBatch> datas = pageResult.getDatas();
                List<ProductStockBatchViewExportModel> models = datas.stream()
                        .map(ProductStockBatchViewExportModel::new)
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
