package com.lframework.xingyun.shkb.service.contract;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.bo.contract.GetContractBo;
import com.lframework.xingyun.shkb.bo.contract.QueryContractBo;
import com.lframework.xingyun.shkb.entity.Contract;
import com.lframework.xingyun.shkb.vo.contract.CreateContractVo;
import com.lframework.xingyun.shkb.vo.contract.QueryContractVo;
import com.lframework.xingyun.shkb.vo.contract.UpdateContractVo;
import com.lframework.xingyun.shkb.vo.contract.UpdateContractStatusVo;

import java.util.List;

/**
 * 合同服务接口
 *
 * @author kison
 */
public interface ContractService extends BaseMpService<Contract> {

    /**
     * 查询合同列表
     *
     * @param pageIndex 页码
     * @param pageSize  每页记录数
     * @param vo        参数
     * @return 合同列表
     */
    PageResult<QueryContractBo> query(Integer pageIndex, Integer pageSize, QueryContractVo vo);

    /**
     * 查询合同列表
     *
     * @param vo 参数
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

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return 合同
     */
    Contract findById(String id);

    /**
     * 创建合同
     *
     * @param vo 参数
     * @return 合同ID
     */
    String create(CreateContractVo vo);

    /**
     * 修改合同
     *
     * @param vo 参数
     */
    void update(UpdateContractVo vo);

    /**
     * 根据ID删除
     *
     * @param id ID
     */
    void deleteById(String id);

    /**
     * 清除缓存
     *
     * @param key 缓存键
     */
    void cleanCacheByKey(String key);
    
    /**
     * 修改合同状态
     *
     * @param vo 修改合同状态参数
     */
    void updateStatus(UpdateContractStatusVo vo);
}
