package com.beautysalon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beautysalon.entity.PosInventory;
import com.beautysalon.entity.PosInventoryRecord;
import com.beautysalon.entity.PosProduct;
import com.beautysalon.mapper.PosInventoryMapper;
import com.beautysalon.mapper.PosInventoryRecordMapper;
import com.beautysalon.mapper.PosProductMapper;
import com.beautysalon.service.PosInventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 库存服务实现类
 */
@Service
public class PosInventoryServiceImpl extends ServiceImpl<PosInventoryMapper, PosInventory> implements PosInventoryService {

    @Resource
    private PosInventoryRecordMapper recordMapper;

    @Resource
    private PosProductMapper productMapper;

    @Override
    public Map<String, Object> queryInventoryPage(Map<String, Object> params) {
        long current = Long.parseLong(params.getOrDefault("page", "1").toString());
        long size = Long.parseLong(params.getOrDefault("limit", "10").toString());
        String keyword = (String) params.get("keyword");
        Long storeId = params.get("storeId") != null ? Long.parseLong(params.get("storeId").toString()) : null;

        LambdaQueryWrapper<PosInventory> wrapper = new LambdaQueryWrapper<>();
        if (storeId != null) {
            wrapper.eq(PosInventory::getStoreId, storeId);
        }
        
        // 如果有关键字，先查产品ID列表
        if (StringUtils.hasText(keyword)) {
            LambdaQueryWrapper<PosProduct> productWrapper = new LambdaQueryWrapper<>();
            productWrapper.and(w -> w.like(PosProduct::getName, keyword).or().like(PosProduct::getCode, keyword));
            java.util.List<Long> productIds = productMapper.selectList(productWrapper).stream()
                    .map(PosProduct::getId).collect(Collectors.toList());
            if (productIds.isEmpty()) {
                // 返回空结果
                Map<String, Object> emptyResult = new HashMap<>();
                emptyResult.put("list", new java.util.ArrayList<>());
                emptyResult.put("total", 0L);
                emptyResult.put("page", current);
                emptyResult.put("limit", size);
                emptyResult.put("pages", 0L);
                return emptyResult;
            }
            wrapper.in(PosInventory::getProductId, productIds);
        }

        Page<PosInventory> page = new Page<>(current, size);
        IPage<PosInventory> pageResult = this.page(page, wrapper);

        // 填充产品名称等信息
        java.util.List<Map<String, Object>> list = pageResult.getRecords().stream().map(inventory -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", inventory.getId());
            map.put("productId", inventory.getProductId());
            map.put("storeId", inventory.getStoreId());
            map.put("stockQuantity", inventory.getStockQuantity());
            map.put("lastInventoryTime", inventory.getLastInventoryTime());
            map.put("updateTime", inventory.getUpdateTime());
            
            PosProduct product = productMapper.selectById(inventory.getProductId());
            if (product != null) {
                map.put("productName", product.getName());
                map.put("productCode", product.getCode());
                map.put("category", product.getCategory());
                map.put("spec", product.getSpec());
                map.put("unit", product.getUnit());
                map.put("minStock", product.getMinStock());
            }
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", pageResult.getTotal());
        result.put("page", pageResult.getCurrent());
        result.put("limit", pageResult.getSize());
        result.put("pages", pageResult.getPages());

        return result;
    }

    @Override
    public PosInventory getByProductAndStore(Long productId, Long storeId) {
        return this.getOne(new LambdaQueryWrapper<PosInventory>()
                .eq(PosInventory::getProductId, productId)
                .eq(PosInventory::getStoreId, storeId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean stockIn(Long productId, Long storeId, Integer quantity, Integer type, Long operatorId, String orderNo, String remark) {
        if (quantity <= 0) {
            throw new RuntimeException("入库数量必须大于0");
        }

        PosInventory inventory = getByProductAndStore(productId, storeId);
        int beforeQuantity = 0;
        if (inventory == null) {
            inventory = new PosInventory();
            inventory.setProductId(productId);
            inventory.setStoreId(storeId);
            inventory.setStockQuantity(quantity);
            this.save(inventory);
        } else {
            beforeQuantity = inventory.getStockQuantity();
            inventory.setStockQuantity(beforeQuantity + quantity);
            this.updateById(inventory);
        }

        // 记录流水
        saveRecord(productId, storeId, type, quantity, beforeQuantity, inventory.getStockQuantity(), operatorId, orderNo, remark);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean stockOut(Long productId, Long storeId, Integer quantity, Integer type, Long operatorId, String orderNo, String remark) {
        if (quantity <= 0) {
            throw new RuntimeException("出库数量必须大于0");
        }

        PosInventory inventory = getByProductAndStore(productId, storeId);
        if (inventory == null || inventory.getStockQuantity() < quantity) {
            throw new RuntimeException("库存不足，无法出库");
        }

        int beforeQuantity = inventory.getStockQuantity();
        inventory.setStockQuantity(beforeQuantity - quantity);
        this.updateById(inventory);

        // 记录流水
        saveRecord(productId, storeId, type, -quantity, beforeQuantity, inventory.getStockQuantity(), operatorId, orderNo, remark);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean stockAdjustment(Long productId, Long storeId, Integer targetQuantity, Long operatorId, String remark) {
        if (targetQuantity < 0) {
            throw new RuntimeException("盘点目标数量不能小于0");
        }

        PosInventory inventory = getByProductAndStore(productId, storeId);
        int beforeQuantity = 0;
        if (inventory == null) {
            inventory = new PosInventory();
            inventory.setProductId(productId);
            inventory.setStoreId(storeId);
            inventory.setStockQuantity(targetQuantity);
            inventory.setLastInventoryTime(LocalDateTime.now());
            this.save(inventory);
        } else {
            beforeQuantity = inventory.getStockQuantity();
            inventory.setStockQuantity(targetQuantity);
            inventory.setLastInventoryTime(LocalDateTime.now());
            this.updateById(inventory);
        }

        // 记录流水 (变动类型 5-盘点调整)
        saveRecord(productId, storeId, 5, targetQuantity - beforeQuantity, beforeQuantity, targetQuantity, operatorId, null, remark);

        return true;
    }

    private void saveRecord(Long productId, Long storeId, Integer type, Integer quantity, Integer before, Integer after, Long operatorId, String orderNo, String remark) {
        PosInventoryRecord record = new PosInventoryRecord();
        record.setProductId(productId);
        record.setStoreId(storeId);
        record.setType(type);
        record.setQuantity(quantity);
        record.setBeforeQuantity(before);
        record.setAfterQuantity(after);
        record.setOperatorId(operatorId);
        record.setOrderNo(orderNo);
        record.setRemark(remark);
        record.setCreateTime(LocalDateTime.now());
        recordMapper.insert(record);
    }
}
