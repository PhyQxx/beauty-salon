package com.beautysalon.controller;

import com.beautysalon.common.Result;
import com.beautysalon.service.PosInventoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 库存控制器
 */
@Api(tags = "库存管理")
@RestController
@RequestMapping("/pos/inventory")
public class PosInventoryController {

    @Resource
    private PosInventoryService inventoryService;

    @ApiOperation("分页查询库存")
    @GetMapping("/page")
    public Result<Map<String, Object>> queryPage(@RequestParam Map<String, Object> params) {
        return Result.success(inventoryService.queryInventoryPage(params));
    }

    @ApiOperation("入库操作")
    @PostMapping("/stock-in")
    public Result<Boolean> stockIn(@RequestBody Map<String, Object> params) {
        Long productId = Long.parseLong(params.get("productId").toString());
        Long storeId = Long.parseLong(params.get("storeId").toString());
        Integer quantity = Integer.parseInt(params.get("quantity").toString());
        Integer type = Integer.parseInt(params.get("type").toString());
        Long operatorId = params.get("operatorId") != null ? Long.parseLong(params.get("operatorId").toString()) : null;
        String orderNo = (String) params.get("orderNo");
        String remark = (String) params.get("remark");

        return Result.success(inventoryService.stockIn(productId, storeId, quantity, type, operatorId, orderNo, remark));
    }

    @ApiOperation("出库操作")
    @PostMapping("/stock-out")
    public Result<Boolean> stockOut(@RequestBody Map<String, Object> params) {
        Long productId = Long.parseLong(params.get("productId").toString());
        Long storeId = Long.parseLong(params.get("storeId").toString());
        Integer quantity = Integer.parseInt(params.get("quantity").toString());
        Integer type = Integer.parseInt(params.get("type").toString());
        Long operatorId = params.get("operatorId") != null ? Long.parseLong(params.get("operatorId").toString()) : null;
        String orderNo = (String) params.get("orderNo");
        String remark = (String) params.get("remark");

        try {
            return Result.success(inventoryService.stockOut(productId, storeId, quantity, type, operatorId, orderNo, remark));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("盘点调整")
    @PostMapping("/adjustment")
    public Result<Boolean> adjustment(@RequestBody Map<String, Object> params) {
        Long productId = Long.parseLong(params.get("productId").toString());
        Long storeId = Long.parseLong(params.get("storeId").toString());
        Integer targetQuantity = Integer.parseInt(params.get("targetQuantity").toString());
        Long operatorId = params.get("operatorId") != null ? Long.parseLong(params.get("operatorId").toString()) : null;
        String remark = (String) params.get("remark");

        return Result.success(inventoryService.stockAdjustment(productId, storeId, targetQuantity, operatorId, remark));
    }
}
