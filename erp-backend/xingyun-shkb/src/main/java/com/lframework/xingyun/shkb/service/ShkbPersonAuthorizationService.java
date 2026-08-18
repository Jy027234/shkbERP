package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.ShkbPersonAuthorization;
import com.lframework.xingyun.shkb.vo.authorization.CreateShkbPersonAuthorizationVo;
import com.lframework.xingyun.shkb.vo.authorization.PersonAuthorizationProjectVo;
import com.lframework.xingyun.shkb.vo.authorization.QueryShkbPersonAuthorizationVo;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ShkbPersonAuthorizationService
extends BaseMpService<ShkbPersonAuthorization> {
    public ShkbPersonAuthorization findById(String var1);

    public PageResult<ShkbPersonAuthorization> query(Integer var1, Integer var2, QueryShkbPersonAuthorizationVo var3);

    public List<ShkbPersonAuthorization> queryByEmployeeId(String var1);

    public String create(CreateShkbPersonAuthorizationVo var1, MultipartFile var2);

    public void update(String var1, String var2);

    public void updateProjects(String var1, List<PersonAuthorizationProjectVo> var2);

    public void extend(String var1, String var2, LocalDate var3);

    public void revoke(String var1);

    public void deleteById(String var1);

    public void deleteByIds(List<String> var1);
}
