package com.beautysalon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beautysalon.entity.Beautician;
import com.beautysalon.mapper.BeauticianMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 美容师服务实现类
 *
 * @author BeautySalon Team
 */
@Slf4j
@Service
public class BeauticianServiceImpl implements BeauticianService {

    @Autowired
    private BeauticianMapper beauticianMapper;

    @Override
    public Map<String, Object> queryBeauticianPage(Integer page, Integer limit, String keyword, Integer status) {
        Map<String, Object> result = new HashMap<>();

        Page<Beautician> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<Beautician> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（姓名/工号/手机号）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Beautician::getName, keyword)
                    .or().like(Beautician::getCode, keyword)
                    .or().like(Beautician::getPhone, keyword));
        }

        // 状态筛选
        if (status != null) {
            wrapper.eq(Beautician::getStatus, status);
        }

        // 只查询未删除的
        wrapper.eq(Beautician::getDeleted, 0);
        wrapper.orderByDesc(Beautician::getCreateTime);

        IPage<Beautician> iPage = beauticianMapper.selectPage(pageParam, wrapper);

        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", iPage.getRecords());
        result.put("total", iPage.getTotal());
        result.put("page", page);
        result.put("limit", limit);

        return result;
    }

    @Override
    public Beautician getBeauticianById(Long id) {
        Beautician beautician = beauticianMapper.selectById(id);
        if (beautician == null || beautician.getDeleted() == 1) {
            throw new RuntimeException("美容师不存在");
        }
        return beautician;
    }

    @Override
    public Long createBeautician(Beautician beautician) {
        // 检查工号是否重复
        if (beauticianMapper.selectByCode(beautician.getCode()) != null) {
            throw new RuntimeException("工号已存在");
        }

        // 检查手机号是否重复
        if (beauticianMapper.selectByPhone(beautician.getPhone()) != null) {
            throw new RuntimeException("手机号已存在");
        }

        beautician.setDeleted(0);
        beautician.setCreateTime(LocalDateTime.now());
        beautician.setUpdateTime(LocalDateTime.now());
        beautician.setServiceCount(0);
        if (beautician.getRating() == null) {
            beautician.setRating(new java.math.BigDecimal("5.00"));
        }
        if (beautician.getStatus() == null) {
            beautician.setStatus(1); // 默认在职
        }

        beauticianMapper.insert(beautician);
        log.info("新增美容师成功: id={}, name={}", beautician.getId(), beautician.getName());
        return beautician.getId();
    }

    @Override
    public boolean updateBeautician(Long id, Beautician beautician) {
        Beautician existing = getBeauticianById(id);

        // 检查工号是否重复（排除自己）
        Beautician byCode = beauticianMapper.selectByCode(beautician.getCode());
        if (byCode != null && !byCode.getId().equals(id)) {
            throw new RuntimeException("工号已存在");
        }

        // 检查手机号是否重复（排除自己）
        Beautician byPhone = beauticianMapper.selectByPhone(beautician.getPhone());
        if (byPhone != null && !byPhone.getId().equals(id)) {
            throw new RuntimeException("手机号已存在");
        }

        beautician.setId(id);
        beautician.setUpdateTime(LocalDateTime.now());
        beauticianMapper.updateById(beautician);
        log.info("更新美容师成功: id={}, name={}", id, beautician.getName());
        return true;
    }

    @Override
    public boolean deleteBeautician(Long id) {
        Beautician existing = getBeauticianById(id);
        existing.setDeleted(1);
        existing.setUpdateTime(LocalDateTime.now());
        beauticianMapper.updateById(existing);
        log.info("删除美容师成功: id={}", id);
        return true;
    }

    @Override
    public List<Beautician> getActiveBeauticians() {
        LambdaQueryWrapper<Beautician> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Beautician::getStatus, 1);
        wrapper.eq(Beautician::getDeleted, 0);
        wrapper.orderByDesc(Beautician::getLevel);
        return beauticianMapper.selectList(wrapper);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        Beautician existing = getBeauticianById(id);
        existing.setStatus(status);
        existing.setUpdateTime(LocalDateTime.now());
        beauticianMapper.updateById(existing);
        log.info("更新美容师状态: id={}, status={}", id, status);
        return true;
    }
}
