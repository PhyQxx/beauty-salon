package com.beautysalon.controller;

import com.beautysalon.dto.RechargeDTO;
import com.beautysalon.service.RechargeService;
import com.beautysalon.vo.RechargeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 充值管理 Controller (POS模块)
 * 负责余额充值、满送规则执行、充值记录查询、日周月报表等操作
 *
 * @author BeautySalon Team
 */
@Api(tags = "充值管理")
@RestController
@RequestMapping("/pos/recharge")
public class PosRechargeController {

    @Autowired
    private RechargeService rechargeService;

    /**
     * 余额充值
     * 根据满送规则自动计算赠送金额
     *
     * @param dto 充值请求参数（客户ID、充值金额、支付方式等）
     * @return 充值结果（含充值ID、新余额、赠送金额等）
     */
    @ApiOperation("余额充值")
    @PostMapping
    public Map<String, Object> recharge(@RequestBody RechargeDTO dto) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> rechargeResult = rechargeService.recharge(dto);
            result.putAll(rechargeResult);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "充值失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 分页查询充值记录
     *
     * @param page 页码
     * @param limit 每页数量
     * @param customerId 客户ID（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 充值记录列表
     */
    @ApiOperation("分页查询充值记录")
    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<RechargeVO> list = rechargeService.listPage(page, limit, customerId, startDate, endDate);
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
     * 根据ID获取充值记录详情
     *
     * @param id 充值记录ID
     * @return 充值记录详情
     */
    @ApiOperation("根据ID获取充值记录详情")
    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            RechargeVO recharge = rechargeService.getById(id);
            if (recharge == null) {
                result.put("success", false);
                result.put("message", "充值记录不存在");
            } else {
                result.put("success", true);
                result.put("data", recharge);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 根据充值订单号获取充值记录
     *
     * @param rechargeNo 充值订单号
     * @return 充值记录详情
     */
    @ApiOperation("根据充值订单号获取充值记录")
    @GetMapping("/no/{rechargeNo}")
    public Map<String, Object> getByRechargeNo(@PathVariable String rechargeNo) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            RechargeVO recharge = rechargeService.getByRechargeNo(rechargeNo);
            if (recharge == null) {
                result.put("success", false);
                result.put("message", "充值记录不存在");
            } else {
                result.put("success", true);
                result.put("data", recharge);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取客户当前余额
     *
     * @param customerId 客户ID
     * @return 当前余额
     */
    @ApiOperation("获取客户当前余额")
    @GetMapping("/balance/{customerId}")
    public Map<String, Object> getBalance(@PathVariable Long customerId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            BigDecimal balance = rechargeService.getCustomerBalance(customerId);
            result.put("success", true);
            result.put("balance", balance);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取客户充值记录列表
     *
     * @param customerId 客户ID
     * @return 充值记录列表
     */
    @ApiOperation("获取客户充值记录列表")
    @GetMapping("/customer/{customerId}")
    public Map<String, Object> getByCustomerId(@PathVariable Long customerId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<RechargeVO> list = rechargeService.getByCustomerId(customerId);
            result.put("success", true);
            result.put("data", list);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 充值退款（退还余额）
     *
     * @param orderId 原订单ID
     * @param customerId 客户ID
     * @param refundAmount 退款金额
     * @param reason 退款原因
     * @param operatorId 操作员ID
     * @return 退款结果
     */
    @ApiOperation("充值退款")
    @PostMapping("/refund")
    public Map<String, Object> refund(
            @RequestParam Long orderId,
            @RequestParam Long customerId,
            @RequestParam BigDecimal refundAmount,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) Long operatorId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> refundResult = rechargeService.refund(
                    orderId, customerId, refundAmount, reason, operatorId);
            result.putAll(refundResult);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "退款失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取满送规则
     *
     * @param amount 充值金额（可选，传入则返回该金额对应的赠送金额）
     * @return 满送规则列表或指定金额的赠送金额
     */
    @ApiOperation("获取满送规则")
    @GetMapping("/rules")
    public Map<String, Object> getGiftRules(
            @RequestParam(required = false) BigDecimal amount) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (amount != null) {
                // 返回指定金额的赠送金额
                BigDecimal giftAmount = rechargeService.getGiftAmount(amount);
                result.put("amount", amount);
                result.put("giftAmount", giftAmount);
                result.put("total", amount.add(giftAmount));
            } else {
                // 返回所有规则
                Map<String, Object> rules = new HashMap<>();
                rules.put("200", "20");
                rules.put("500", "60");
                rules.put("1000", "150");
                rules.put("2000", "350");
                rules.put("5000", "1000");
                result.put("rules", rules);
            }
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取日充值报表
     *
     * @param date 日期（格式：yyyy-MM-dd）
     * @return 日充值统计
     */
    @ApiOperation("获取日充值报表")
    @GetMapping("/daily-report")
    public Map<String, Object> getDailyReport(@RequestParam String date) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> report = rechargeService.getDailyReport(date);
            result.put("success", true);
            result.put("data", report);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取周充值报表
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 周充值统计
     */
    @ApiOperation("获取周充值报表")
    @GetMapping("/weekly-report")
    public Map<String, Object> getWeeklyReport(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> report = rechargeService.getWeeklyReport(startDate, endDate);
            result.put("success", true);
            result.put("data", report);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取月充值报表
     *
     * @param yearMonth 年月（格式：yyyy-MM）
     * @return 月充值统计
     */
    @ApiOperation("获取月充值报表")
    @GetMapping("/monthly-report")
    public Map<String, Object> getMonthlyReport(@RequestParam String yearMonth) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> report = rechargeService.getMonthlyReport(yearMonth);
            result.put("success", true);
            result.put("data", report);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        
        return result;
    }
}
