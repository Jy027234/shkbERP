package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.components.excel.ExcelMultipartWriterSheetBuilder;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.ExcelUtil;
import com.lframework.xingyun.shkb.entity.ShkbTrainingCourse;
import com.lframework.xingyun.shkb.entity.ShkbTrainingCourseFile;
import com.lframework.xingyun.shkb.excel.training.TrainingCourseExportModel;
import com.lframework.xingyun.shkb.service.ShkbTrainingCourseFileService;
import com.lframework.xingyun.shkb.service.ShkbTrainingCourseService;
import com.lframework.xingyun.shkb.vo.training.CreateShkbTrainingCourseVo;
import com.lframework.xingyun.shkb.vo.training.QueryShkbTrainingCourseVo;
import com.lframework.xingyun.shkb.vo.training.UpdateShkbTrainingCourseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
import org.springframework.web.multipart.MultipartFile;

@Api(
   tags = {"培训课程管理"}
)
@Validated
@RestController
@RequestMapping({"/training-course"})
public class TrainingCourseController extends DefaultBaseController {
   @Autowired
   private ShkbTrainingCourseService trainingCourseService;
   @Autowired
   private ShkbTrainingCourseFileService trainingCourseFileService;

   @ApiOperation("查询培训课程列表")
   @HasPermission({"hr:training:query", "hr:training:create"})
   @PostMapping({"/query"})
   public InvokeResult<PageResult<ShkbTrainingCourse>> query(@Valid @RequestBody QueryShkbTrainingCourseVo vo) {
      PageResult<ShkbTrainingCourse> pageResult = this.trainingCourseService.query(this.getPageIndex(vo), this.getPageSize(vo), vo);
      return InvokeResultBuilder.success(pageResult);
   }

   @ApiOperation("查询所有启用的课程")
   @HasPermission({"hr:training:query"})
   @GetMapping({"/list/enabled"})
   public InvokeResult<List<ShkbTrainingCourse>> queryEnabled() {
      List<ShkbTrainingCourse> datas = this.trainingCourseService.queryByStatus(1);
      return InvokeResultBuilder.success(datas);
   }

   @ApiOperation("获取所有有效课程")
   @HasPermission({"hr:training:query"})
   @GetMapping({"/all-valid"})
   public InvokeResult<List<ShkbTrainingCourse>> getAllValid() {
      List<ShkbTrainingCourse> datas = this.trainingCourseService.queryByStatus(1);
      return InvokeResultBuilder.success(datas);
   }

   @ApiOperation("查询课程详情")
   @ApiImplicitParam(
      value = "ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:training:query"})
   @GetMapping({"/{id}"})
   public InvokeResult<ShkbTrainingCourse> get(@NotBlank(message = "ID不能为空") @PathVariable String id) {
      ShkbTrainingCourse data = this.trainingCourseService.findById(id);
      if (data == null) {
         throw new DefaultClientException("课程不存在");
      } else {
         return InvokeResultBuilder.success(data);
      }
   }

   @ApiOperation("根据ID列表加载课程")
   @HasPermission({"hr:training:query"})
   @PostMapping({"/load"})
   public InvokeResult<List<ShkbTrainingCourse>> loadByIds(@RequestBody List<String> ids) {
      List<ShkbTrainingCourse> datas = this.trainingCourseService.findByIds(ids);
      return InvokeResultBuilder.success(datas);
   }

   @ApiOperation("创建课程")
   @HasPermission({"hr:training:create"})
   @PostMapping
   public InvokeResult<Void> create(@Valid @RequestBody CreateShkbTrainingCourseVo vo) {
      this.trainingCourseService.create(vo);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("修改课程")
   @HasPermission({"hr:training:update"})
   @PutMapping
   public InvokeResult<Void> update(@Valid @RequestBody UpdateShkbTrainingCourseVo vo) {
      this.trainingCourseService.update(vo);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("启用/禁用课程")
   @HasPermission({"hr:training:update"})
   @PutMapping({"/status"})
   public InvokeResult<Void> changeStatus(@NotBlank(message = "ID不能为空") @RequestParam String id, @RequestParam Integer status) {
      this.trainingCourseService.updateStatus(id, status);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("删除课程")
   @ApiImplicitParam(
      value = "ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:training:delete"})
   @DeleteMapping({"/{id}"})
   public InvokeResult<Void> delete(@NotBlank(message = "ID不能为空") @PathVariable String id) {
      this.trainingCourseService.deleteById(id);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("批量删除课程")
   @HasPermission({"hr:training:delete"})
   @DeleteMapping({"/batch"})
   public InvokeResult<Void> batchDelete(@ApiParam(value = "ID",required = true) @RequestBody @NotEmpty(message = "请选择需要删除的课程") List<String> ids) {
      this.trainingCourseService.deleteByIds(ids);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("查询课程文档列表")
   @GetMapping({"/file/list"})
   public InvokeResult<List<ShkbTrainingCourseFile>> getFileList(@RequestParam String courseId) {
      List<ShkbTrainingCourseFile> files = this.trainingCourseFileService.queryByCourseId(courseId);
      return InvokeResultBuilder.success(files);
   }

   @ApiOperation("上传课程文档")
   @HasPermission({"hr:training:update"})
   @PostMapping({"/file/upload"})
   public InvokeResult<Void> uploadFile(@RequestParam String courseId, @RequestParam MultipartFile file, @RequestParam(required = false) String description) {
      this.trainingCourseFileService.upload(courseId, file, description);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("删除课程文档")
   @DeleteMapping({"/file/{id}"})
   public InvokeResult<Void> deleteFile(@NotBlank(message = "ID不能为空") @PathVariable String id) {
      this.trainingCourseFileService.deleteById(id);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("批量删除课程文档")
   @DeleteMapping({"/file/batch"})
   public InvokeResult<Void> batchDeleteFile(@RequestBody @NotEmpty(message = "请选择需要删除的文档") List<String> ids) {
      this.trainingCourseFileService.deleteByIds(ids);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("导出培训课程列表")
   @PostMapping({"/export"})
   public void export(@Valid @RequestBody QueryShkbTrainingCourseVo vo) {
      ExcelMultipartWriterSheetBuilder builder = ExcelUtil.multipartExportXls("培训课程", TrainingCourseExportModel.class);

      try {
         int pageIndex = 1;

         while (true) {
            PageResult<ShkbTrainingCourse> pageResult = this.trainingCourseService.query(pageIndex, com.lframework.starter.web.core.controller.ExportSizeSupport.DEFAULT_EXPORT_SIZE, vo);
            List<ShkbTrainingCourse> datas = pageResult.getDatas();
            List<TrainingCourseExportModel> models = datas.stream().map(TrainingCourseExportModel::new).collect(Collectors.toList());
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
