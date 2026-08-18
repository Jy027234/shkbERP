package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.components.excel.ExcelMultipartWriterSheetBuilder;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.ExcelUtil;
import com.lframework.xingyun.shkb.bo.certificate.CertificateStatisticsBo;
import com.lframework.xingyun.shkb.bo.certificate.GetShkbEmployeeCertificateBo;
import com.lframework.xingyun.shkb.bo.certificate.QueryShkbEmployeeCertificateBo;
import com.lframework.xingyun.shkb.entity.ShkbEmployeeCertificate;
import com.lframework.xingyun.shkb.excel.certificate.CertificateExportModel;
import com.lframework.xingyun.shkb.service.ShkbEmployeeCertificateService;
import com.lframework.xingyun.shkb.vo.employee.CreateShkbEmployeeCertificateVo;
import com.lframework.xingyun.shkb.vo.employee.QueryShkbEmployeeCertificateVo;
import com.lframework.xingyun.shkb.vo.employee.UpdateShkbEmployeeCertificateVo;
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
   tags = {"员工证书管理"}
)
@Validated
@RestController
@RequestMapping({"/shkb/employee-certificate"})
public class ShkbEmployeeCertificateController extends DefaultBaseController {
   @Autowired
   private ShkbEmployeeCertificateService certificateService;

   @ApiOperation("查询员工证书列表")
   @HasPermission({"hr:certificate:query"})
   @PostMapping({"/query"})
   public InvokeResult<PageResult<QueryShkbEmployeeCertificateBo>> query(@Valid @RequestBody QueryShkbEmployeeCertificateVo vo) {
      PageResult<QueryShkbEmployeeCertificateBo> pageResult = this.certificateService.query(this.getPageIndex(vo), this.getPageSize(vo), vo);
      return InvokeResultBuilder.success(pageResult);
   }

   @ApiOperation("根据员工ID查询证书列表")
   @ApiImplicitParam(
      value = "员工ID",
      name = "employeeId",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:certificate:query"})
   @GetMapping({"/employee/{employeeId}"})
   public InvokeResult<List<ShkbEmployeeCertificate>> getByEmployeeId(@NotBlank(message = "员工ID不能为空") @PathVariable String employeeId) {
      List<ShkbEmployeeCertificate> datas = this.certificateService.queryByEmployeeId(employeeId);
      return InvokeResultBuilder.success(datas);
   }

   @ApiOperation("查询证书详情")
   @ApiImplicitParam(
      value = "ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:certificate:query"})
   @GetMapping({"/{id}"})
   public InvokeResult<GetShkbEmployeeCertificateBo> get(@NotBlank(message = "ID不能为空") @PathVariable String id) {
      GetShkbEmployeeCertificateBo data = this.certificateService.getDetail(id);
      if (data == null) {
         throw new DefaultClientException("证书不存在");
      } else {
         return InvokeResultBuilder.success(data);
      }
   }

   @ApiOperation("创建证书")
   @HasPermission({"hr:certificate:create"})
   @PostMapping
   public InvokeResult<Void> create(@Valid @RequestBody CreateShkbEmployeeCertificateVo vo) {
      this.certificateService.create(vo);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("修改证书")
   @HasPermission({"hr:certificate:update"})
   @PutMapping
   public InvokeResult<Void> update(@Valid @RequestBody UpdateShkbEmployeeCertificateVo vo) {
      this.certificateService.update(vo);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("删除证书")
   @ApiImplicitParam(
      value = "ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:certificate:delete"})
   @DeleteMapping({"/{id}"})
   public InvokeResult<Void> delete(@NotBlank(message = "ID不能为空") @PathVariable String id) {
      this.certificateService.deleteById(id);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("批量删除证书")
   @HasPermission({"hr:certificate:delete"})
   @DeleteMapping({"/batch"})
   public InvokeResult<Void> batchDelete(@ApiParam(value = "ID",required = true) @NotEmpty(message = "请选择需要删除的证书") @RequestParam List<String> ids) {
      this.certificateService.deleteByIds(ids);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("获取证书统计")
   @HasPermission({"hr:certificate:query"})
   @GetMapping({"/statistics"})
   public InvokeResult<CertificateStatisticsBo> getStatistics() {
      CertificateStatisticsBo statistics = this.certificateService.getStatistics();
      return InvokeResultBuilder.success(statistics);
   }

   @ApiOperation("导出证书")
   @HasPermission({"hr:certificate:query"})
   @PostMapping({"/export"})
   public void export(@Valid @RequestBody QueryShkbEmployeeCertificateVo vo) {
      ExcelMultipartWriterSheetBuilder builder = ExcelUtil.multipartExportXls("证书信息", CertificateExportModel.class);

      try {
         if (vo.getIds() != null && !vo.getIds().trim().isEmpty()) {
            List<String> idList = Arrays.stream(vo.getIds().split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
            if (!idList.isEmpty()) {
               List<QueryShkbEmployeeCertificateBo> certificates = this.certificateService.query(1, idList.size(), vo).getDatas();
               List<CertificateExportModel> models = certificates.stream().map(CertificateExportModel::new).collect(Collectors.toList());
               builder.doWrite(models);
               return;
            }
         }

         int pageIndex = 1;

         while (true) {
            PageResult<QueryShkbEmployeeCertificateBo> pageResult = this.certificateService.query(pageIndex, com.lframework.starter.web.core.controller.ExportSizeSupport.DEFAULT_EXPORT_SIZE, vo);
            List<QueryShkbEmployeeCertificateBo> datas = pageResult.getDatas();
            List<CertificateExportModel> models = datas.stream().map(CertificateExportModel::new).collect(Collectors.toList());
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
