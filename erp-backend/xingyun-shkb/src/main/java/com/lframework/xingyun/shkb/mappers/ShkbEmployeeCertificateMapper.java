package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.bo.certificate.QueryShkbEmployeeCertificateBo;
import com.lframework.xingyun.shkb.entity.ShkbEmployeeCertificate;
import com.lframework.xingyun.shkb.vo.employee.QueryShkbEmployeeCertificateVo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ShkbEmployeeCertificateMapper
extends BaseMapper<ShkbEmployeeCertificate> {
    public List<QueryShkbEmployeeCertificateBo> query(@Param(value="vo") QueryShkbEmployeeCertificateVo var1);
}


