package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.bo.certificate.CertificateStatisticsBo;
import com.lframework.xingyun.shkb.bo.certificate.GetShkbEmployeeCertificateBo;
import com.lframework.xingyun.shkb.bo.certificate.QueryShkbEmployeeCertificateBo;
import com.lframework.xingyun.shkb.entity.ShkbEmployeeCertificate;
import com.lframework.xingyun.shkb.vo.employee.CreateShkbEmployeeCertificateVo;
import com.lframework.xingyun.shkb.vo.employee.QueryShkbEmployeeCertificateVo;
import com.lframework.xingyun.shkb.vo.employee.UpdateShkbEmployeeCertificateVo;
import java.util.List;

public interface ShkbEmployeeCertificateService
extends BaseMpService<ShkbEmployeeCertificate> {
    public ShkbEmployeeCertificate findById(String var1);

    public PageResult<QueryShkbEmployeeCertificateBo> query(Integer var1, Integer var2, QueryShkbEmployeeCertificateVo var3);

    public List<ShkbEmployeeCertificate> queryByEmployeeId(String var1);

    public GetShkbEmployeeCertificateBo getDetail(String var1);

    public void create(CreateShkbEmployeeCertificateVo var1);

    public void update(UpdateShkbEmployeeCertificateVo var1);

    public void deleteById(String var1);

    public void deleteByIds(List<String> var1);

    public CertificateStatisticsBo getStatistics();
}
