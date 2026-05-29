package com.beautysalon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.beautysalon.entity.PosInventory;

import java.util.Map;

/**
 * 库存服务接口
 */
public interface PosInventoryService extends IService<PosInventory> {
    
    /**
     * 分页查询库存
     */
    Map<String, Object> queryInventoryPage(Map<String, Object> params);
    
    /**
     * 获取指定产品在指定门店的库存
     */
    PosInventory getByProductAndStore(Long productId, Long storeId);
    
    /**
     * 入库操作
     */
    boolean stockIn(Long productId, Long storeId, Integer quantity, Integer type, Long operatorId, String orderNo, String remark);
    
    /**
     * 出库操作
     */
    boolean stockOut(Long productId, Long storeId, Integer quantity, Integer type, Long operatorId, String orderNo, String remark);
    
    /**
     * 盘点调整
     */
    boolean stockAdjustment(Long productId, Long storeId, Integer targetQuantity, Long operatorId, String remark);
}
