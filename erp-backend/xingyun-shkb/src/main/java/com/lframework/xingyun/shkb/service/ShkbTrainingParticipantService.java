package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.bo.participant.GetTrainingParticipantBo;
import com.lframework.xingyun.shkb.bo.participant.QueryTrainingParticipantBo;
import com.lframework.xingyun.shkb.entity.ShkbTrainingParticipant;
import com.lframework.xingyun.shkb.vo.participant.CreateTrainingParticipantVo;
import com.lframework.xingyun.shkb.vo.participant.UpdateTrainingParticipantVo;

public interface ShkbTrainingParticipantService
extends BaseMpService<ShkbTrainingParticipant> {
    public PageResult<QueryTrainingParticipantBo> query(Integer var1, Integer var2, String var3);

    public GetTrainingParticipantBo getDetail(String var1);

    public void create(CreateTrainingParticipantVo var1);

    public void update(UpdateTrainingParticipantVo var1);

    public void deleteById(String var1);

    public void deleteByImplementationId(String var1);
}
