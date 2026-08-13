package com.lframework.xingyun.core.mappers;

import com.lframework.xingyun.core.dto.GenerateCodeDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GenerateCodeMapper {

  GenerateCodeDto findById(Integer id);
}
