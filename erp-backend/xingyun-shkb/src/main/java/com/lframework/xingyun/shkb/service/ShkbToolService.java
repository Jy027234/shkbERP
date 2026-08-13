package com.lframework.xingyun.shkb.service;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.shkb.entity.ShkbTool;
import com.lframework.xingyun.shkb.vo.tool.CreateShkbToolVo;
import com.lframework.xingyun.shkb.vo.tool.QueryShkbToolVo;
import com.lframework.xingyun.shkb.vo.tool.UpdateShkbToolVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author kison
* @description 针对表【shkb_tool(工具管理)】的数据库操作Service
* @createDate 2025-06-06 10:07:22
*/
public interface ShkbToolService extends BaseMpService<ShkbTool> {

    /**
     * 查询工具列表
     *
     * @param pageIndex 页码
     * @param pageSize  每页条数
     * @param vo        查询条件
     * @return 工具列表
     */
    PageResult<ShkbTool> query(Integer pageIndex, Integer pageSize, QueryShkbToolVo vo);

    /**
     * 根据ID查询工具
     *
     * @param id ID
     * @return 工具
     */
    ShkbTool findById(String id);

    /**
     * 修改工具
     *
     * @param vo 修改工具VO
     */
    void update(UpdateShkbToolVo vo);

    /**
     * 根据ID删除工具
     *
     * @param id ID
     */
    void deleteById(String id);

    /**
     * 批量删除工具
     *
     * @param ids ID列表
     */
    void deleteByIds(List<String> ids);
    
    /**
     * 创建工具
     *
     * @param vo 创建工具VO
     * @return 工具ID
     */
    String create(CreateShkbToolVo vo);
    
    /**
     * 创建工具并初始化计量记录
     *
     * @param vo 创建工具VO
     * @param files 附件文件列表
     * @return 工具ID
     */
    String create(CreateShkbToolVo vo, List<MultipartFile> files);
    
    /**
     * 更新计量工具的证书编号
     *
     * @param toolId 工具ID
     * @param certificateNumber 证书编号
     */
    void updateCertificateNumber(String toolId, String certificateNumber);
}
