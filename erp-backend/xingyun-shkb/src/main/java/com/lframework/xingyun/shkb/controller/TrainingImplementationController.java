package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.xingyun.shkb.entity.ShkbTrainingImplementation;
import com.lframework.xingyun.shkb.service.ShkbTrainingImplementationService;
import com.lframework.xingyun.shkb.vo.training.CreateTrainingImplementationVo;
import com.lframework.xingyun.shkb.vo.training.QueryShkbTrainingImplementationVo;
import com.lframework.xingyun.shkb.vo.training.UpdateTrainingImplementationVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.time.LocalDate;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.multipart.MultipartFile;

@Api(
   tags = {"培训实施管理"}
)
@Validated
@RestController
@RequestMapping({"/training-implementation"})
public class TrainingImplementationController extends DefaultBaseController {
   @Autowired
   private ShkbTrainingImplementationService implementationService;

   @ApiOperation("查询实施计划列表")
   @HasPermission({"hr:training:query"})
   @PostMapping({"/query"})
   public InvokeResult<PageResult<ShkbTrainingImplementation>> query(@Valid @RequestBody QueryShkbTrainingImplementationVo vo) {
      PageResult<ShkbTrainingImplementation> pageResult = this.implementationService.query(this.getPageIndex(vo), this.getPageSize(vo), vo);
      return InvokeResultBuilder.success(pageResult);
   }

   @ApiOperation("根据课程ID查询实施计划列表")
   @ApiImplicitParam(
      value = "课程ID",
      name = "courseId",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:training:query"})
   @GetMapping({"/course/{courseId}"})
   public InvokeResult<List<ShkbTrainingImplementation>> getByCourseId(@NotBlank(message = "课程ID不能为空") @PathVariable String courseId) {
      List<ShkbTrainingImplementation> datas = this.implementationService.queryByCourseId(courseId);
      return InvokeResultBuilder.success(datas);
   }

   @ApiOperation("查询实施计划详情")
   @ApiImplicitParam(
      value = "ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:training:query"})
   @GetMapping({"/{id}"})
   public InvokeResult<ShkbTrainingImplementation> get(@NotBlank(message = "ID不能为空") @PathVariable String id) {
      ShkbTrainingImplementation data = this.implementationService.findById(id);
      if (data == null) {
         throw new DefaultClientException("实施计划不存在");
      } else {
         return InvokeResultBuilder.success(data);
      }
   }

   @ApiOperation("创建实施计划")
   @HasPermission({"hr:training:create"})
   @PostMapping
   public InvokeResult<String> create(@Valid @RequestBody CreateTrainingImplementationVo vo) {
      String id = this.implementationService
         .create(vo.getCourseId(), vo.getPlanStartDate(), vo.getPlanEndDate(), vo.getTrainingLocation(), vo.getInstructor(), vo.getDescription());
      return InvokeResultBuilder.success(id);
   }

   @ApiOperation("修改实施计划")
   @HasPermission({"hr:training:update"})
   @PutMapping
   public InvokeResult<Void> update(@Valid @RequestBody UpdateTrainingImplementationVo vo) {
      this.implementationService
         .update(vo.getId(), vo.getCourseId(), vo.getPlanStartDate(), vo.getPlanEndDate(), vo.getTrainingLocation(), vo.getInstructor(), vo.getDescription());
      return InvokeResultBuilder.success();
   }

   @ApiOperation("变更状态")
   @HasPermission({"hr:training:update"})
   @PutMapping({"/status"})
   public InvokeResult<Void> changeStatus(@NotBlank(message = "ID不能为空") @RequestParam String id, @RequestParam Integer status) {
      this.implementationService.changeStatus(id, status);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("开始培训")
   @HasPermission({"hr:training:update"})
   @PutMapping({"/start"})
   public InvokeResult<Void> start(
      @NotBlank(message = "ID不能为空") @RequestParam String id, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate actualStartDate
   ) {
      this.implementationService.startWithDate(id, actualStartDate != null ? actualStartDate.atStartOfDay() : null);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("完成培训")
   @HasPermission({"hr:training:update"})
   @PutMapping({"/complete"})
   public InvokeResult<Void> complete(
      @NotBlank(message = "ID不能为空") @RequestParam String id,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate actualEndDate,
      @RequestParam(required = false) String participantResults,
      @RequestParam(required = false) String trainingType,
      @RequestParam(required = false) String trainingOrg,
      @RequestParam(required = false) Integer trainingHours,
      @RequestParam(required = false) String trainingContent,
      @RequestParam(value = "file",required = false) MultipartFile file
   ) {
      this.implementationService
         .completeWithDate(
            id,
            actualEndDate != null ? actualEndDate.atStartOfDay() : null,
            participantResults,
            trainingType,
            trainingOrg,
            trainingHours,
            trainingContent,
            file
         );
      return InvokeResultBuilder.success();
   }

   @ApiOperation("取消培训")
   @HasPermission({"hr:training:update"})
   @PutMapping({"/cancel"})
   public InvokeResult<Void> cancel(@NotBlank(message = "ID不能为空") @RequestParam String id) {
      this.implementationService.cancel(id);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("删除实施计划")
   @ApiImplicitParam(
      value = "ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:training:delete"})
   @DeleteMapping({"/{id}"})
   public InvokeResult<Void> delete(@NotBlank(message = "ID不能为空") @PathVariable String id) {
      this.implementationService.deleteById(id);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("批量删除实施计划")
   @HasPermission({"hr:training:delete"})
   @DeleteMapping({"/batch"})
   public InvokeResult<Void> batchDelete(@ApiParam(value = "ID",required = true) @RequestBody @NotEmpty(message = "请选择需要删除的实施计划") List<String> ids) {
      this.implementationService.deleteByIds(ids);
      return InvokeResultBuilder.success();
   }
}
