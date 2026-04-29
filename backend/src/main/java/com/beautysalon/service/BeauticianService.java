package com.beautysalon.service;

import com.beautysalon.entity.Beautician;

import java.util.List;
import java.util.Map;

/**
 * 美容师服务接口
 *
 * @author BeautySalon Team
 */
public interface BeauticianService {

    /**
     * 分页查询美容师列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @param keyword 关键词搜索
     * @param status 状态筛选
     * @return 分页结果
     */
    Map<String, Object> queryBeauticianPage(Integer page, Integer limit, String keyword, Integer status);

    /**
     * 根据ID获取美容师详情
     *
     * @param id 美容师ID
     * @return 美容师详情
     */
    Beautician getBeauticianById(Long id);

    /**
     * 新增美容师
     *
     * @param beautician 美容师信息
     * @return 美容师ID
     */
    Long createBeautician(Beautician beautician);

    /**
     * 更新美容师信息
     *
     * @param id 美容师ID
     * @param beautician 美容师信息
     * @return 是否成功
     */
    boolean updateBeautician(Long id, Beautician beautician);

    /**
     * 删除美容师（逻辑删除）
     *
     * @param id 美容师ID
     * @return 是否成功
     */
    boolean deleteBeautician(Long id);

    /**
     * 查询在职美容师列表
     *
     * @return 在职美容师列表
     */
    List<Beautician> getActiveBeauticians();

    /**
     * 更新美容师状态
     *
     * @param id 美容师ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);
}
