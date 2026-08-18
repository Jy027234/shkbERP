package com.lframework.xingyun.shkb.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.Assert;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.common.utils.FileUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.starter.web.core.utils.UploadUtil;
import com.lframework.xingyun.shkb.entity.ShkbTrainingImplementation;
import com.lframework.xingyun.shkb.entity.ShkbTrainingParticipant;
import com.lframework.xingyun.shkb.mappers.ShkbTrainingImplementationMapper;
import com.lframework.xingyun.shkb.service.ShkbEmployeeTrainingService;
import com.lframework.xingyun.shkb.service.ShkbTrainingImplementationService;
import com.lframework.xingyun.shkb.service.ShkbTrainingParticipantService;
import com.lframework.xingyun.shkb.vo.employee.CreateShkbEmployeeTrainingVo;
import com.lframework.xingyun.shkb.vo.training.QueryShkbTrainingImplementationVo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ShkbTrainingImplementationServiceImpl
   extends BaseMpServiceImpl<ShkbTrainingImplementationMapper, ShkbTrainingImplementation>
   implements ShkbTrainingImplementationService {
   @Autowired
   private ShkbEmployeeTrainingService employeeTrainingService;
   @Autowired
   private ShkbTrainingParticipantService participantService;

   @Override
   public ShkbTrainingImplementation findById(String id) {
      return ((ShkbTrainingImplementationMapper)this.getBaseMapper()).findByIdWithCourse(id);
   }

   @Override
   public PageResult<ShkbTrainingImplementation> query(Integer pageIndex, Integer pageSize, QueryShkbTrainingImplementationVo vo) {
      Assert.greaterThanZero(pageIndex);
      Assert.greaterThanZero(pageSize);
      PageHelperUtil.startPage(pageIndex, pageSize);
      List<ShkbTrainingImplementation> datas = ((ShkbTrainingImplementationMapper)this.getBaseMapper()).queryWithCourse(vo);
      return PageResultUtil.convert(new PageInfo<>(datas));
   }

   @Override
   public List<ShkbTrainingImplementation> queryByCourseId(String courseId) {
      QueryShkbTrainingImplementationVo vo = new QueryShkbTrainingImplementationVo();
      vo.setCourseId(courseId);
      return ((ShkbTrainingImplementationMapper)this.getBaseMapper()).queryWithCourse(vo);
   }

   public List<ShkbTrainingImplementation> query(QueryShkbTrainingImplementationVo vo) {
      return ((ShkbTrainingImplementationMapper)this.getBaseMapper()).queryWithCourse(vo);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public String create(String courseId, LocalDate planStartDate, LocalDate planEndDate, String trainingLocation, String instructor, String description) {
      ShkbTrainingImplementation data = new ShkbTrainingImplementation();
      data.setId("impl_" + System.currentTimeMillis());
      data.setCourseId(courseId);
      data.setPlanStartDate(planStartDate);
      data.setPlanEndDate(planEndDate);
      data.setTrainingLocation(trainingLocation);
      data.setInstructor(instructor);
      data.setDescription(description);
      data.setStatus(0);
      int result = ((ShkbTrainingImplementationMapper)this.getBaseMapper()).insert(data);
      if (result <= 0) {
         throw new DefaultClientException("创建实施计划失败");
      } else {
         return data.getId();
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void update(
      String id, String courseId, LocalDate planStartDate, LocalDate planEndDate, String trainingLocation, String instructor, String description
   ) {
      ShkbTrainingImplementation data = ((ShkbTrainingImplementationMapper)this.getBaseMapper()).findByIdWithCourse(id);
      Assert.notNull(data, "实施计划不存在", new Object[0]);
      data.setCourseId(courseId);
      data.setPlanStartDate(planStartDate);
      data.setPlanEndDate(planEndDate);
      data.setTrainingLocation(trainingLocation);
      data.setInstructor(instructor);
      data.setDescription(description);
      int result = ((ShkbTrainingImplementationMapper)this.getBaseMapper()).updateById(data);
      if (result <= 0) {
         throw new DefaultClientException("修改实施计划失败");
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void changeStatus(String id, Integer status) {
      ShkbTrainingImplementation data = ((ShkbTrainingImplementationMapper)this.getBaseMapper()).findByIdWithCourse(id);
      Assert.notNull(data, "实施计划不存在", new Object[0]);
      data.setStatus(status);
      int result = ((ShkbTrainingImplementationMapper)this.getBaseMapper()).updateById(data);
      if (result <= 0) {
         throw new DefaultClientException("变更状态失败");
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void startWithDate(String id, LocalDateTime actualStartDate) {
      ShkbTrainingImplementation data = ((ShkbTrainingImplementationMapper)this.getBaseMapper()).findByIdWithCourse(id);
      Assert.notNull(data, "实施计划不存在", new Object[0]);
      if (data.getStatus() != 0) {
         throw new DefaultClientException("只有计划中状态才能开始！");
      } else {
         data.setActualStartDate(actualStartDate != null ? actualStartDate : LocalDateTime.now());
         data.setStatus(1);
         int result = ((ShkbTrainingImplementationMapper)this.getBaseMapper()).updateById(data);
         if (result <= 0) {
            throw new DefaultClientException("开始培训失败");
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void completeWithDate(
      String id,
      LocalDateTime actualEndDate,
      String participantResults,
      String trainingType,
      String trainingOrg,
      Integer trainingHours,
      String trainingContent,
      MultipartFile file
   ) {
      ShkbTrainingImplementation data = ((ShkbTrainingImplementationMapper)this.getBaseMapper()).findByIdWithCourse(id);
      Assert.notNull(data, "实施计划不存在", new Object[0]);
      if (data.getStatus() != 1) {
         throw new DefaultClientException("只有进行中状态才能完成！");
      } else {
         if (file != null && !file.isEmpty()) {
            try {
               String url = UploadUtil.upload(file).getUrl();
               data.setUrl(url);
               data.setFileName(file.getOriginalFilename());
               data.setContentType(file.getContentType());
               data.setFileSize(FileUtil.readableFileSize(file.getSize()));
               String originalFilename = file.getOriginalFilename();
               if (originalFilename != null) {
                  int lastDotIndex = originalFilename.lastIndexOf(46);
                  if (lastDotIndex > 0) {
                     data.setFileSuffix(originalFilename.substring(lastDotIndex + 1));
                  }
               }
            } catch (Exception var22) {
               throw new DefaultClientException("文件上传失败：" + var22.getMessage());
            }
         }

         data.setActualEndDate(actualEndDate != null ? actualEndDate : LocalDateTime.now());
         data.setStatus(2);
         int result = ((ShkbTrainingImplementationMapper)this.getBaseMapper()).updateById(data);
         if (result <= 0) {
            throw new DefaultClientException("完成培训失败");
         } else {
            if (StringUtil.isNotBlank(participantResults)) {
               try {
                  ObjectMapper objectMapper = new ObjectMapper();

                   List<Map<String, Object>> parsedResults = objectMapper.readValue(participantResults, new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {});
                   for (Map<String, Object> participant : parsedResults) {
                     String participantId = (String)participant.get("participantId");
                     String employeeId = (String)participant.get("employeeId");
                     String employeeName = (String)participant.get("employeeName");
                     String trainingResult = (String)participant.get("trainingResult");
                     String certificateNo = (String)participant.get("certificateNo");
                     if (StringUtil.isNotBlank(employeeId)) {
                        CreateShkbEmployeeTrainingVo vo = new CreateShkbEmployeeTrainingVo();
                        vo.setEmployeeId(employeeId);
                        vo.setTrainingName(data.getCourseName());
                        vo.setTrainingType(StringUtil.isNotBlank(trainingType) ? trainingType : (data.getCourseType() != null ? data.getCourseType() : "内部培训"));
                        vo.setTrainingOrg(StringUtil.isNotBlank(trainingOrg) ? trainingOrg : data.getInstructor());
                        if (trainingHours != null) {
                           vo.setTrainingHours(trainingHours);
                        } else if (data.getCourseTrainingHours() != null) {
                           vo.setTrainingHours(data.getCourseTrainingHours().intValue());
                        }

                        vo.setTrainingContent(StringUtil.isNotBlank(trainingContent) ? trainingContent : data.getDescription());
                        vo.setStartDate(data.getActualStartDate() != null ? data.getActualStartDate().toLocalDate() : null);
                        vo.setEndDate(actualEndDate != null ? actualEndDate.toLocalDate() : LocalDate.now());
                        vo.setTrainingResult(trainingResult);
                        vo.setCertificateNo(certificateNo);
                        vo.setDescription("来自培训实施：" + data.getId());
                        this.employeeTrainingService.create(vo);
                        if (StringUtil.isNotBlank(participantId)) {
                           ShkbTrainingParticipant participantEntity = (ShkbTrainingParticipant)this.participantService.getById(participantId);
                           if (participantEntity != null) {
                              participantEntity.setStatus(2);
                              participantEntity.setTrainingResult(trainingResult);
                              participantEntity.setCertificateNo(certificateNo);
                              this.participantService.updateById(participantEntity);
                           }
                        }
                     }
                  }
               } catch (Exception var23) {
                  throw new DefaultClientException("创建培训记录失败：" + var23.getMessage());
               }
            }
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void cancel(String id) {
      ShkbTrainingImplementation data = ((ShkbTrainingImplementationMapper)this.getBaseMapper()).findByIdWithCourse(id);
      Assert.notNull(data, "实施计划不存在", new Object[0]);
      data.setStatus(3);
      int result = ((ShkbTrainingImplementationMapper)this.getBaseMapper()).updateById(data);
      if (result <= 0) {
         throw new DefaultClientException("取消培训失败");
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteById(String id) {
      ShkbTrainingImplementation data = ((ShkbTrainingImplementationMapper)this.getBaseMapper()).findByIdWithCourse(id);
      Assert.notNull(data, "实施计划不存在", new Object[0]);
      if (data.getStatus() != 0) {
         throw new DefaultClientException("只有计划中状态才能删除！");
      } else {
         int result = ((ShkbTrainingImplementationMapper)this.getBaseMapper()).deleteById(id);
         if (result <= 0) {
            throw new DefaultClientException("删除实施计划失败");
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteByIds(List<String> ids) {
      if (!CollectionUtil.isEmpty(ids)) {
         List<ShkbTrainingImplementation> datas = ((ShkbTrainingImplementationMapper)this.getBaseMapper()).selectBatchIds(ids);
         if (!CollectionUtil.isEmpty(datas)) {
            for (ShkbTrainingImplementation data : datas) {
               if (data.getStatus() != 0) {
                  throw new DefaultClientException("只有计划中状态才能删除！");
               }
            }

            int result = ((ShkbTrainingImplementationMapper)this.getBaseMapper()).deleteBatchIds(ids);
            if (result <= 0) {
               throw new DefaultClientException("删除实施计划失败");
            }
         }
      }
   }
}
