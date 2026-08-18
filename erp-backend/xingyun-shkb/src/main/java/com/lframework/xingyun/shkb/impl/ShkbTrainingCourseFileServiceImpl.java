package com.lframework.xingyun.shkb.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.ObjectUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.components.security.SecurityUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.UploadUtil;
import com.lframework.xingyun.core.annotations.OpLog;
import com.lframework.xingyun.core.utils.OpLogUtil;
import com.lframework.xingyun.shkb.entity.ShkbTrainingCourseFile;
import com.lframework.xingyun.shkb.mappers.ShkbTrainingCourseFileMapper;
import com.lframework.xingyun.shkb.service.ShkbTrainingCourseFileService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ShkbTrainingCourseFileServiceImpl
   extends BaseMpServiceImpl<ShkbTrainingCourseFileMapper, ShkbTrainingCourseFile>
   implements ShkbTrainingCourseFileService {
   @Override
   public List<ShkbTrainingCourseFile> queryByCourseId(String courseId) {
      LambdaQueryWrapper<ShkbTrainingCourseFile> wrapper = (Wrappers.lambdaQuery(
               ShkbTrainingCourseFile.class
            )
            .eq(ShkbTrainingCourseFile::getCourseId, courseId))
         .orderByDesc(ShkbTrainingCourseFile::getCreateTime);
      return ((ShkbTrainingCourseFileMapper)this.getBaseMapper()).selectList(wrapper);
   }

   @OpLog(
      type = 99,
      name = "上传培训课程文档"
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public String upload(String courseId, MultipartFile file, String description) {
      if (file != null && !file.isEmpty()) {
         String url = UploadUtil.upload(file).getUrl();
         ShkbTrainingCourseFile data = new ShkbTrainingCourseFile();
         data.setId(IdUtil.getId());
         data.setCourseId(courseId);
         data.setFileName(file.getOriginalFilename());
         data.setFileType(file.getContentType());
         data.setFileUrl(url);
         data.setFileSize(file.getSize());
         data.setDescription(StringUtil.isBlank(description) ? "" : description);
         data.setCreateBy(SecurityUtil.getCurrentUser().getName());
         ((ShkbTrainingCourseFileMapper)this.getBaseMapper()).insert(data);
         return data.getId();
      } else {
         throw new DefaultClientException("文件不能为空！");
      }
   }

   @OpLog(
      type = 99,
      name = "删除培训课程文档，ID：{}",
      params = {"#id"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteById(String id) {
      ShkbTrainingCourseFile data = (ShkbTrainingCourseFile)((ShkbTrainingCourseFileMapper)this.getBaseMapper()).selectById(id);
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("文档不存在！");
      } else {
         ((ShkbTrainingCourseFileMapper)this.getBaseMapper()).deleteById(id);
         OpLogUtil.setVariable("id", id);
      }
   }

   @OpLog(
      type = 99,
      name = "批量删除培训课程文档，数量：{}",
      params = {"#ids.size()"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteByIds(List<String> ids) {
      if (ids != null && !ids.isEmpty()) {
         for (String id : ids) {
            this.deleteById(id);
         }
      } else {
         throw new DefaultClientException("请选择要删除的文档！");
      }
   }
}
