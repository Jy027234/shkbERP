package com.lframework.xingyun.shkb.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.web.core.components.security.SecurityUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.UploadUtil;
import com.lframework.xingyun.shkb.entity.ShkbPersonAuthorizationFile;
import com.lframework.xingyun.shkb.mappers.ShkbPersonAuthorizationFileMapper;
import com.lframework.xingyun.shkb.service.ShkbPersonAuthorizationFileService;
import com.lframework.xingyun.shkb.service.ShkbPersonAuthorizationService;
import com.lframework.xingyun.shkb.utils.ShkbUploadFileUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ShkbPersonAuthorizationFileServiceImpl
   extends BaseMpServiceImpl<ShkbPersonAuthorizationFileMapper, ShkbPersonAuthorizationFile>
   implements ShkbPersonAuthorizationFileService {
   @Autowired
   private ShkbPersonAuthorizationService personAuthorizationService;

   @Autowired
   private ShkbUploadFileUtil uploadFileUtil;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public List<String> uploadPersonAuthorizationFiles(String authorizationId, List<MultipartFile> files) {
      List<String> fileIds = new ArrayList<>();
      if (CollectionUtil.isEmpty(files)) {
         return fileIds;
      } else {
         if (this.personAuthorizationService.findById(authorizationId) == null) {
            throw new DefaultClientException("人员授权不存在！");
         }
         for (MultipartFile file : files) {
            if (!file.isEmpty()) {
               String url = UploadUtil.upload(file).getUrl();
               ShkbPersonAuthorizationFile authorizationFile = new ShkbPersonAuthorizationFile();
               authorizationFile.setId(IdUtil.getId());
               authorizationFile.setAuthorizationId(authorizationId);
               authorizationFile.setFileName(file.getOriginalFilename());
               authorizationFile.setFileType(file.getContentType());
               authorizationFile.setFileUrl(url);
               authorizationFile.setFileSize(file.getSize());
               authorizationFile.setCreateBy(SecurityUtil.getCurrentUser().getName());
               authorizationFile.setCreateById(SecurityUtil.getCurrentUser().getId());
               authorizationFile.setCreateTime(LocalDateTime.now());
               ((ShkbPersonAuthorizationFileMapper)this.getBaseMapper()).insert(authorizationFile);
               fileIds.add(authorizationFile.getId());
            }
         }

         return fileIds;
      }
   }

   @Override
   public List<ShkbPersonAuthorizationFile> queryByAuthorizationId(String authorizationId) {
      LambdaQueryWrapper<ShkbPersonAuthorizationFile> wrapper = (Wrappers.lambdaQuery(
               ShkbPersonAuthorizationFile.class
            )
            .eq(ShkbPersonAuthorizationFile::getAuthorizationId, authorizationId))
         .orderByDesc(ShkbPersonAuthorizationFile::getCreateTime);
      return ((ShkbPersonAuthorizationFileMapper)this.getBaseMapper()).selectList(wrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public boolean deletePersonAuthorizationFile(String id) {
      ShkbPersonAuthorizationFile file = (ShkbPersonAuthorizationFile)((ShkbPersonAuthorizationFileMapper)this.getBaseMapper()).selectById(id);
      if (file == null) {
         throw new DefaultClientException("附件不存在！");
      } else {
         this.uploadFileUtil.deletePhysicalFile(file.getFileUrl());
         return ((ShkbPersonAuthorizationFileMapper)this.getBaseMapper()).deleteById(id) > 0;
      }
   }

   @Override
   public ShkbPersonAuthorizationFile getFileById(String id) {
      return (ShkbPersonAuthorizationFile)((ShkbPersonAuthorizationFileMapper)this.getBaseMapper()).selectById(id);
   }
}
