package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.xingyun.shkb.entity.ShkbEmployeeFile;
import com.lframework.xingyun.shkb.service.ShkbEmployeeFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(
   tags = {"员工附件管理"}
)
@Validated
@RestController
@RequestMapping({"/shkb/employee-file"})
public class ShkbEmployeeFileController extends DefaultBaseController {
   @Autowired
   private ShkbEmployeeFileService fileService;

   @Value("${jugg.upload.location}")
   private String uploadLocation;

   @ApiOperation("上传员工附件")
   @ApiImplicitParam(
      value = "员工ID",
      name = "employeeId",
      paramType = "query",
      required = true
   )
   @HasPermission({"hr:employee:update"})
   @PostMapping({"/upload"})
   public InvokeResult<List<String>> upload(
      @NotBlank(message = "员工ID不能为空！") @RequestParam("employeeId") String employeeId, @RequestParam(value = "files",required = false) List<MultipartFile> files
   ) {
      List<String> fileIds = this.fileService.uploadEmployeeFiles(employeeId, files);
      return InvokeResultBuilder.success(fileIds);
   }

   @ApiOperation("获取员工附件列表")
   @ApiImplicitParam(
      value = "员工ID",
      name = "employeeId",
      paramType = "query",
      required = true
   )
   @HasPermission({"hr:employee:query"})
   @GetMapping({"/list"})
   public InvokeResult<List<ShkbEmployeeFile>> getList(@NotBlank(message = "员工ID不能为空！") @RequestParam("employeeId") String employeeId) {
      List<ShkbEmployeeFile> files = this.fileService.queryByEmployeeId(employeeId);
      return InvokeResultBuilder.success(files);
   }

   @ApiOperation("删除员工附件")
   @ApiImplicitParam(
      value = "附件ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:employee:update"})
   @DeleteMapping({"/{id}"})
   public InvokeResult<Void> delete(@NotBlank(message = "附件ID不能为空！") @PathVariable("id") String id) {
      boolean success = this.fileService.deleteEmployeeFile(id);
      if (!success) {
         throw new DefaultClientException("附件不存在或删除失败！");
      } else {
         return InvokeResultBuilder.success();
      }
   }

   @ApiOperation("下载员工附件")
   @ApiImplicitParam(
      value = "附件ID",
      name = "id",
      paramType = "path",
      required = true
   )
   @HasPermission({"hr:employee:query"})
   @GetMapping({"/download/{id}"})
   public ResponseEntity<Resource> download(@NotBlank(message = "附件ID不能为空！") @PathVariable("id") String id) throws MalformedURLException {
      ShkbEmployeeFile file = this.fileService.getFileById(id);
      if (file != null && file.getFileUrl() != null) {
         String fileUrl = file.getFileUrl();
         String relativePath;
         try {
            if (fileUrl.contains("://")) {
               relativePath = new URI(fileUrl).getPath();
            } else {
               relativePath = fileUrl;
            }
         } catch (URISyntaxException var13) {
            throw new DefaultClientException("不支持的附件路径！");
         }
         if (relativePath.startsWith("/oss/")) {
            relativePath = relativePath.substring("/oss".length());
         } else if (relativePath.startsWith("/uploads/")) {
            relativePath = relativePath.substring("/uploads".length());
         }
         Path uploadsRoot = Paths.get(this.uploadLocation).toAbsolutePath().normalize();
         Path filePath = uploadsRoot.resolve(relativePath.startsWith("/") ? relativePath.substring(1) : relativePath).normalize();
         if (!filePath.startsWith(uploadsRoot)) {
            throw new DefaultClientException("不支持的附件路径！");
         }
         Resource resource = new UrlResource(filePath.toUri());
         if (!resource.exists()) {
            throw new DefaultClientException("文件不存在！");
         } else {
            String fileName = file.getFileName();
            return ((BodyBuilder)ResponseEntity.ok()
                  .contentType(MediaType.parseMediaType(file.getFileType() != null ? file.getFileType() : "application/octet-stream"))
                  .header("Content-Disposition", new String[]{"attachment; filename=\"" + fileName + "\""}))
               .body(resource);
         }
      } else {
         throw new DefaultClientException("附件不存在！");
      }
   }
}
