package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.components.excel.ExcelMultipartWriterSheetBuilder;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.ExcelUtil;
import com.lframework.xingyun.shkb.bo.training.GetShkbEmployeeTrainingBo;
import com.lframework.xingyun.shkb.bo.training.QueryShkbEmployeeTrainingBo;
import com.lframework.xingyun.shkb.bo.training.TrainingStatisticsBo;
import com.lframework.xingyun.shkb.entity.ShkbEmployeeTraining;
import com.lframework.xingyun.shkb.excel.training.TrainingRecordExportModel;
import com.lframework.xingyun.shkb.service.ShkbEmployeeTrainingService;
import com.lframework.xingyun.shkb.vo.employee.CreateShkbEmployeeTrainingVo;
import com.lframework.xingyun.shkb.vo.employee.QueryShkbEmployeeTrainingVo;
import com.lframework.xingyun.shkb.vo.employee.UpdateShkbEmployeeTrainingVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(
   tags = {"员工培训记录管理"}
)
@Validated
@RestController
@RequestMapping({"/shkb/employee-training"})
public class ShkbEmployeeTrainingController extends DefaultBaseController {
   @Autowired
   private ShkbEmployeeTrainingService trainingService;

   @ApiOperation("查询员工培训记录列表")
   @HasPermission({"hr:training:query"})
   @PostMapping({"/query"})
   public InvokeResult<PageResult<QueryShkbEmployeeTrainingBo>> query(@Valid @RequestBody QueryShkbEmployeeTrainingVo vo) {
      PageResult<QueryShkbEmployeeTrainingBo> pageResult = this.trainingService.query(this.getPageIndex(vo), this.getPageSize(vo), vo);
      return InvokeResultBuilder.success(pageResult);
   }

   @ApiOperation("根据员工ID查询培训记录列表")
   @ApiImplicitParam(
      value = "员工ID",
      name = "employeeId",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:training:query"})
   @GetMapping({"/employee/{employeeId}"})
   public InvokeResult<List<ShkbEmployeeTraining>> getByEmployeeId(@NotBlank(message = "员工ID不能为空") @PathVariable String employeeId) {
      List<ShkbEmployeeTraining> datas = this.trainingService.queryByEmployeeId(employeeId);
      return InvokeResultBuilder.success(datas);
   }

   @ApiOperation("查询培训记录详情")
   @ApiImplicitParam(
      value = "ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:training:query"})
   @GetMapping({"/{id}"})
   public InvokeResult<GetShkbEmployeeTrainingBo> get(@NotBlank(message = "ID不能为空") @PathVariable String id) {
      GetShkbEmployeeTrainingBo data = this.trainingService.getById(id);
      if (data == null) {
         throw new DefaultClientException("培训记录不存在");
      } else {
         return InvokeResultBuilder.success(data);
      }
   }

   @ApiOperation("创建培训记录")
   @HasPermission({"hr:training:create"})
   @PostMapping
   public InvokeResult<Void> create(@Valid @RequestBody CreateShkbEmployeeTrainingVo vo) {
      this.trainingService.create(vo);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("修改培训记录")
   @HasPermission({"hr:training:update"})
   @PutMapping
   public InvokeResult<Void> update(@Valid @RequestBody UpdateShkbEmployeeTrainingVo vo) {
      this.trainingService.update(vo);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("删除培训记录")
   @ApiImplicitParam(
      value = "ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:training:delete"})
   @DeleteMapping({"/{id}"})
   public InvokeResult<Void> delete(@NotBlank(message = "ID不能为空") @PathVariable String id) {
      this.trainingService.deleteById(id);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("批量删除培训记录")
   @HasPermission({"hr:training:delete"})
   @DeleteMapping({"/batch"})
   public InvokeResult<Void> batchDelete(@ApiParam(value = "ID",required = true) @NotEmpty(message = "请选择需要删除的培训记录") @RequestParam List<String> ids) {
      this.trainingService.deleteByIds(ids);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("获取培训统计")
   @HasPermission({"hr:training:query"})
   @GetMapping({"/statistics"})
   public InvokeResult<TrainingStatisticsBo> getStatistics() {
      TrainingStatisticsBo statistics = this.trainingService.getStatistics();
      return InvokeResultBuilder.success(statistics);
   }

   @ApiOperation("导出培训记录")
   @HasPermission({"hr:training:query"})
   @PostMapping({"/export"})
   public void export(@Valid @RequestBody QueryShkbEmployeeTrainingVo vo) {
      ExcelMultipartWriterSheetBuilder builder = ExcelUtil.multipartExportXls("培训记录", TrainingRecordExportModel.class);

      try {
         if (vo.getIds() != null && !vo.getIds().trim().isEmpty()) {
            List<String> idList = Arrays.stream(vo.getIds().split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
            if (!idList.isEmpty()) {
               List<QueryShkbEmployeeTrainingBo> trainings = this.trainingService.query(1, idList.size(), vo).getDatas();
               List<TrainingRecordExportModel> models = trainings.stream().map(TrainingRecordExportModel::new).collect(Collectors.toList());
               builder.doWrite(models);
               return;
            }
         }

         int pageIndex = 1;

         while (true) {
            PageResult<QueryShkbEmployeeTrainingBo> pageResult = this.trainingService.query(pageIndex, com.lframework.starter.web.core.controller.ExportSizeSupport.DEFAULT_EXPORT_SIZE, vo);
            List<QueryShkbEmployeeTrainingBo> datas = pageResult.getDatas();
            List<TrainingRecordExportModel> models = datas.stream().map(TrainingRecordExportModel::new).collect(Collectors.toList());
            builder.doWrite(models);
            if (!pageResult.isHasNext()) {
               return;
            }

            pageIndex++;
         }
      } finally {
         builder.finish();
      }
   }
}
