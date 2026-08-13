package com.lframework.xingyun.sc.controller.imports;

import com.lframework.starter.web.core.utils.ExcelUtil;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.xingyun.sc.service.StockInitImportFlowService;
import com.lframework.xingyun.sc.excel.stock.StockInitImportModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "库存初始化导入")
@Validated
@RestController
@RequestMapping("/stock/init/import")
public class StockInitImportController extends DefaultBaseController {

  @Autowired
  private StockInitImportFlowService flowService;

  @ApiOperation("预检导入（不落库）")
  @HasPermission({"stock:init-import"})
  @PostMapping("/precheck")
  public InvokeResult<Map<String, Object>> precheck(
      @NotBlank(message = "任务ID不能为空") String id,
      @NotNull(message = "请上传文件") MultipartFile file,
      Boolean initOnly) {

    Map<String, Object> data = flowService.precheck(id, file, initOnly != null && initOnly);
    return InvokeResultBuilder.success(data);
  }

  @ApiOperation("执行导入（按批次，仅处理未成功项）")
  @HasPermission({"stock:init-import"})
  @PostMapping("/execute")
  public InvokeResult<Map<String, Object>> execute(
      @NotBlank(message = "批次ID不能为空") String batchId,
      @NotBlank(message = "任务ID不能为空") String id,
      Boolean initOnly) {

    Map<String, Object> data = flowService.execute(batchId, id, initOnly != null && initOnly);
    return InvokeResultBuilder.success(data);
  }

  /**
   * 下载库存导入模板
   */
  @ApiOperation("下载库存导入模板")
  @HasPermission({"stock:init-import"})
  @GetMapping("/template")
  public void downloadInitImportTemplate() {
    ExcelUtil.exportXls("库存导入模板", StockInitImportModel.class);
  }
}
