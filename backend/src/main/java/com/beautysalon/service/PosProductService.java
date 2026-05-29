package com.beautysalon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.beautysalon.entity.PosProduct;

import java.util.List;
import java.util.Map;

/**
 * 产品信息服务接口
 */
public interface PosProductService extends IService<PosProduct> {
    
    /**
     * 分页查询产品
     */
    Map<String, Object> queryProductPage(Map<String, Object> params);
    
    /**
     * 根据编码查询产品
     */
    PosProduct getByCode(String code);
    
    /**
     * 检查编码是否存在
     */
    boolean isCodeExists(String code, Long excludeId);
}
