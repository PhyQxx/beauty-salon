package com.beautysalon.service;

import com.beautysalon.entity.PosService;
import java.util.List;
import java.util.Map;

/**
 * 服务项目管理服务接口
 * 定义服务项目相关的业务操作
 *
 * @author BeautySalon Team
 */
public interface PosServiceService {

    /**
     * 根据ID获取服务项目详情
     *
     * @param id 项目ID
     * @return 服务项目详情
     */
    PosService getServiceById(Long id);

    /**
     * 分页查询服务项目列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @param keyword 关键词搜索
     * @param category 项目分类
     * @param isActive 是否上架
     * @return 分页结果
     */
    Map<String, Object> queryServicePage(Integer page, Integer limit, String keyword, String category, Integer isActive);

    /**
     * 获取所有服务分类
     *
     * @return 分类列表
     */
    List<String> getAllCategories();

    /**
     * 新增服务项目
     *
     * @param service 服务项目信息
     * @return 项目ID
     */
    Long createService(PosService service);

    /**
     * 更新服务项目
     *
     * @param id 项目ID
     * @param service 服务项目信息
     * @return 是否成功
     */
    boolean updateService(Long id, PosService service);

    /**
     * 删除服务项目（逻辑删除）
     *
     * @param id 项目ID
     * @return 是否成功
     */
    boolean deleteService(Long id);

    /**
     * 上下架服务项目
     *
     * @param id 项目ID
     * @param isActive 是否上架
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer isActive);

    /**
     * 获取所有上架的服务项目（下拉框用）
     *
     * @return 服务项目列表
     */
    List<PosService> getActiveServices();
}
