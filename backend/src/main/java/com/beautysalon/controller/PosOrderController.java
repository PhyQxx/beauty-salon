package com.beautysalon.controller;

import com.beautysalon.common.Result;
import com.beautysalon.dto.OrderCreateDTO;
import com.beautysalon.service.OrderService;
import com.beautysalon.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单管理 Controller (POS模块)
 * 负责订单的创建、查询、支付、退款、日周月报表等操作
 *
 * @author BeautySalon Team
 */
@Api(tags = "订单管理")
@RestController
@RequestMapping("/pos/order")
public class PosOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 分页查询订单列表
     */
    @ApiOperation("分页查询订单列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer orderType,
            @RequestParam(required = false) Integer payStatus,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        List<OrderVO> list = orderService.listPage(page, limit, orderNo, customerId,
                orderType, payStatus, status, startDate, endDate);

        Map<String, Object> result = new HashMap<>();
        result.put("data", list);
        result.put("page", page);
        result.put("limit", limit);
        // total 由 Service 层统计后补充，当前先返回列表长度作为兼容
        result.put("total", list != null ? list.size() : 0);
        return Result.success("查询成功", result);
    }

    /**
     * 根据ID获取订单详情
     */
    @ApiOperation("根据ID获取订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> getById(@PathVariable Long id) {
        OrderVO order = orderService.getById(id);
        if (order == null) {
            return Result.notFound("订单不存在");
        }
        return Result.success("查询成功", order);
    }

    /**
     * 创建服务订单
     */
    @ApiOperation("创建服务订单")
    @PostMapping("/service")
    public Result<Map<String, Object>> createServiceOrder(@RequestBody OrderCreateDTO dto) {
        Map<String, Object> createResult = orderService.createServiceOrder(dto);
        if (Boolean.FALSE.equals(createResult.get("success"))) {
            return Result.error(createResult.get("message").toString());
        }
        return Result.success(createResult.get("message").toString(), createResult);
    }

    /**
     * 创建商品订单
     */
    @ApiOperation("创建商品订单")
    @PostMapping("/product")
    public Result<Map<String, Object>> createProductOrder(@RequestBody OrderCreateDTO dto) {
        Map<String, Object> createResult = orderService.createProductOrder(dto);
        if (Boolean.FALSE.equals(createResult.get("success"))) {
            return Result.error(createResult.get("message").toString());
        }
        return Result.success(createResult.get("message").toString(), createResult);
    }

    /**
     * 创建充值订单
     */
    @ApiOperation("创建充值订单")
    @PostMapping("/recharge")
    public Result<Map<String, Object>> createRechargeOrder(
            @RequestParam Long customerId,
            @RequestParam BigDecimal amount,
            @RequestParam Integer payType) {
        Map<String, Object> result = new HashMap<>();
        result.put("customerId", customerId);
        result.put("amount", amount);
        result.put("payType", payType);
        return Result.success("请使用充值接口进行充值操作", result);
    }

    /**
     * 创建套餐订单
     */
    @ApiOperation("创建套餐订单")
    @PostMapping("/package")
    public Result<Map<String, Object>> createPackageOrder(@RequestBody OrderCreateDTO dto) {
        Map<String, Object> createResult = orderService.createPackageOrder(dto);
        if (Boolean.FALSE.equals(createResult.get("success"))) {
            return Result.error(createResult.get("message").toString());
        }
        return Result.success(createResult.get("message").toString(), createResult);
    }

    /**
     * 支付订单
     */
    @ApiOperation("支付订单")
    @PutMapping("/{id}/pay")
    public Result<Map<String, Object>> pay(
            @PathVariable Long id,
            @RequestParam Integer payType,
            @RequestParam(required = false) Long operatorId) {
        Map<String, Object> payResult = orderService.pay(id, payType, operatorId);
        if (Boolean.FALSE.equals(payResult.get("success"))) {
            return Result.error(payResult.get("message").toString());
        }
        return Result.success(payResult.get("message").toString(), payResult);
    }

    /**
     * 退款
     */
    @ApiOperation("退款")
    @PutMapping("/{id}/refund")
    public Result<Map<String, Object>> refund(
            @PathVariable Long id,
            @RequestParam BigDecimal refundAmount,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) Long operatorId) {
        Map<String, Object> refundResult = orderService.refund(id, refundAmount, reason, operatorId);
        if (Boolean.FALSE.equals(refundResult.get("success"))) {
            return Result.error(refundResult.get("message").toString());
        }
        return Result.success(refundResult.get("message").toString(), refundResult);
    }

    /**
     * 取消订单
     */
    @ApiOperation("取消订单")
    @PutMapping("/{id}/cancel")
    public Result<Map<String, Object>> cancel(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) Long operatorId) {
        Map<String, Object> cancelResult = orderService.cancel(id, reason, operatorId);
        if (Boolean.FALSE.equals(cancelResult.get("success"))) {
            return Result.error(cancelResult.get("message").toString());
        }
        return Result.success(cancelResult.get("message").toString(), cancelResult);
    }

    /**
     * 完成订单
     */
    @ApiOperation("完成订单")
    @PutMapping("/{id}/complete")
    public Result<Map<String, Object>> complete(
            @PathVariable Long id,
            @RequestParam(required = false) Long operatorId) {
        Map<String, Object> completeResult = orderService.complete(id, operatorId);
        if (Boolean.FALSE.equals(completeResult.get("success"))) {
            return Result.error(completeResult.get("message").toString());
        }
        return Result.success(completeResult.get("message").toString(), completeResult);
    }

    /**
     * 使用套餐明细
     */
    @ApiOperation("使用套餐明细")
    @PutMapping("/item/{itemId}/use")
    public Result<Map<String, Object>> usePackageItem(
            @PathVariable Long itemId,
            @RequestParam(required = false) Long beauticianId) {
        Map<String, Object> useResult = orderService.usePackageItem(itemId, beauticianId);
        if (Boolean.FALSE.equals(useResult.get("success"))) {
            return Result.error(useResult.get("message").toString());
        }
        return Result.success(useResult.get("message").toString(), useResult);
    }

    /**
     * 获取日结报表
     */
    @ApiOperation("获取日结报表")
    @GetMapping("/daily-report")
    public Result<Map<String, Object>> getDailyReport(@RequestParam String date) {
        Map<String, Object> report = orderService.getDailyReport(date);
        return Result.success("查询成功", report);
    }

    /**
     * 获取周报表
     */
    @ApiOperation("获取周报表")
    @GetMapping("/weekly-report")
    public Result<Map<String, Object>> getWeeklyReport(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        Map<String, Object> report = orderService.getWeeklyReport(startDate, endDate);
        return Result.success("查询成功", report);
    }

    /**
     * 获取月报表
     */
    @ApiOperation("获取月报表")
    @GetMapping("/monthly-report")
    public Result<Map<String, Object>> getMonthlyReport(@RequestParam String yearMonth) {
        Map<String, Object> report = orderService.getMonthlyReport(yearMonth);
        return Result.success("查询成功", report);
    }

    /**
     * 获取营业统计
     */
    @ApiOperation("获取营业统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        Map<String, Object> statistics = orderService.getStatistics(startDate, endDate);
        return Result.success("查询成功", statistics);
    }

    /**
     * 根据客户ID获取订单列表
     */
    @ApiOperation("根据客户ID获取订单列表")
    @GetMapping("/customer/{customerId}")
    public Result<List<OrderVO>> getByCustomerId(@PathVariable Long customerId) {
        List<OrderVO> list = orderService.getByCustomerId(customerId);
        return Result.success("查询成功", list);
    }

    /**
     * 根据订单号获取订单
     */
    @ApiOperation("根据订单号获取订单")
    @GetMapping("/no/{orderNo}")
    public Result<OrderVO> getByOrderNo(@PathVariable String orderNo) {
        OrderVO order = orderService.getByOrderNo(orderNo);
        if (order == null) {
            return Result.notFound("订单不存在");
        }
        return Result.success("查询成功", order);
    }
}
