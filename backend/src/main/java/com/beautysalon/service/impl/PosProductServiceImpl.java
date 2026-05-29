package com.beautysalon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beautysalon.entity.PosProduct;
import com.beautysalon.mapper.PosProductMapper;
import com.beautysalon.service.PosProductService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 产品信息服务实现类
 */
@Service
public class PosProductServiceImpl extends ServiceImpl<PosProductMapper, PosProduct> implements PosProductService {

    @Override
    public Map<String, Object> queryProductPage(Map<String, Object> params) {
        long current = Long.parseLong(params.getOrDefault("page", "1").toString());
        long size = Long.parseLong(params.getOrDefault("limit", "10").toString());
        String keyword = (String) params.get("keyword");
        String category = (String) params.get("category");
        
        Integer status = null;
        if (params.get("status") != null && StringUtils.hasText(params.get("status").toString())) {
            try {
                status = Integer.parseInt(params.get("status").toString());
            } catch (NumberFormatException ignored) {}
        }

        LambdaQueryWrapper<PosProduct> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(PosProduct::getName, keyword).or().like(PosProduct::getCode, keyword));
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(PosProduct::getCategory, category);
        }
        if (status != null) {
            wrapper.eq(PosProduct::getStatus, status);
        }
        wrapper.orderByDesc(PosProduct::getCreateTime);

        Page<PosProduct> page = new Page<>(current, size);
        IPage<PosProduct> pageResult = this.page(page, wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("page", pageResult.getCurrent());
        result.put("limit", pageResult.getSize());
        result.put("pages", pageResult.getPages());

        return result;
    }

    @Override
    public PosProduct getByCode(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        return this.getOne(new LambdaQueryWrapper<PosProduct>().eq(PosProduct::getCode, code));
    }

    @Override
    public boolean isCodeExists(String code, Long excludeId) {
        if (!StringUtils.hasText(code)) {
            return false;
        }
        LambdaQueryWrapper<PosProduct> wrapper = new LambdaQueryWrapper<PosProduct>()
                .eq(PosProduct::getCode, code);
        if (excludeId != null) {
            wrapper.ne(PosProduct::getId, excludeId);
        }
        return this.count(wrapper) > 0;
    }
}
