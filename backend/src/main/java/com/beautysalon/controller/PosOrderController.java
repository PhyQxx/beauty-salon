package com.beautysalon.controller;

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
     *
     * @param page 页码
     * @param limit 每页数量
     * @param orderNo 订单号
     * @param customerId 客户ID
     * @param orderType 订单类型
     * @param payStatus 支付状态
     * @param status 订单状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单列表
     */
    @ApiOperation("分页查询订单列表")
    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer orderType,
            @RequestParam(required = false) Integer payStatus,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<OrderVO> list = orderService.listPage(page, limit, orderNo, customerId, 
                    orderType, payStatus, status, startDate, endDate);
            result.put("success", true);
            result.put("data", list);
            result.put("page", page);
            result.put("limit", limit);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 根据ID获取订单详情
     *
     * @param id 订单ID
     * @return 订单详情（含明细）
     */
    @ApiOperation("根据ID获取订单详情")
    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            OrderVO order = orderService.getById(id);
            if (order == null) {
                result.put("success", false);
                result.put("message", "订单不存在");
            } else {
                result.put("success", true);
                result.put("data", order);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 创建服务订单
     *
     * @param dto 订单信息
     * @return 操作结果
     */
    @ApiOperation("创建服务订单")
    @PostMapping("/service")
    public Map<String, Object> createServiceOrder(@RequestBody OrderCreateDTO dto) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> createResult = orderService.createServiceOrder(dto);
            result.putAll(createResult);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 创建商品订单
     *
     * @param dto 订单信息
     * @return 操作结果
     */
    @ApiOperation("创建商品订单")
    @PostMapping("/product")
    public Map<String, Object> createProductOrder(@RequestBody OrderCreateDTO dto) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> createResult = orderService.createProductOrder(dto);
            result.putAll(createResult);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 创建充值订单
     *
     * @param customerId 客户ID
     * @param amount 充值金额
     * @param payType 支付方式
     * @return 操作结果
     */
    @ApiOperation("创建充值订单")
    @PostMapping("/recharge")
    public Map<String, Object> createRechargeOrder(
            @RequestParam Long customerId,
            @RequestParam BigDecimal amount,
            @RequestParam Integer payType) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 充值订单实际上是通过RechargeService处理，这里返回提示
            result.put("success", true);
            result.put("message", "请使用充值接口进行充值操作");
            result.put("redirect", "/pos/recharge");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 创建套餐订单
     *
     * @param dto 订单信息
     * @return 操作结果
     */
    @ApiOperation("创建套餐订单")
    @PostMapping("/package")
    public Map<String, Object> createPackageOrder(@RequestBody OrderCreateDTO dto) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> createResult = orderService.createPackageOrder(dto);
            result.putAll(createResult);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 支付订单
     *
     * @param id 订单ID
     * @param payType 支付方式
     * @param operatorId 操作员ID
     * @return 支付结果
     */
    @ApiOperation("支付订单")
    @PutMapping("/{id}/pay")
    public Map<String, Object> pay(
            @PathVariable Long id,
            @RequestParam Integer payType,
            @RequestParam(required = false) Long operatorId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> payResult = orderService.pay(id, payType, operatorId);
            result.putAll(payResult);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "支付失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 退款
     *
     * @param id 订单ID
     * @param refundAmount 退款金额
     * @param reason 退款原因
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    @ApiOperation("退款")
    @PutMapping("/{id}/refund")
    public Map<String, Object> refund(
            @PathVariable Long id,
            @RequestParam BigDecimal refundAmount,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) Long operatorId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> refundResult = orderService.refund(id, refundAmount, reason, operatorId);
            result.putAll(refundResult);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "退款失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 取消订单
     *
     * @param id 订单ID
     * @param reason 取消原因
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    @ApiOperation("取消订单")
    @PutMapping("/{id}/cancel")
    public Map<String, Object> cancel(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) Long operatorId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> cancelResult = orderService.cancel(id, reason, operatorId);
            result.putAll(cancelResult);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "取消失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 完成订单
     *
     * @param id 订单ID
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    @ApiOperation("完成订单")
    @PutMapping("/{id}/complete")
    public Map<String, Object> complete(
            @PathVariable Long id,
            @RequestParam(required = false) Long operatorId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> completeResult = orderService.complete(id, operatorId);
            result.putAll(completeResult);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 使用套餐明细
     *
     * @param itemId 订单明细ID
     * @param beauticianId 美容师ID
     * @return 操作结果
     */
    @ApiOperation("使用套餐明细")
    @PutMapping("/item/{itemId}/use")
    public Map<String, Object> usePackageItem(
            @PathVariable Long itemId,
            @RequestParam(required = false) Long beauticianId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> useResult = orderService.usePackageItem(itemId, beauticianId);
            result.putAll(useResult);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取日结报表
     *
     * @param date 日期
     * @return 日结数据
     */
    @ApiOperation("获取日结报表")
    @GetMapping("/daily-report")
    public Map<String, Object> getDailyReport(@RequestParam String date) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> report = orderService.getDailyReport(date);
            result.put("success", true);
            result.put("data", report);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取周报表
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 周报数据
     */
    @ApiOperation("获取周报表")
    @GetMapping("/weekly-report")
    public Map<String, Object> getWeeklyReport(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> report = orderService.getWeeklyReport(startDate, endDate);
            result.put("success", true);
            result.put("data", report);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取月报表
     *
     * @param yearMonth 年月（格式：yyyy-MM）
     * @return 月报数据
     */
    @ApiOperation("获取月报表")
    @GetMapping("/monthly-report")
    public Map<String, Object> getMonthlyReport(@RequestParam String yearMonth) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> report = orderService.getMonthlyReport(yearMonth);
            result.put("success", true);
            result.put("data", report);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取营业统计
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 营业统计
     */
    @ApiOperation("获取营业统计")
    @GetMapping("/statistics")
    public Map<String, Object> getStatistics(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> statistics = orderService.getStatistics(startDate, endDate);
            result.put("success", true);
            result.put("data", statistics);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 根据客户ID获取订单列表
     *
     * @param customerId 客户ID
     * @return 订单列表
     */
    @ApiOperation("根据客户ID获取订单列表")
    @GetMapping("/customer/{customerId}")
    public Map<String, Object> getByCustomerId(@PathVariable Long customerId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<OrderVO> list = orderService.getByCustomerId(customerId);
            result.put("success", true);
            result.put("data", list);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 根据订单号获取订单
     *
     * @param orderNo 订单号
     * @return 订单详情
     */
    @ApiOperation("根据订单号获取订单")
    @GetMapping("/no/{orderNo}")
    public Map<String, Object> getByOrderNo(@PathVariable String orderNo) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            OrderVO order = orderService.getByOrderNo(orderNo);
            if (order == null) {
                result.put("success", false);
                result.put("message", "订单不存在");
            } else {
                result.put("success", true);
                result.put("data", order);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }
}
