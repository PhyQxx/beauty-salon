package com.beautysalon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beautysalon.entity.PosMembershipCard;
import com.beautysalon.mapper.PosMembershipCardMapper;
import com.beautysalon.service.PosMembershipCardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 会员卡/套餐服务实现类
 * 实现会员卡相关的所有业务逻辑
 *
 * @author BeautySalon Team
 */
@Slf4j
@Service
public class PosMembershipCardServiceImpl implements PosMembershipCardService {

    @Resource
    private PosMembershipCardMapper cardMapper;

    /**
     * 会员卡类型映射
     */
    private static final Map<Integer, String> TYPE_MAP = new HashMap<>();
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();

    static {
        TYPE_MAP.put(1, "充值卡");
        TYPE_MAP.put(2, "次卡");
        TYPE_MAP.put(3, "时间卡");

        STATUS_MAP.put(0, "下架");
        STATUS_MAP.put(1, "上架");
    }

    /**
     * 根据ID获取会员卡详情
     */
    @Override
    public PosMembershipCard getCardById(Long id) {
        PosMembershipCard card = cardMapper.selectById(id);
        if (card == null) {
            throw new RuntimeException("会员卡不存在");
        }
        return card;
    }

    /**
     * 分页查询会员卡列表
     */
    @Override
    public Map<String, Object> queryCardPage(Integer page, Integer limit, String keyword, Integer type, Integer isActive) {
        LambdaQueryWrapper<PosMembershipCard> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(PosMembershipCard::getName, keyword)
                    .or()
                    .like(PosMembershipCard::getCode, keyword)
                    .or()
                    .like(PosMembershipCard::getDescription, keyword));
        }

        // 类型筛选
        if (type != null) {
            wrapper.eq(PosMembershipCard::getType, type);
        }

        // 上架状态筛选
        if (isActive != null) {
            wrapper.eq(PosMembershipCard::getIsActive, isActive);
        }

        wrapper.orderByDesc(PosMembershipCard::getCreateTime);

        Page<PosMembershipCard> pageParam = new Page<>(page, limit);
        IPage<PosMembershipCard> pageResult = cardMapper.selectPage(pageParam, wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("page", pageResult.getCurrent());
        result.put("limit", pageResult.getSize());
        result.put("pages", pageResult.getPages());

        return result;
    }

    /**
     * 新增会员卡
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCard(PosMembershipCard card) {
        // 检查编码是否已存在
        if (StringUtils.hasText(card.getCode())) {
            PosMembershipCard existCard = cardMapper.selectByCode(card.getCode());
            if (existCard != null) {
                throw new RuntimeException("卡编码已存在");
            }
        }

        // 设置默认值
        if (card.getIsActive() == null) {
            card.setIsActive(1);
        }

        card.setCreateTime(LocalDateTime.now());
        card.setUpdateTime(LocalDateTime.now());

        cardMapper.insert(card);
        log.info("创建会员卡成功: id={}, name={}", card.getId(), card.getName());

        return card.getId();
    }

    /**
     * 更新会员卡
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCard(Long id, PosMembershipCard card) {
        PosMembershipCard existCard = cardMapper.selectById(id);
        if (existCard == null) {
            throw new RuntimeException("会员卡不存在");
        }

        // 如果更新编码，检查是否重复
        if (StringUtils.hasText(card.getCode()) && !card.getCode().equals(existCard.getCode())) {
            PosMembershipCard duplicateCard = cardMapper.selectByCode(card.getCode());
            if (duplicateCard != null) {
                throw new RuntimeException("卡编码已存在");
            }
            existCard.setCode(card.getCode());
        }

        // 选择性更新非空字段
        if (StringUtils.hasText(card.getName())) {
            existCard.setName(card.getName());
        }
        if (card.getType() != null) {
            existCard.setType(card.getType());
        }
        if (card.getPrice() != null) {
            existCard.setPrice(card.getPrice());
        }
        if (card.getFaceValue() != null) {
            existCard.setFaceValue(card.getFaceValue());
        }
        if (card.getDurationDays() != null) {
            existCard.setDurationDays(card.getDurationDays());
        }
        if (StringUtils.hasText(card.getDescription())) {
            existCard.setDescription(card.getDescription());
        }
        if (StringUtils.hasText(card.getBenefitDesc())) {
            existCard.setBenefitDesc(card.getBenefitDesc());
        }
        if (card.getIsActive() != null) {
            existCard.setIsActive(card.getIsActive());
        }

        existCard.setUpdateTime(LocalDateTime.now());
        int rows = cardMapper.updateById(existCard);

        log.info("更新会员卡: id={}, rows={}", id, rows);
        return rows > 0;
    }

    /**
     * 删除会员卡（逻辑删除）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCard(Long id) {
        PosMembershipCard card = cardMapper.selectById(id);
        if (card == null) {
            throw new RuntimeException("会员卡不存在");
        }

        card.setDeleted(1);
        card.setUpdateTime(LocalDateTime.now());
        int rows = cardMapper.updateById(card);

        log.info("删除会员卡: id={}, rows={}", id, rows);
        return rows > 0;
    }

    /**
     * 上下架会员卡
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer isActive) {
        PosMembershipCard card = cardMapper.selectById(id);
        if (card == null) {
            throw new RuntimeException("会员卡不存在");
        }

        card.setIsActive(isActive);
        card.setUpdateTime(LocalDateTime.now());
        int rows = cardMapper.updateById(card);

        log.info("更新会员卡状态: id={}, isActive={}, rows={}", id, isActive, rows);
        return rows > 0;
    }

    /**
     * 获取所有上架的会员卡
     */
    @Override
    public List<PosMembershipCard> getActiveCards() {
        LambdaQueryWrapper<PosMembershipCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PosMembershipCard::getIsActive, 1)
                .orderByAsc(PosMembershipCard::getType)
                .orderByAsc(PosMembershipCard::getName);
        return cardMapper.selectList(wrapper);
    }
}
