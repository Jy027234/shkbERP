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
import com.lframework.xingyun.shkb.bo.certificate.CertificateStatisticsBo;
import com.lframework.xingyun.shkb.bo.certificate.GetShkbEmployeeCertificateBo;
import com.lframework.xingyun.shkb.bo.certificate.QueryShkbEmployeeCertificateBo;
import com.lframework.xingyun.shkb.entity.ShkbEmployee;
import com.lframework.xingyun.shkb.entity.ShkbEmployeeCertificate;
import com.lframework.xingyun.shkb.mappers.ShkbEmployeeCertificateMapper;
import com.lframework.xingyun.shkb.mappers.ShkbEmployeeMapper;
import com.lframework.xingyun.shkb.service.ShkbEmployeeCertificateService;
import com.lframework.xingyun.shkb.vo.employee.CreateShkbEmployeeCertificateVo;
import com.lframework.xingyun.shkb.vo.employee.QueryShkbEmployeeCertificateVo;
import com.lframework.xingyun.shkb.vo.employee.UpdateShkbEmployeeCertificateVo;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShkbEmployeeCertificateServiceImpl
   extends BaseMpServiceImpl<ShkbEmployeeCertificateMapper, ShkbEmployeeCertificate>
   implements ShkbEmployeeCertificateService {
   @Autowired
   private ShkbEmployeeMapper employeeMapper;

   @Override
   public ShkbEmployeeCertificate findById(String id) {
      return (ShkbEmployeeCertificate)((ShkbEmployeeCertificateMapper)this.getBaseMapper()).selectById(id);
   }

   @Override
   public PageResult<QueryShkbEmployeeCertificateBo> query(Integer pageIndex, Integer pageSize, QueryShkbEmployeeCertificateVo vo) {
      Assert.greaterThanZero(pageIndex);
      Assert.greaterThanZero(pageSize);
      PageHelperUtil.startPage(pageIndex, pageSize);
      List<QueryShkbEmployeeCertificateBo> datas = ((ShkbEmployeeCertificateMapper)this.getBaseMapper()).query(vo);
      return PageResultUtil.convert(new PageInfo<>(datas));
   }

   @Override
   public List<ShkbEmployeeCertificate> queryByEmployeeId(String employeeId) {
      LambdaQueryWrapper<ShkbEmployeeCertificate> wrapper = (Wrappers.lambdaQuery(
               ShkbEmployeeCertificate.class
            )
            .eq(ShkbEmployeeCertificate::getEmployeeId, employeeId))
         .orderByDesc(ShkbEmployeeCertificate::getCreateTime);
      return ((ShkbEmployeeCertificateMapper)this.getBaseMapper()).selectList(wrapper);
   }

   @Override
   public GetShkbEmployeeCertificateBo getDetail(String id) {
      ShkbEmployeeCertificate entity = (ShkbEmployeeCertificate)((ShkbEmployeeCertificateMapper)this.getBaseMapper()).selectById(id);
      if (entity == null) {
         return null;
      } else {
         GetShkbEmployeeCertificateBo bo = new GetShkbEmployeeCertificateBo();
         bo.setId(entity.getId());
         bo.setEmployeeId(entity.getEmployeeId());
         bo.setCertificateType(entity.getCertificateType());
         bo.setCertificateName(entity.getCertificateName());
         bo.setCertificateNo(entity.getCertificateNo());
         bo.setIssueOrg(entity.getIssueOrg());
         if (entity.getIssueDate() != null) {
            bo.setIssueDate(entity.getIssueDate().toString());
         }

         if (entity.getValidStartDate() != null) {
            bo.setValidStartDate(entity.getValidStartDate().toString());
         }

         if (entity.getValidEndDate() != null) {
            bo.setValidEndDate(entity.getValidEndDate().toString());
         }

         bo.setStatus(entity.getStatus());
         if (entity.getStatus() != null) {
            bo.setStatusText(entity.getStatus() == 1 ? "有效" : "过期");
         }

         bo.setFileUrl(entity.getFileUrl());
         bo.setDescription(entity.getDescription());
         if (entity.getEmployeeId() != null) {
            ShkbEmployee employee = (ShkbEmployee)this.employeeMapper.selectById(entity.getEmployeeId());
            if (employee != null) {
               bo.setEmployeeName(employee.getName());
            }
         }

         return bo;
      }
   }

   private LambdaQueryWrapper<ShkbEmployeeCertificate> getQueryWrapper(QueryShkbEmployeeCertificateVo vo) {
      LambdaQueryWrapper<ShkbEmployeeCertificate> wrapper = Wrappers.<ShkbEmployeeCertificate>lambdaQuery(ShkbEmployeeCertificate.class);
      if (vo != null) {
         if (StringUtil.isNotBlank(vo.getKeyword())) {
            ((wrapper.like(ShkbEmployeeCertificate::getCertificateName, vo.getKeyword())).or())
               .like(ShkbEmployeeCertificate::getCertificateNo, vo.getKeyword());
         }

         if (StringUtil.isNotBlank(vo.getEmployeeId())) {
            wrapper.eq(ShkbEmployeeCertificate::getEmployeeId, vo.getEmployeeId());
         }

         if (StringUtil.isNotBlank(vo.getCertificateType())) {
            wrapper.eq(ShkbEmployeeCertificate::getCertificateType, vo.getCertificateType());
         }

         if (vo.getStatus() != null) {
            wrapper.eq(ShkbEmployeeCertificate::getStatus, vo.getStatus());
         }
      }

      wrapper.orderByDesc(ShkbEmployeeCertificate::getCreateTime);
      return wrapper;
   }

   @OpLog(
      type = 99,
      name = "新增员工证书，证书名称：{}",
      params = {"#certificateName"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void create(CreateShkbEmployeeCertificateVo vo) {
      ShkbEmployeeCertificate data = new ShkbEmployeeCertificate();
      data.setId(IdUtil.getId());
      data.setEmployeeId(vo.getEmployeeId());
      data.setCertificateType(vo.getCertificateType());
      data.setCertificateName(vo.getCertificateName());
      data.setCertificateNo(vo.getCertificateNo());
      data.setIssueOrg(vo.getIssueOrg());
      data.setIssueDate(vo.getIssueDate());
      data.setValidStartDate(vo.getValidStartDate());
      data.setValidEndDate(vo.getValidEndDate());
      data.setFileUrl(vo.getFileUrl());
      data.setDescription(StringUtil.isBlank(vo.getDescription()) ? "" : vo.getDescription());
      data.setStatus(vo.getStatus() != null ? vo.getStatus() : 1);
      data.setCreateBy(SecurityUtil.getCurrentUser().getName());
      data.setCreateById(SecurityUtil.getCurrentUser().getId());
      data.setUpdateBy(SecurityUtil.getCurrentUser().getName());
      data.setUpdateById(SecurityUtil.getCurrentUser().getId());
      ((ShkbEmployeeCertificateMapper)this.getBaseMapper()).insert(data);
      OpLogUtil.setVariable("certificateName", vo.getCertificateName());
   }

   @OpLog(
      type = 99,
      name = "修改员工证书，证书名称：{}",
      params = {"#certificateName"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void update(UpdateShkbEmployeeCertificateVo vo) {
      ShkbEmployeeCertificate data = (ShkbEmployeeCertificate)((ShkbEmployeeCertificateMapper)this.getBaseMapper()).selectById(vo.getId());
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("证书不存在！");
      } else {
         LambdaUpdateWrapper<ShkbEmployeeCertificate> updateWrapper = ((((((((((((((Wrappers.lambdaUpdate(
                                                         ShkbEmployeeCertificate.class
                                                      )
                                                      .set(ShkbEmployeeCertificate::getEmployeeId, vo.getEmployeeId()))
                                                   .set(ShkbEmployeeCertificate::getCertificateType, vo.getCertificateType()))
                                                .set(ShkbEmployeeCertificate::getCertificateName, vo.getCertificateName()))
                                             .set(ShkbEmployeeCertificate::getCertificateNo, vo.getCertificateNo()))
                                          .set(ShkbEmployeeCertificate::getIssueOrg, vo.getIssueOrg()))
                                       .set(ShkbEmployeeCertificate::getIssueDate, vo.getIssueDate()))
                                    .set(ShkbEmployeeCertificate::getValidStartDate, vo.getValidStartDate()))
                                 .set(ShkbEmployeeCertificate::getValidEndDate, vo.getValidEndDate()))
                              .set(ShkbEmployeeCertificate::getStatus, vo.getStatus()))
                           .set(ShkbEmployeeCertificate::getFileUrl, vo.getFileUrl()))
                        .set(ShkbEmployeeCertificate::getDescription, StringUtil.isBlank(vo.getDescription()) ? "" : vo.getDescription()))
                     .set(ShkbEmployeeCertificate::getUpdateBy, SecurityUtil.getCurrentUser().getName()))
                  .set(ShkbEmployeeCertificate::getUpdateById, SecurityUtil.getCurrentUser().getId()))
               .set(ShkbEmployeeCertificate::getUpdateTime, LocalDateTime.now()))
            .eq(ShkbEmployeeCertificate::getId, vo.getId());
         ((ShkbEmployeeCertificateMapper)this.getBaseMapper()).update(null, updateWrapper);
         OpLogUtil.setVariable("certificateName", vo.getCertificateName());
      }
   }

   @OpLog(
      type = 99,
      name = "删除员工证书，ID：{}",
      params = {"#id"}
   )
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteById(String id) {
      ShkbEmployeeCertificate data = (ShkbEmployeeCertificate)((ShkbEmployeeCertificateMapper)this.getBaseMapper()).selectById(id);
      if (ObjectUtil.isNull(data)) {
         throw new DefaultClientException("证书不存在！");
      } else {
         ((ShkbEmployeeCertificateMapper)this.getBaseMapper()).deleteById(id);
         OpLogUtil.setVariable("id", id);
      }
   }

   @OpLog(
      type = 99,
      name = "批量删除员工证书，数量：{}",
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
         throw new DefaultClientException("请选择要删除的证书！");
      }
   }

   @Override
   public CertificateStatisticsBo getStatistics() {
      LambdaQueryWrapper<ShkbEmployeeCertificate> wrapper = Wrappers.<ShkbEmployeeCertificate>lambdaQuery(ShkbEmployeeCertificate.class);
      List<ShkbEmployeeCertificate> allCertificates = ((ShkbEmployeeCertificateMapper)this.getBaseMapper()).selectList(wrapper);
      CertificateStatisticsBo stats = new CertificateStatisticsBo();
      stats.setTotal(allCertificates.size());
      stats.setValid((int)allCertificates.stream().filter(c -> c.getStatus() != null && c.getStatus() == 1).count());
      stats.setExpired((int)allCertificates.stream().filter(c -> c.getStatus() != null && c.getStatus() == 0).count());
      stats.setExpiring(0);
      return stats;
   }
}
