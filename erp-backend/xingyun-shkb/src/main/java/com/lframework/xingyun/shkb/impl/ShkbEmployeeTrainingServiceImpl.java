package com.lframework.xingyun.shkb.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.Assert;
import com.lframework.starter.common.utils.ObjectUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.components.security.SecurityUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.core.annotations.OpLog;
import com.lframework.xingyun.core.utils.OpLogUtil;
import com.lframework.xingyun.shkb.bo.training.GetShkbEmployeeTrainingBo;
import com.lframework.xingyun.shkb.bo.training.QueryShkbEmployeeTrainingBo;
import com.lframework.xingyun.shkb.bo.training.TrainingStatisticsBo;
import com.lframework.xingyun.shkb.entity.ShkbEmployee;
import com.lframework.xingyun.shkb.entity.ShkbEmployeeTraining;
import com.lframework.xingyun.shkb.mappers.ShkbEmployeeMapper;
import com.lframework.xingyun.shkb.mappers.ShkbEmployeeTrainingMapper;
import com.lframework.xingyun.shkb.service.ShkbEmployeeTrainingService;
import com.lframework.xingyun.shkb.vo.employee.CreateShkbEmployeeTrainingVo;
import com.lframework.xingyun.shkb.vo.employee.QueryShkbEmployeeTrainingVo;
import com.lframework.xingyun.shkb.vo.employee.UpdateShkbEmployeeTrainingVo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShkbEmployeeTrainingServiceImpl extends BaseMpServiceImpl<ShkbEmployeeTrainingMapper, ShkbEmployeeTraining> implements ShkbEmployeeTrainingService {
   @Autowired
   private ShkbEmployeeMapper employeeMapper;

   @Override
   public ShkbEmployeeTraining findById(String id) {
      return (ShkbEmployeeTraining)((ShkbEmployeeTrainingMapper)this.getBaseMapper()).selectById(id);
   }

   @Override
   public GetShkbEmployeeTrainingBo getById(String id) {
      ShkbEmployeeTraining entity = (ShkbEmployeeTraining)((ShkbEmployeeTrainingMapper)this.getBaseMapper()).selectById(id);
      if (entity == null) {
         return null;
      } else {
         GetShkbEmployeeTrainingBo bo = new GetShkbEmployeeTrainingBo();
         bo.setId(entity.getId());
         bo.setEmployeeId(entity.getEmployeeId());
         bo.setTrainingName(entity.getTrainingName());
         bo.setTrainingType(entity.getTrainingType());
         bo.setTrainingOrg(entity.getTrainingOrg());
         bo.setTrainingContent(entity.getTrainingContent());
         bo.setStartDate(entity.getStartDate() != null ? entity.getStartDate().toString() : null);
         bo.setEndDate(entity.getEndDate() != null ? entity.getEndDate().toString() : null);
         bo.setTrainingHours(entity.getTrainingHours());
         bo.setTrainingResult(entity.getTrainingResult());
         bo.setCertificateNo(entity.getCertificateNo());
         bo.setFileUrl(entity.getFileUrl());
         bo.setDescription(entity.getDescription());
         bo.setCreateBy(entity.getCreateBy());
         bo.setCreateTime(entity.getCreateTime() != null ? entity.getCreateTime().toString() : null);
         if (StringUtil.isNotBlank(entity.getEmployeeId())) {
            ShkbEmployee employee = (ShkbEmployee)this.employeeMapper.selectById(entity.getEmployeeId());
            if (employee != null) {
               bo.setEmployeeName(employee.getName());
            }
         }

         return bo;
      }
   }

   @Override
   public PageResult<QueryShkbEmployeeTrainingBo> query(Integer pageIndex, Integer pageSize, QueryShkbEmployeeTrainingVo vo) {
      Assert.greaterThanZero(pageIndex);
      Assert.greaterThanZero(pageSize);
      PageHelperUtil.startPage(pageIndex, pageSize);
      List<QueryShkbEmployeeTrainingBo> datas = ((ShkbEmployeeTrainingMapper)this.getBaseMapper()).query(vo);
      return PageResultUtil.convert(new PageInfo<>(datas));
   }

   @Override
   public List<ShkbEmployeeTraining> queryByEmployeeId(String employeeId) {
      LambdaQueryWrapper<ShkbEmployeeTraining> wrapper = (Wrappers.lambdaQuery(
               ShkbEmployeeTraining.class
            )
            .eq(ShkbEmployeeTraining::getEmployeeId, employeeId))
         .orderByDesc(ShkbEmployeeTraining::getStartDate);
      return ((ShkbEmployeeTrainingMapper)this.getBaseMapper()).selectList(wrapper);
   }

   @OpLog(
      type = 99,
      name = "新增员工培训记录，培训名称：{}",
      params = {"#trainingName"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void create(CreateShkbEmployeeTrainingVo vo) {
      ShkbEmployeeTraining data = new ShkbEmployeeTraining();
      data.setId(IdUtil.getId());
      data.setEmployeeId(vo.getEmployeeId());
      data.setTrainingName(vo.getTrainingName());
      data.setTrainingType(vo.getTrainingType());
      data.setTrainingOrg(vo.getTrainingOrg());
      data.setTrainingContent(vo.getTrainingContent());
      data.setStartDate(vo.getStartDate());
      data.setEndDate(vo.getEndDate());
      data.setTrainingHours(vo.getTrainingHours() != null ? BigDecimal.valueOf((long)vo.getTrainingHours().intValue()) : null);
      data.setTrainingResult(vo.getTrainingResult());
      data.setCertificateNo(vo.getCertificateNo());
      data.setDescription(StringUtil.isBlank(vo.getDescription()) ? "" : vo.getDescription());
      data.setCreateBy(SecurityUtil.getCurrentUser().getName());
      data.setCreateById(SecurityUtil.getCurrentUser().getId());
      data.setUpdateBy(SecurityUtil.getCurrentUser().getName());
      data.setUpdateById(SecurityUtil.getCurrentUser().getId());
      ((ShkbEmployeeTrainingMapper)this.getBaseMapper()).insert(data);
      OpLogUtil.setVariable("trainingName", vo.getTrainingName());
   }

   @OpLog(
      type = 99,
      name = "修改员工培训记录，培训名称：{}",
      params = {"#trainingName"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void update(UpdateShkbEmployeeTrainingVo vo) {
      ShkbEmployeeTraining data = (ShkbEmployeeTraining)((ShkbEmployeeTrainingMapper)this.getBaseMapper()).selectById(vo.getId());
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("培训记录不存在！");
      } else {
         LambdaUpdateWrapper<ShkbEmployeeTraining> updateWrapper = ((((((((((((((Wrappers.lambdaUpdate(
                                                         ShkbEmployeeTraining.class
                                                      )
                                                      .set(ShkbEmployeeTraining::getEmployeeId, vo.getEmployeeId()))
                                                   .set(ShkbEmployeeTraining::getTrainingName, vo.getTrainingName()))
                                                .set(ShkbEmployeeTraining::getTrainingType, vo.getTrainingType()))
                                             .set(ShkbEmployeeTraining::getTrainingOrg, vo.getTrainingOrg()))
                                          .set(ShkbEmployeeTraining::getTrainingContent, vo.getTrainingContent()))
                                       .set(ShkbEmployeeTraining::getStartDate, vo.getStartDate()))
                                    .set(ShkbEmployeeTraining::getEndDate, vo.getEndDate()))
                                 .set(ShkbEmployeeTraining::getTrainingHours, vo.getTrainingHours()))
                              .set(ShkbEmployeeTraining::getTrainingResult, vo.getTrainingResult()))
                           .set(ShkbEmployeeTraining::getCertificateNo, vo.getCertificateNo()))
                        .set(ShkbEmployeeTraining::getDescription, StringUtil.isBlank(vo.getDescription()) ? "" : vo.getDescription()))
                     .set(ShkbEmployeeTraining::getUpdateBy, SecurityUtil.getCurrentUser().getName()))
                  .set(ShkbEmployeeTraining::getUpdateById, SecurityUtil.getCurrentUser().getId()))
               .set(ShkbEmployeeTraining::getUpdateTime, LocalDateTime.now()))
            .eq(ShkbEmployeeTraining::getId, vo.getId());
         ((ShkbEmployeeTrainingMapper)this.getBaseMapper()).update(null, updateWrapper);
         OpLogUtil.setVariable("trainingName", vo.getTrainingName());
      }
   }

   @OpLog(
      type = 99,
      name = "删除员工培训记录，ID：{}",
      params = {"#id"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteById(String id) {
      ShkbEmployeeTraining data = (ShkbEmployeeTraining)((ShkbEmployeeTrainingMapper)this.getBaseMapper()).selectById(id);
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("培训记录不存在！");
      } else {
         ((ShkbEmployeeTrainingMapper)this.getBaseMapper()).deleteById(id);
         OpLogUtil.setVariable("id", id);
      }
   }

   @OpLog(
      type = 99,
      name = "批量删除员工培训记录，数量：{}",
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
         throw new DefaultClientException("请选择要删除的培训记录！");
      }
   }

   @Override
   public TrainingStatisticsBo getStatistics() {
      LambdaQueryWrapper<ShkbEmployeeTraining> wrapper = Wrappers.<ShkbEmployeeTraining>lambdaQuery(ShkbEmployeeTraining.class);
      List<ShkbEmployeeTraining> allTrainings = ((ShkbEmployeeTrainingMapper)this.getBaseMapper()).selectList(wrapper);
      LocalDate today = LocalDate.now();
      TrainingStatisticsBo stats = new TrainingStatisticsBo();
      stats.setTotal(allTrainings.size());
      stats.setCompleted((int)allTrainings.stream().filter(t -> StringUtil.isNotBlank(t.getTrainingResult())).count());
      stats.setInProgress(
         (int)allTrainings.stream().filter(t -> StringUtil.isBlank(t.getTrainingResult()) && t.getEndDate() != null && t.getEndDate().isBefore(today)).count()
      );
      stats.setPending(
         (int)allTrainings.stream()
            .filter(t -> StringUtil.isBlank(t.getTrainingResult()) && t.getStartDate() != null && t.getStartDate().isAfter(today))
            .count()
      );
      return stats;
   }
}
