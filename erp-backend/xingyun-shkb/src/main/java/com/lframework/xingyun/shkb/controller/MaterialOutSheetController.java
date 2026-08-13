package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.starter.web.core.utils.ExcelUtil;
import com.lframework.starter.web.core.components.excel.ExcelMultipartWriterSheetBuilder;
import com.lframework.xingyun.shkb.bo.material.out.GetMaterialOutSheetBo;
import com.lframework.xingyun.shkb.bo.material.out.PrintMaterialOutSheetBo;
import com.lframework.xingyun.shkb.bo.material.out.QueryMaterialOutSheetBo;
import com.lframework.xingyun.shkb.bo.material.out.BatchStockBo;
import com.lframework.xingyun.shkb.bo.material.out.MaterialOutProductBo;
import com.lframework.xingyun.shkb.dto.material.out.MaterialOutSheetFullDto;
import com.lframework.xingyun.shkb.entity.MaterialOutSheet;
import com.lframework.xingyun.shkb.excel.material.out.MaterialOutSheetExportModel;
import com.lframework.xingyun.shkb.service.MaterialOutSheetService;
import com.lframework.xingyun.shkb.vo.material.out.ApprovePassMaterialOutSheetVo;
import com.lframework.xingyun.shkb.vo.material.out.ApproveRefuseMaterialOutSheetVo;
import com.lframework.xingyun.shkb.vo.material.out.CreateMaterialOutSheetVo;
import com.lframework.xingyun.shkb.vo.material.out.QueryMaterialOutSheetVo;
import com.lframework.xingyun.shkb.vo.material.out.UpdateMaterialOutSheetVo;
import com.lframework.xingyun.shkb.bo.material.out.SerialStockBo;
import com.lframework.xingyun.sc.dto.purchase.PurchaseProductDto;
import com.lframework.xingyun.basedata.enums.ProductType;
import com.lframework.xingyun.sc.entity.ProductStock;
import com.lframework.xingyun.sc.service.purchase.PurchaseOrderService;
import com.lframework.xingyun.sc.service.stock.ProductStockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 发料出库单 前端控制器
 * </p>
 *
 * @author kison
 * @since 2025-08-10
 */
@Api(tags = "发料出库单")
@Validated
@RestController
@RequestMapping("/material/out/sheet")
public class MaterialOutSheetController extends DefaultBaseController {

    @Autowired
    private MaterialOutSheetService materialOutSheetService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private ProductStockService productStockService;

    /**
     * 发料出库单列表
     */
    @ApiOperation("发料出库单列表")
    @HasPermission({"material:out"})
    @GetMapping("/query")
    public InvokeResult<PageResult<QueryMaterialOutSheetBo>> query(@Valid QueryMaterialOutSheetVo vo) {
        PageResult<QueryMaterialOutSheetBo> results = materialOutSheetService.queryList(getPageIndex(vo), getPageSize(vo), vo);
        return InvokeResultBuilder.success(results);
    }

    /**
     * 根据关键字查询发料出库可选航材。
     */
    @ApiOperation("根据关键字查询发料出库可选航材")
    @HasPermission({"material:out"})
    @GetMapping("/product/search")
    public InvokeResult<List<MaterialOutProductBo>> searchProducts(
            @NotBlank(message = "仓库ID不能为空！") String scId, String condition) {

        if (StringUtil.isBlank(condition)) {
            return InvokeResultBuilder.success(CollectionUtil.emptyList());
        }

        PageResult<PurchaseProductDto> pageResult = purchaseOrderService.queryPurchaseByCondition(
                getPageIndex(), getPageSize(), condition);
        List<MaterialOutProductBo> results = CollectionUtil.emptyList();
        if (!CollectionUtil.isEmpty(pageResult.getDatas())) {
            List<String> productIds = pageResult.getDatas().stream()
                    .map(PurchaseProductDto::getId)
                    .collect(Collectors.toList());
            Map<String, Integer> stockNums = productStockService
                    .getByProductIdsAndScId(productIds, scId, ProductType.NORMAL.getCode())
                    .stream()
                    .collect(Collectors.toMap(ProductStock::getProductId,
                            item -> item.getStockNum() == null ? 0 : item.getStockNum(),
                            Integer::sum));

            results = pageResult.getDatas().stream()
                    .map(item -> new MaterialOutProductBo(
                            item, stockNums.getOrDefault(item.getId(), 0)))
                    .collect(Collectors.toList());
        }

        return InvokeResultBuilder.success(results);
    }

    /**
     * 导出
     */
    @ApiOperation("导出")
    @HasPermission({"material:out"})
    @PostMapping("/export")
    public void export(@Valid @RequestBody QueryMaterialOutSheetVo vo) {
        ExcelMultipartWriterSheetBuilder builder = ExcelUtil.multipartExportXls("发料出库单信息",
                MaterialOutSheetExportModel.class);

        try {
            int pageIndex = 1;
            while (true) {
                PageResult<QueryMaterialOutSheetBo> pageResult = materialOutSheetService.queryList(pageIndex, com.lframework.starter.web.core.controller.ExportSizeSupport.DEFAULT_EXPORT_SIZE, vo);
                List<QueryMaterialOutSheetBo> datas = pageResult.getDatas();
                List<MaterialOutSheetExportModel> models = datas.stream().map(MaterialOutSheetExportModel::new)
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
     * 根据ID查询
     */
    @ApiOperation("根据ID查询")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
    @HasPermission({"material:out"})
    @GetMapping
    public InvokeResult<GetMaterialOutSheetBo> findById(@NotBlank(message = "发料出库单ID不能为空！") String id) {

        MaterialOutSheetFullDto data = materialOutSheetService.getDetail(id);

        return InvokeResultBuilder.success(new GetMaterialOutSheetBo(data));
    }

    /**
     * 打印
     */
    @ApiOperation("打印")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
    @HasPermission({"material:out"})
    @GetMapping("/print")
    public PrintMaterialOutSheetBo print(@NotBlank(message = "发料出库单ID不能为空！") String id) {

        MaterialOutSheetFullDto data = materialOutSheetService.getDetail(id);

        return new PrintMaterialOutSheetBo(data);
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @HasPermission({"material:out"})
    @PostMapping
    public InvokeResult<String> create(@Valid @RequestBody CreateMaterialOutSheetVo vo) {

        vo.validate();

        String id = materialOutSheetService.create(vo);

        return InvokeResultBuilder.success(id);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @HasPermission({"material:out"})
    @PutMapping
    public InvokeResult<Void> update(@Valid @RequestBody UpdateMaterialOutSheetVo vo) {

        vo.validate();

        materialOutSheetService.update(vo);
        return InvokeResultBuilder.success();
    }

    /**
     * 发料
     */
    @ApiOperation("发料")
    @HasPermission({"material:out"})
    @PatchMapping("/approve/pass")
    public InvokeResult<Void> approvePass(@Valid @RequestBody ApprovePassMaterialOutSheetVo vo) {

        materialOutSheetService.approvePass(vo);
        return InvokeResultBuilder.success();
    }

    /**
     * 批量发料
     */
    @ApiOperation("批量发料")
    @HasPermission({"material:out"})
    @PatchMapping("/approve/pass/batch")
    public InvokeResult<Void> batchApprovePass(
            @NotEmpty(message = "发料出库单ID不能为空！") @RequestBody List<String> ids) {

        if (CollectionUtil.isEmpty(ids)) {
            throw new DefaultClientException("发料出库单ID不能为空！");
        }

        for (String id : ids) {
            ApprovePassMaterialOutSheetVo vo = new ApprovePassMaterialOutSheetVo();
            vo.setId(id);

            materialOutSheetService.approvePass(vo);
        }
        return InvokeResultBuilder.success();

    }

    /**
     * 直接审核通过
     */
    @ApiOperation("直接审核通过")
    @HasPermission({"material:out", "material:out:sheet:approve"})
    @PostMapping("/direct/approve/pass")
    public InvokeResult<String> directApprovePass(@Valid @RequestBody CreateMaterialOutSheetVo vo) {

        vo.validate();

        materialOutSheetService.directApprovePass(vo);

        String id = vo.getId();
        return InvokeResultBuilder.success(id);
    }

    /**
     * 可领料
     */
    @ApiOperation("可领料")
    @HasPermission({"material:out"})
    @PatchMapping("/mark/pickable")
    public InvokeResult<Void> markPickable(@Valid @RequestBody ApproveRefuseMaterialOutSheetVo vo) {

        materialOutSheetService.markPickable(vo);
        return InvokeResultBuilder.success();
    }

    /**
     * 批量可领料
     */
    @ApiOperation("批量可领料")
    @HasPermission({"material:out"})
    @PatchMapping("/mark/pickable/batch")
    public InvokeResult<Void> batchMarkPickable(@Valid @RequestBody ApproveRefuseMaterialOutSheetVo vo) {

        materialOutSheetService.markPickable(vo);
        return InvokeResultBuilder.success();
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
    @HasPermission({"material:out"})
    @DeleteMapping
    public InvokeResult<Void>  deleteById(@NotBlank(message = "发料出库单ID不能为空！") String id) {

        materialOutSheetService.deleteById(id);
        return InvokeResultBuilder.success();
    }

    /**
     * 批量删除
     */
    @ApiOperation("批量删除")
    @HasPermission({"material:out"})
    @DeleteMapping("/batch")
    public InvokeResult<Void>  deleteByIds(
            @NotEmpty(message = "发料出库单ID不能为空！") @RequestBody List<String> ids) {

        if (CollectionUtil.isEmpty(ids)) {
            throw new DefaultClientException("发料出库单ID不能为空！");
        }

        for (String id : ids) {
            materialOutSheetService.deleteById(id);
        }
        return InvokeResultBuilder.success();
    }

    /**
     * 查询批次库存列表
     */
    @ApiOperation("查询批次库存列表")
    @HasPermission({"material:out"})
    @GetMapping("/batch/stock")
    public InvokeResult<List<BatchStockBo>> queryBatchStock(
            @NotBlank(message = "仓库ID不能为空！") @RequestParam String scId,
            @NotBlank(message = "商品ID不能为空！") @RequestParam String productId) {
        List<BatchStockBo> results = materialOutSheetService.queryBatchStock(scId, productId);
        return InvokeResultBuilder.success(results);
    }

    /**
     * 查询序列号库存列表
     */
    @ApiOperation("查询序列号库存列表")
    @HasPermission({"material:out"})
    @GetMapping("/serial/stock")
    public InvokeResult<List<SerialStockBo>> querySerialStock(
            @NotBlank(message = "仓库ID不能为空！") @RequestParam String scId,
            @NotBlank(message = "商品ID不能为空！") @RequestParam String productId) {

        List<SerialStockBo> results = materialOutSheetService.querySerialStock(scId, productId);
        return InvokeResultBuilder.success(results);
    }
}
