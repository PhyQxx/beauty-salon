package com.beautysalon.service;

import com.beautysalon.entity.PosMembershipCard;
import java.util.List;
import java.util.Map;

/**
 * 会员卡/套餐服务接口
 * 定义会员卡相关的业务操作
 *
 * @author BeautySalon Team
 */
public interface PosMembershipCardService {

    /**
     * 根据ID获取会员卡详情
     *
     * @param id 会员卡ID
     * @return 会员卡详情
     */
    PosMembershipCard getCardById(Long id);

    /**
     * 分页查询会员卡列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @param keyword 关键词搜索
     * @param type 卡类型
     * @param isActive 是否上架
     * @return 分页结果
     */
    Map<String, Object> queryCardPage(Integer page, Integer limit, String keyword, Integer type, Integer isActive);

    /**
     * 新增会员卡
     *
     * @param card 会员卡信息
     * @return 会员卡ID
     */
    Long createCard(PosMembershipCard card);

    /**
     * 更新会员卡
     *
     * @param id 会员卡ID
     * @param card 会员卡信息
     * @return 是否成功
     */
    boolean updateCard(Long id, PosMembershipCard card);

    /**
     * 删除会员卡（逻辑删除）
     *
     * @param id 会员卡ID
     * @return 是否成功
     */
    boolean deleteCard(Long id);

    /**
     * 上下架会员卡
     *
     * @param id 会员卡ID
     * @param isActive 是否上架
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer isActive);

    /**
     * 获取所有上架的会员卡
     *
     * @return 会员卡列表
     */
    List<PosMembershipCard> getActiveCards();
}
