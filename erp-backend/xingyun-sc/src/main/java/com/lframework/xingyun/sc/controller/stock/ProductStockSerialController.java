package com.lframework.xingyun.sc.controller.stock;

import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.components.excel.ExcelMultipartWriterSheetBuilder;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.ExcelUtil;
import com.lframework.xingyun.sc.bo.stock.serial.GetProductStockSerialBo;
import com.lframework.xingyun.sc.bo.stock.serial.QueryProductStockSerialBo;
import com.lframework.xingyun.sc.entity.ProductStockSerial;
import com.lframework.xingyun.sc.excel.stock.ProductStockSerialViewExportModel;
import com.lframework.xingyun.sc.service.stock.ProductStockSerialService;
import com.lframework.xingyun.sc.vo.stock.serial.QueryProductStockSerialVo;
import com.lframework.xingyun.sc.vo.stock.serial.UpdateProductStockSerialVo;
import com.lframework.xingyun.sc.vo.stock.serial.UpdateProductStockSerialNumberVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商品序列号库存
 *
 * @author kison
 */
@Api(tags = "商品序列号库存")
@Validated
@RestController
@RequestMapping("/stock/product/serial")
public class ProductStockSerialController extends DefaultBaseController {

    @Autowired
    private ProductStockSerialService productStockSerialService;

    /**
     * 查询商品序列号库存
     */
    @ApiOperation("查询商品序列号库存")
    @HasPermission({"stock:product-serial"})
    @GetMapping("/query")
    public InvokeResult<PageResult<QueryProductStockSerialBo>> query(@Valid QueryProductStockSerialVo vo) {
        // 直接返回Mapper查询结果，无需手动转换
        PageResult<QueryProductStockSerialBo> pageResult = productStockSerialService.query(getPageIndex(vo),
                getPageSize(vo), vo);
        return InvokeResultBuilder.success(pageResult);
    }

    /**
     * 根据ID查询商品序列号库存
     */
    @ApiOperation("根据ID查询商品序列号库存")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "path", required = true)
    @HasPermission({"stock:product-serial"})
    @GetMapping("/{id}")
    public InvokeResult<GetProductStockSerialBo> get(@PathVariable String id) {

        ProductStockSerial data = productStockSerialService.findById(id);
        if (data == null) {
            return InvokeResultBuilder.success(null);
        }

        GetProductStockSerialBo result = new GetProductStockSerialBo(data);

        return InvokeResultBuilder.success(result);
    }

    /**
     * 修改商品序列号库存信息
     */
    @ApiOperation("修改商品序列号库存信息")
    @HasPermission({"stock:product-serial"})
    @PostMapping("/modify")
    public InvokeResult<Void> updateInfo(@RequestBody @Valid UpdateProductStockSerialVo vo) {

        productStockSerialService.updateInfo(vo);

        return InvokeResultBuilder.success();
    }

    /**
     * 修改商品序列号
     */
    @ApiOperation("修改商品序列号")
    @HasPermission({"stock:product-serial:modify"})
    @PostMapping("/modify-serial-number")
    public InvokeResult<Void> updateSerialNumber(@RequestBody @Valid UpdateProductStockSerialNumberVo vo) {

        productStockSerialService.updateSerialNumber(vo);

        return InvokeResultBuilder.success();
    }

    /**
     * 导出航材序列号库存（按前端列表视图）
     */
    @ApiOperation("导出航材序列号库存（列表视图）")
    @HasPermission({"stock:product-serial"})
    @GetMapping("/export-view")
    public void exportView(@Valid QueryProductStockSerialVo vo) {

        ExcelMultipartWriterSheetBuilder builder = ExcelUtil.multipartExportXls("航材序列号库存信息",
                ProductStockSerialViewExportModel.class);

        try {
            int pageIndex = 1;
            while (true) {
                PageResult<QueryProductStockSerialBo> pageResult = productStockSerialService.query(pageIndex, com.lframework.starter.web.core.controller.ExportSizeSupport.DEFAULT_EXPORT_SIZE, vo);
                java.util.List<QueryProductStockSerialBo> datas = pageResult.getDatas();
                java.util.List<ProductStockSerialViewExportModel> models = datas.stream()
                        .map(ProductStockSerialViewExportModel::new)
                        .collect(java.util.stream.Collectors.toList());
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
