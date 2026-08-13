package com.lframework.xingyun.shkb.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.shkb.bo.contract.GetContractBo;
import com.lframework.xingyun.shkb.bo.contract.QueryContractBo;
import com.lframework.xingyun.shkb.entity.Contract;
import com.lframework.xingyun.shkb.vo.contract.QueryContractVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_contract(合同表)】的数据库操作Mapper
* @createDate 2025-04-23 16:55:28
* @Entity com.lframework.xingyun.shkb.entity.Contract
*/
@Mapper
public interface ContractMapper extends BaseMapper<Contract> {

    /**
     * 查询合同列表
     *
     * @param vo 查询条件
     * @return 合同列表
     */
    List<QueryContractBo> query(QueryContractVo vo);
    
    /**
     * 获取合同详情
     *
     * @param id 合同ID
     * @return 合同详情
     */
    GetContractBo getDetail(String id);
}
