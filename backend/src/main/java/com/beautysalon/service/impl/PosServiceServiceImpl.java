package com.beautysalon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beautysalon.entity.PosService;
import com.beautysalon.mapper.PosServiceMapper;
import com.beautysalon.service.PosServiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 服务项目管理服务实现类
 * 实现服务项目相关的所有业务逻辑
 *
 * @author BeautySalon Team
 */
@Slf4j
@Service
public class PosServiceServiceImpl implements PosServiceService {

    @Resource
    private PosServiceMapper serviceMapper;

    /**
     * 根据ID获取服务项目详情
     */
    @Override
    public PosService getServiceById(Long id) {
        PosService service = serviceMapper.selectById(id);
        if (service == null) {
            throw new RuntimeException("服务项目不存在");
        }
        return service;
    }

    /**
     * 分页查询服务项目列表
     */
    @Override
    public Map<String, Object> queryServicePage(Integer page, Integer limit, String keyword, String category, Integer isActive) {
        LambdaQueryWrapper<PosService> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(PosService::getName, keyword)
                    .or()
                    .like(PosService::getCode, keyword)
                    .or()
                    .like(PosService::getDescription, keyword));
        }

        // 分类筛选
        if (StringUtils.hasText(category)) {
            wrapper.eq(PosService::getCategory, category);
        }

        // 上架状态筛选
        if (isActive != null) {
            wrapper.eq(PosService::getIsActive, isActive);
        }

        wrapper.orderByDesc(PosService::getCreateTime);

        Page<PosService> pageParam = new Page<>(page, limit);
        IPage<PosService> pageResult = serviceMapper.selectPage(pageParam, wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("page", pageResult.getCurrent());
        result.put("limit", pageResult.getSize());
        result.put("pages", pageResult.getPages());

        return result;
    }

    /**
     * 获取所有服务分类
     */
    @Override
    public List<String> getAllCategories() {
        LambdaQueryWrapper<PosService> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(PosService::getCategory)
                .isNotNull(PosService::getCategory)
                .eq(PosService::getIsActive, 1)
                .groupBy(PosService::getCategory);
        List<PosService> services = serviceMapper.selectList(wrapper);
        return services.stream()
                .map(PosService::getCategory)
                .filter(c -> StringUtils.hasText(c))
                .collect(Collectors.toList());
    }

    /**
     * 新增服务项目
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createService(PosService service) {
        // 检查编码是否已存在
        if (StringUtils.hasText(service.getCode())) {
            PosService existService = serviceMapper.selectByCode(service.getCode());
            if (existService != null) {
                throw new RuntimeException("项目编码已存在");
            }
        }

        // 设置默认值
        if (service.getIsActive() == null) {
            service.setIsActive(1);
        }
        if (service.getDuration() == null) {
            service.setDuration(60);
        }

        service.setCreateTime(LocalDateTime.now());
        service.setUpdateTime(LocalDateTime.now());

        serviceMapper.insert(service);
        log.info("创建服务项目成功: id={}, name={}", service.getId(), service.getName());

        return service.getId();
    }

    /**
     * 更新服务项目
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateService(Long id, PosService service) {
        PosService existService = serviceMapper.selectById(id);
        if (existService == null) {
            throw new RuntimeException("服务项目不存在");
        }

        // 如果更新编码，检查是否重复
        if (StringUtils.hasText(service.getCode()) && !service.getCode().equals(existService.getCode())) {
            PosService duplicateService = serviceMapper.selectByCode(service.getCode());
            if (duplicateService != null) {
                throw new RuntimeException("项目编码已存在");
            }
            existService.setCode(service.getCode());
        }

        // 选择性更新非空字段
        if (StringUtils.hasText(service.getName())) {
            existService.setName(service.getName());
        }
        if (StringUtils.hasText(service.getCategory())) {
            existService.setCategory(service.getCategory());
        }
        if (StringUtils.hasText(service.getDescription())) {
            existService.setDescription(service.getDescription());
        }
        if (service.getDuration() != null) {
            existService.setDuration(service.getDuration());
        }
        if (service.getPrice() != null) {
            existService.setPrice(service.getPrice());
        }
        if (service.getCost() != null) {
            existService.setCost(service.getCost());
        }
        if (StringUtils.hasText(service.getImageUrl())) {
            existService.setImageUrl(service.getImageUrl());
        }
        if (service.getIsActive() != null) {
            existService.setIsActive(service.getIsActive());
        }

        existService.setUpdateTime(LocalDateTime.now());
        int rows = serviceMapper.updateById(existService);

        log.info("更新服务项目: id={}, rows={}", id, rows);
        return rows > 0;
    }

    /**
     * 删除服务项目（逻辑删除）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteService(Long id) {
        PosService service = serviceMapper.selectById(id);
        if (service == null) {
            throw new RuntimeException("服务项目不存在");
        }

        service.setDeleted(1);
        service.setUpdateTime(LocalDateTime.now());
        int rows = serviceMapper.updateById(service);

        log.info("删除服务项目: id={}, rows={}", id, rows);
        return rows > 0;
    }

    /**
     * 上下架服务项目
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer isActive) {
        PosService service = serviceMapper.selectById(id);
        if (service == null) {
            throw new RuntimeException("服务项目不存在");
        }

        service.setIsActive(isActive);
        service.setUpdateTime(LocalDateTime.now());
        int rows = serviceMapper.updateById(service);

        log.info("更新服务项目状态: id={}, isActive={}, rows={}", id, isActive, rows);
        return rows > 0;
    }

    /**
     * 获取所有上架的服务项目（下拉框用）
     */
    @Override
    public List<PosService> getActiveServices() {
        LambdaQueryWrapper<PosService> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PosService::getIsActive, 1)
                .orderByAsc(PosService::getCategory)
                .orderByAsc(PosService::getName);
        return serviceMapper.selectList(wrapper);
    }
}
