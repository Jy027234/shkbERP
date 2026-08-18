package com.lframework.xingyun.shkb.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
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
import com.lframework.starter.web.core.utils.UploadUtil;
import com.lframework.xingyun.core.annotations.OpLog;
import com.lframework.xingyun.core.utils.OpLogUtil;
import com.lframework.xingyun.shkb.bo.employee.GetShkbEmployeeBo;
import com.lframework.xingyun.shkb.bo.employee.QueryShkbEmployeeBo;
import com.lframework.xingyun.shkb.entity.ShkbEmployee;
import com.lframework.xingyun.shkb.mappers.ShkbEmployeeMapper;
import com.lframework.xingyun.shkb.service.ShkbEmployeeService;
import com.lframework.xingyun.shkb.vo.employee.BatchLeaveStatusShkbEmployeeVo;
import com.lframework.xingyun.shkb.vo.employee.CreateShkbEmployeeVo;
import com.lframework.xingyun.shkb.vo.employee.LeaveShkbEmployeeVo;
import com.lframework.xingyun.shkb.vo.employee.LeaveStatusShkbEmployeeVo;
import com.lframework.xingyun.shkb.vo.employee.QueryShkbEmployeeVo;
import com.lframework.xingyun.shkb.vo.employee.UpdateShkbEmployeeVo;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ShkbEmployeeServiceImpl extends BaseMpServiceImpl<ShkbEmployeeMapper, ShkbEmployee> implements ShkbEmployeeService {
   @Override
   public PageResult<QueryShkbEmployeeBo> query(Integer pageIndex, Integer pageSize, QueryShkbEmployeeVo vo) {
      Assert.greaterThanZero(pageIndex);
      Assert.greaterThanZero(pageSize);
      PageHelperUtil.startPage(pageIndex, pageSize);
      List<QueryShkbEmployeeBo> datas = ((ShkbEmployeeMapper)this.getBaseMapper()).query(vo);
      return PageResultUtil.convert(new PageInfo<>(datas));
   }

   @Override
   public ShkbEmployee findById(String id) {
      return (ShkbEmployee)((ShkbEmployeeMapper)this.getBaseMapper()).selectById(id);
   }

   @Override
   public GetShkbEmployeeBo getDetail(String id) {
      return ((ShkbEmployeeMapper)this.getBaseMapper()).getDetail(id);
   }

   @OpLog(
      type = 99,
      name = "新增员工，ID：{}, 工号：{}",
      params = {"#id", "#code"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void create(CreateShkbEmployeeVo vo) {
      Wrapper<ShkbEmployee> checkWrapper = (Wrapper<ShkbEmployee>)Wrappers.<ShkbEmployee>lambdaQuery(ShkbEmployee.class).eq(ShkbEmployee::getCode, vo.getCode());
      if (((ShkbEmployeeMapper)this.getBaseMapper()).selectCount(checkWrapper) > 0) {
         throw new DefaultClientException("工号重复，请重新输入！");
      } else {
         ShkbEmployee data = new ShkbEmployee();
         data.setId(IdUtil.getId());
         data.setCode(vo.getCode());
         data.setName(vo.getName());
         data.setGender(vo.getGender());
         data.setIdCard(vo.getIdCard());
         data.setBirthday(vo.getBirthday());
         data.setNation(vo.getNation());
         data.setNativePlace(vo.getNativePlace());
         data.setPoliticalStatus(vo.getPoliticalStatus());
         data.setEducation(vo.getEducation());
         data.setMajor(vo.getMajor());
         data.setGraduateSchool(vo.getGraduateSchool());
         data.setGraduateDate(vo.getGraduateDate());
         data.setPhone(vo.getPhone());
         data.setEmail(vo.getEmail());
         data.setAddress(vo.getAddress());
         data.setEmergencyContact(vo.getEmergencyContact());
         data.setEmergencyPhone(vo.getEmergencyPhone());
         data.setDeptId(vo.getDeptId());
         data.setPosition(vo.getPosition());
         data.setEntryDate(vo.getEntryDate());
         data.setRegularDate(vo.getRegularDate());
         data.setStatus(vo.getStatus());
         data.setPhotoUrl(vo.getPhotoUrl());
         data.setDescription(StringUtil.isBlank(vo.getDescription()) ? "" : vo.getDescription());
         data.setCreateBy(SecurityUtil.getCurrentUser().getName());
         data.setCreateById(SecurityUtil.getCurrentUser().getId());
         data.setUpdateBy(SecurityUtil.getCurrentUser().getName());
         data.setUpdateById(SecurityUtil.getCurrentUser().getId());
         ((ShkbEmployeeMapper)this.getBaseMapper()).insert(data);
         OpLogUtil.setVariable("id", data.getId());
         OpLogUtil.setVariable("code", vo.getCode());
         OpLogUtil.setExtra(vo);
      }
   }

   @OpLog(
      type = 99,
      name = "修改员工，ID：{}, 工号：{}",
      params = {"#id", "#code"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void update(UpdateShkbEmployeeVo vo) {
      ShkbEmployee data = (ShkbEmployee)((ShkbEmployeeMapper)this.getBaseMapper()).selectById(vo.getId());
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("员工不存在！");
      } else {
         Wrapper<ShkbEmployee> checkWrapper = (Wrapper<ShkbEmployee>)(Wrappers.<ShkbEmployee>lambdaQuery(ShkbEmployee.class)
               .eq(ShkbEmployee::getCode, vo.getCode()))
            .ne(ShkbEmployee::getId, vo.getId());
         if (((ShkbEmployeeMapper)this.getBaseMapper()).selectCount(checkWrapper) > 0) {
            throw new DefaultClientException("工号重复，请重新输入！");
         } else {
            LambdaUpdateWrapper<ShkbEmployee> updateWrapper = (((((((((((((((((((((((((((Wrappers.lambdaUpdate(
                                                                                                   ShkbEmployee.class
                                                                                                )
                                                                                                .set(ShkbEmployee::getCode, vo.getCode()))
                                                                                             .set(ShkbEmployee::getName, vo.getName()))
                                                                                          .set(ShkbEmployee::getGender, vo.getGender()))
                                                                                       .set(ShkbEmployee::getIdCard, vo.getIdCard()))
                                                                                    .set(ShkbEmployee::getBirthday, vo.getBirthday()))
                                                                                 .set(ShkbEmployee::getNation, vo.getNation()))
                                                                              .set(ShkbEmployee::getNativePlace, vo.getNativePlace()))
                                                                           .set(ShkbEmployee::getPoliticalStatus, vo.getPoliticalStatus()))
                                                                        .set(ShkbEmployee::getEducation, vo.getEducation()))
                                                                     .set(ShkbEmployee::getMajor, vo.getMajor()))
                                                                  .set(ShkbEmployee::getGraduateSchool, vo.getGraduateSchool()))
                                                               .set(ShkbEmployee::getGraduateDate, vo.getGraduateDate()))
                                                            .set(ShkbEmployee::getPhone, vo.getPhone()))
                                                         .set(ShkbEmployee::getEmail, vo.getEmail()))
                                                      .set(ShkbEmployee::getAddress, vo.getAddress()))
                                                   .set(ShkbEmployee::getEmergencyContact, vo.getEmergencyContact()))
                                                .set(ShkbEmployee::getEmergencyPhone, vo.getEmergencyPhone()))
                                             .set(ShkbEmployee::getDeptId, vo.getDeptId()))
                                          .set(ShkbEmployee::getPosition, vo.getPosition()))
                                       .set(ShkbEmployee::getEntryDate, vo.getEntryDate()))
                                    .set(ShkbEmployee::getRegularDate, vo.getRegularDate()))
                                 .set(ShkbEmployee::getStatus, vo.getStatus()))
                              .set(ShkbEmployee::getPhotoUrl, vo.getPhotoUrl()))
                           .set(ShkbEmployee::getDescription, StringUtil.isBlank(vo.getDescription()) ? "" : vo.getDescription()))
                        .set(ShkbEmployee::getUpdateBy, SecurityUtil.getCurrentUser().getName()))
                     .set(ShkbEmployee::getUpdateById, SecurityUtil.getCurrentUser().getId()))
                  .set(ShkbEmployee::getUpdateTime, new Date()))
               .eq(ShkbEmployee::getId, vo.getId());
            ((ShkbEmployeeMapper)this.getBaseMapper()).update(null, updateWrapper);
            OpLogUtil.setVariable("id", data.getId());
            OpLogUtil.setVariable("code", vo.getCode());
            OpLogUtil.setExtra(vo);
         }
      }
   }

   @OpLog(
      type = 99,
      name = "删除员工，ID：{}",
      params = {"#id"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteById(String id) {
      ShkbEmployee data = (ShkbEmployee)((ShkbEmployeeMapper)this.getBaseMapper()).selectById(id);
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("员工不存在！");
      } else {
         ((ShkbEmployeeMapper)this.getBaseMapper()).deleteById(id);
         OpLogUtil.setVariable("id", data.getId());
         OpLogUtil.setExtra(data);
      }
   }

   @OpLog(
      type = 99,
      name = "批量删除员工，数量：{}",
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
         throw new DefaultClientException("请选择要删除的员工！");
      }
   }

   @OpLog(
      type = 99,
      name = "员工离职，ID：{}",
      params = {"#id"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void leave(LeaveShkbEmployeeVo vo) {
      ShkbEmployee data = (ShkbEmployee)((ShkbEmployeeMapper)this.getBaseMapper()).selectById(vo.getId());
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("员工不存在！");
      } else {
         LambdaUpdateWrapper<ShkbEmployee> updateWrapper = ((((((Wrappers.lambdaUpdate(
                                 ShkbEmployee.class
                              )
                              .set(ShkbEmployee::getStatus, 0))
                           .set(ShkbEmployee::getLeaveDate, vo.getLeaveDate()))
                        .set(ShkbEmployee::getLeaveReason, vo.getLeaveReason()))
                     .set(ShkbEmployee::getUpdateBy, SecurityUtil.getCurrentUser().getName()))
                  .set(ShkbEmployee::getUpdateById, SecurityUtil.getCurrentUser().getId()))
               .set(ShkbEmployee::getUpdateTime, new Date()))
            .eq(ShkbEmployee::getId, vo.getId());
         ((ShkbEmployeeMapper)this.getBaseMapper()).update(null, updateWrapper);
         OpLogUtil.setVariable("id", data.getId());
         OpLogUtil.setExtra(vo);
      }
   }

   @OpLog(
      type = 99,
      name = "更新离职登记信息，ID：{}",
      params = {"#id"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void updateLeaveInfo(LeaveShkbEmployeeVo vo) {
      ShkbEmployee data = (ShkbEmployee)((ShkbEmployeeMapper)this.getBaseMapper()).selectById(vo.getId());
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("员工不存在！");
      } else {
         LambdaUpdateWrapper<ShkbEmployee> updateWrapper = (((((Wrappers.lambdaUpdate(
                              ShkbEmployee.class
                           )
                           .set(ShkbEmployee::getLeaveDate, vo.getLeaveDate()))
                        .set(ShkbEmployee::getLeaveReason, vo.getLeaveReason()))
                     .set(ShkbEmployee::getUpdateBy, SecurityUtil.getCurrentUser().getName()))
                  .set(ShkbEmployee::getUpdateById, SecurityUtil.getCurrentUser().getId()))
               .set(ShkbEmployee::getUpdateTime, new Date()))
            .eq(ShkbEmployee::getId, vo.getId());
         ((ShkbEmployeeMapper)this.getBaseMapper()).update(null, updateWrapper);
         OpLogUtil.setVariable("id", data.getId());
         OpLogUtil.setExtra(vo);
      }
   }

   @OpLog(
      type = 99,
      name = "更新员工离职状态，ID：{}",
      params = {"#id"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void leaveStatus(LeaveStatusShkbEmployeeVo vo) {
      ShkbEmployee data = (ShkbEmployee)((ShkbEmployeeMapper)this.getBaseMapper()).selectById(vo.getId());
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("员工不存在！");
      } else {
         LambdaUpdateWrapper<ShkbEmployee> updateWrapper = ((((Wrappers.lambdaUpdate(
                           ShkbEmployee.class
                        )
                        .set(ShkbEmployee::getStatus, 0))
                     .set(ShkbEmployee::getUpdateBy, SecurityUtil.getCurrentUser().getName()))
                  .set(ShkbEmployee::getUpdateById, SecurityUtil.getCurrentUser().getId()))
               .set(ShkbEmployee::getUpdateTime, new Date()))
            .eq(ShkbEmployee::getId, vo.getId());
         ((ShkbEmployeeMapper)this.getBaseMapper()).update(null, updateWrapper);
         OpLogUtil.setVariable("id", data.getId());
      }
   }

   @OpLog(
      type = 99,
      name = "批量更新员工离职状态，数量：{}",
      params = {"#vo.ids.size()"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void batchLeaveStatus(BatchLeaveStatusShkbEmployeeVo vo) {
      if (vo.getIds() != null && !vo.getIds().isEmpty()) {
         LambdaUpdateWrapper<ShkbEmployee> updateWrapper = ((((Wrappers.lambdaUpdate(
                           ShkbEmployee.class
                        )
                        .set(ShkbEmployee::getStatus, 0))
                     .set(ShkbEmployee::getUpdateBy, SecurityUtil.getCurrentUser().getName()))
                  .set(ShkbEmployee::getUpdateById, SecurityUtil.getCurrentUser().getId()))
               .set(ShkbEmployee::getUpdateTime, new Date()))
            .in(ShkbEmployee::getId, vo.getIds());
         ((ShkbEmployeeMapper)this.getBaseMapper()).update(null, updateWrapper);
      }
   }

   @OpLog(
      type = 99,
      name = "上传员工照片，ID：{}",
      params = {"#id"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public String uploadEmployeePhoto(String id, MultipartFile file) {
      ShkbEmployee data = (ShkbEmployee)((ShkbEmployeeMapper)this.getBaseMapper()).selectById(id);
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("员工不存在！");
      } else if (file.isEmpty()) {
         throw new DefaultClientException("上传文件不能为空！");
      } else {
         String photoUrl = UploadUtil.upload(file).getUrl();
         LambdaUpdateWrapper<ShkbEmployee> updateWrapper = ((((Wrappers.lambdaUpdate(
                           ShkbEmployee.class
                        )
                        .set(ShkbEmployee::getPhotoUrl, photoUrl))
                     .set(ShkbEmployee::getUpdateBy, SecurityUtil.getCurrentUser().getName()))
                  .set(ShkbEmployee::getUpdateById, SecurityUtil.getCurrentUser().getId()))
               .set(ShkbEmployee::getUpdateTime, new Date()))
            .eq(ShkbEmployee::getId, id);
         ((ShkbEmployeeMapper)this.getBaseMapper()).update(null, updateWrapper);
         OpLogUtil.setVariable("id", id);
         OpLogUtil.setExtra(photoUrl);
         return photoUrl;
      }
   }

   @Override
   public Map<String, Long> getStatistics() {
      Map<String, Long> statistics = new HashMap<>();
      long total = (long)((ShkbEmployeeMapper)this.getBaseMapper()).selectCount(null).intValue();
      long active = (long)((ShkbEmployeeMapper)this.getBaseMapper()).selectCount(new LambdaQueryWrapper<ShkbEmployee>().eq(ShkbEmployee::getStatus, 1)).intValue();
      long probation = (long)((ShkbEmployeeMapper)this.getBaseMapper())
          .selectCount(new LambdaQueryWrapper<ShkbEmployee>().eq(ShkbEmployee::getStatus, 2))
         .intValue();
      long resigned = (long)((ShkbEmployeeMapper)this.getBaseMapper()).selectCount(new LambdaQueryWrapper<ShkbEmployee>().eq(ShkbEmployee::getStatus, 0)).intValue();
      statistics.put("total", total);
      statistics.put("active", active);
      statistics.put("probation", probation);
      statistics.put("resigned", resigned);
      return statistics;
   }
}
