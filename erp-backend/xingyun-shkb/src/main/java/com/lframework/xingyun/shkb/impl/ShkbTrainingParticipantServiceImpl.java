package com.lframework.xingyun.shkb.impl;

import com.github.pagehelper.PageInfo;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.shkb.bo.participant.GetTrainingParticipantBo;
import com.lframework.xingyun.shkb.bo.participant.QueryTrainingParticipantBo;
import com.lframework.xingyun.shkb.entity.ShkbTrainingParticipant;
import com.lframework.xingyun.shkb.mappers.ShkbTrainingParticipantMapper;
import com.lframework.xingyun.shkb.service.ShkbTrainingParticipantService;
import com.lframework.xingyun.shkb.vo.participant.CreateTrainingParticipantVo;
import com.lframework.xingyun.shkb.vo.participant.UpdateTrainingParticipantVo;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShkbTrainingParticipantServiceImpl
   extends BaseMpServiceImpl<ShkbTrainingParticipantMapper, ShkbTrainingParticipant>
   implements ShkbTrainingParticipantService {
   @Override
   public PageResult<QueryTrainingParticipantBo> query(Integer pageIndex, Integer pageSize, String implementationId) {
      PageHelperUtil.startPage(pageIndex, pageSize);
      List<QueryTrainingParticipantBo> datas = ((ShkbTrainingParticipantMapper)this.getBaseMapper()).query(implementationId);
      return PageResultUtil.convert(new PageInfo<>(datas));
   }

   @Override
   public GetTrainingParticipantBo getDetail(String id) {
      ShkbTrainingParticipant entity = (ShkbTrainingParticipant)this.getById(id);
      if (entity == null) {
         return null;
      } else {
         GetTrainingParticipantBo bo = new GetTrainingParticipantBo();
         bo.setId(entity.getId());
         bo.setImplementationId(entity.getImplementationId());
         bo.setEmployeeId(entity.getEmployeeId());
         bo.setTrainingResult(entity.getTrainingResult());
         bo.setCertificateNo(entity.getCertificateNo());
         bo.setStatus(entity.getStatus());
         return bo;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void create(CreateTrainingParticipantVo vo) {
      ShkbTrainingParticipant entity = new ShkbTrainingParticipant();
      entity.setImplementationId(vo.getImplementationId());
      entity.setEmployeeId(vo.getEmployeeId());
      entity.setTrainingResult(vo.getTrainingResult());
      entity.setCertificateNo(vo.getCertificateNo());
      entity.setStatus(vo.getStatus() != null ? vo.getStatus() : 0);
      this.save(entity);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void update(UpdateTrainingParticipantVo vo) {
      ShkbTrainingParticipant entity = (ShkbTrainingParticipant)this.getById(vo.getId());
      if (entity != null) {
         entity.setTrainingResult(vo.getTrainingResult());
         entity.setCertificateNo(vo.getCertificateNo());
         if (vo.getStatus() != null) {
            entity.setStatus(vo.getStatus());
         }

         this.updateById(entity);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteById(String id) {
      ((ShkbTrainingParticipantMapper)this.getBaseMapper()).physicalDeleteById(id);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteByImplementationId(String implementationId) {
      ((ShkbTrainingParticipantMapper)this.getBaseMapper()).physicalDeleteByImplementationId(implementationId);
   }
}
