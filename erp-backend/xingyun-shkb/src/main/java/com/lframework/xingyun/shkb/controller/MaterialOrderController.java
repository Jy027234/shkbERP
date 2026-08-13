package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.components.excel.ExcelMultipartWriterSheetBuilder;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.ExcelUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.core.bo.print.A4ExcelPortraitPrintBo;
import com.lframework.xingyun.shkb.bo.material.GetMaterialOrderBo;
import com.lframework.xingyun.shkb.bo.material.PrintMaterialOrderBo;
import com.lframework.xingyun.shkb.bo.material.QueryMaterialOrderBo;
import com.lframework.xingyun.shkb.dto.material.MaterialOrderFullDto;
import com.lframework.xingyun.shkb.entity.MaterialOrder;
import com.lframework.xingyun.shkb.excel.material.MaterialOrderExportModel;
import com.lframework.xingyun.shkb.service.MaterialOrderService;
import com.lframework.xingyun.shkb.vo.material.CreateMaterialOrderFromApplyVo;
import com.lframework.xingyun.shkb.vo.material.QueryMaterialOrderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 发料单管理
 *
 * @author kison
 */
@Api(tags = "发料单管理")
@Validated
@RestController
@RequestMapping("/material/order")
public class MaterialOrderController extends DefaultBaseController {

    @Autowired
    private MaterialOrderService materialOrderService;

    /**
     * 发料单列表
     */
    @ApiOperation("发料单列表")
    @HasPermission({"material:order"})
    @GetMapping("/query")
    public InvokeResult<PageResult<QueryMaterialOrderBo>> query(@Valid QueryMaterialOrderVo vo) {

        PageResult<QueryMaterialOrderBo> pageResult = materialOrderService.query(getPageIndex(vo), getPageSize(vo), vo);

        return InvokeResultBuilder.success(pageResult);
    }

    /**
     * 根据ID查询发料单详情
     */
    @ApiOperation("根据ID查询发料单详情")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
    @HasPermission({"material:order"})
    @GetMapping
    public InvokeResult<GetMaterialOrderBo> findById(@NotBlank(message = "发料单ID不能为空！") String id) {

        MaterialOrderFullDto data = materialOrderService.getDetail(id);
        if (data == null) {
            throw new DefaultClientException("发料单不存在！");
        }

        GetMaterialOrderBo result = new GetMaterialOrderBo(data);

        return InvokeResultBuilder.success(result);
    }

    /**
     * 导出发料单
     */
    @ApiOperation("导出发料单")
    @HasPermission({"material:order"})
    @PostMapping("/export")
    public void export(@Valid QueryMaterialOrderVo vo) {

        ExcelMultipartWriterSheetBuilder builder = ExcelUtil.multipartExportXls("发料单信息",
                MaterialOrderExportModel.class);

        try {
            int pageIndex = 1;
            while (true) {
                PageResult<QueryMaterialOrderBo> pageResult = materialOrderService.query(pageIndex, com.lframework.starter.web.core.controller.ExportSizeSupport.DEFAULT_EXPORT_SIZE, vo);
                List<QueryMaterialOrderBo> datas = pageResult.getDatas();
                List<MaterialOrderExportModel> models = datas.stream().map(MaterialOrderExportModel::new)
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
     * 打印发料单
     */
    @ApiOperation("打印发料单")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
    @HasPermission({"material:order"})
    @GetMapping("/print")
    public InvokeResult<A4ExcelPortraitPrintBo<PrintMaterialOrderBo>> print(
            @NotBlank(message = "发料单ID不能为空！") String id) {

        MaterialOrderFullDto data = materialOrderService.getDetail(id);
        if (data == null) {
            throw new DefaultClientException("发料单不存在！");
        }

        PrintMaterialOrderBo result = new PrintMaterialOrderBo(data);

        A4ExcelPortraitPrintBo<PrintMaterialOrderBo> printResult = new A4ExcelPortraitPrintBo<>(
                "print/material-order.ftl",
                result);

        return InvokeResultBuilder.success(printResult);
    }

    /**
     * 基于发料申请单创建发料单
     */
    @ApiOperation("基于发料申请单创建发料单")
    @HasPermission({"material:order"})
    @PostMapping
    public InvokeResult<String> createFromApply(@RequestBody @Valid CreateMaterialOrderFromApplyVo vo) {

        String id = materialOrderService.createFromApply(vo);

        return InvokeResultBuilder.success(id);
    }
}
