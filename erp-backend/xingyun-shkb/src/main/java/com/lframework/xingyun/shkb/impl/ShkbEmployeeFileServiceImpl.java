package com.lframework.xingyun.shkb.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.components.security.SecurityUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.UploadUtil;
import com.lframework.xingyun.shkb.entity.ShkbEmployeeFile;
import com.lframework.xingyun.shkb.mappers.ShkbEmployeeFileMapper;
import com.lframework.xingyun.shkb.service.ShkbEmployeeFileService;
import com.lframework.xingyun.shkb.service.ShkbEmployeeService;
import com.lframework.xingyun.shkb.utils.ShkbUploadFileUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ShkbEmployeeFileServiceImpl extends BaseMpServiceImpl<ShkbEmployeeFileMapper, ShkbEmployeeFile> implements ShkbEmployeeFileService {
   @Autowired
   private ShkbEmployeeService employeeService;

   @Autowired
   private ShkbUploadFileUtil uploadFileUtil;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public List<String> uploadEmployeeFiles(String employeeId, List<MultipartFile> files) {
      List<String> fileIds = new ArrayList<>();
      if (CollectionUtil.isEmpty(files)) {
         return fileIds;
      } else {
         if (this.employeeService.getDetail(employeeId) == null) {
            throw new DefaultClientException("员工不存在！");
         }
         for (MultipartFile file : files) {
            if (!file.isEmpty()) {
               String url = UploadUtil.upload(file).getUrl();
               ShkbEmployeeFile employeeFile = new ShkbEmployeeFile();
               employeeFile.setId(IdUtil.getId());
               employeeFile.setEmployeeId(employeeId);
               employeeFile.setFileName(file.getOriginalFilename());
               employeeFile.setFileType(file.getContentType());
               employeeFile.setFileUrl(url);
               employeeFile.setFileSize(file.getSize());
               employeeFile.setCreateBy(SecurityUtil.getCurrentUser().getName());
               employeeFile.setCreateById(SecurityUtil.getCurrentUser().getId());
               employeeFile.setCreateTime(LocalDateTime.now());
               ((ShkbEmployeeFileMapper)this.getBaseMapper()).insert(employeeFile);
               fileIds.add(employeeFile.getId());
            }
         }

         return fileIds;
      }
   }

   @Override
   public List<ShkbEmployeeFile> queryByEmployeeId(String employeeId) {
      LambdaQueryWrapper<ShkbEmployeeFile> wrapper = (Wrappers.<ShkbEmployeeFile>lambdaQuery(ShkbEmployeeFile.class)
            .eq(ShkbEmployeeFile::getEmployeeId, employeeId))
         .orderByDesc(ShkbEmployeeFile::getCreateTime);
      return ((ShkbEmployeeFileMapper)this.getBaseMapper()).selectList(wrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public boolean deleteEmployeeFile(String id) {
      ShkbEmployeeFile file = (ShkbEmployeeFile)((ShkbEmployeeFileMapper)this.getBaseMapper()).selectById(id);
      if (file == null) {
         throw new DefaultClientException("附件不存在！");
      } else {
         this.uploadFileUtil.deletePhysicalFile(file.getFileUrl());
         return ((ShkbEmployeeFileMapper)this.getBaseMapper()).deleteById(id) > 0;
      }
   }

   @Override
   public ShkbEmployeeFile getFileById(String id) {
      return (ShkbEmployeeFile)((ShkbEmployeeFileMapper)this.getBaseMapper()).selectById(id);
   }
}
