package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.bo.participant.QueryTrainingParticipantBo;
import com.lframework.xingyun.shkb.entity.ShkbTrainingParticipant;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ShkbTrainingParticipantMapper
extends BaseMapper<ShkbTrainingParticipant> {
    public List<QueryTrainingParticipantBo> query(@Param(value="implementationId") String var1);

    public int physicalDeleteById(@Param(value="id") String var1);

    public int physicalDeleteByImplementationId(@Param(value="implementationId") String var1);
}


