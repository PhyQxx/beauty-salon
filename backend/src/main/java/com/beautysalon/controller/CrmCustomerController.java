package com.beautysalon.controller;

import com.beautysalon.dto.CustomerCreateDTO;
import com.beautysalon.dto.CustomerQueryDTO;
import com.beautysalon.dto.CustomerUpdateDTO;
import com.beautysalon.service.AppointmentService;
import com.beautysalon.service.CustomerService;
import com.beautysalon.service.OrderService;
import com.beautysalon.vo.CustomerVO;
import com.beautysalon.vo.AppointmentVO;
import com.beautysalon.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CRM客户管理 Controller
 * 负责客户信息管理、会员等级、积分、余额等操作
 *
 * @author BeautySalon Team
 */
@Slf4j
@Api(tags = "CRM客户管理")
@RestController
@RequestMapping("/crm/customer")
public class CrmCustomerController {

    @Resource
    private CustomerService customerService;

    @Resource
    private AppointmentService appointmentService;

    @Resource
    private OrderService orderService;

    /**
     * 分页查询客户列表
     *
     * @param queryDTO 查询条件参数
     * @return 客户分页列表
     */
    @ApiOperation("分页查询客户列表")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(@Validated CustomerQueryDTO queryDTO) {
        Map<String, Object> result = customerService.queryCustomerPage(queryDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取客户简要列表（下拉框用）
     */
    @ApiOperation("获取客户简要列表")
    @GetMapping("/simple-list")
    public ResponseEntity<Map<String, Object>> getSimpleList() {
        List<Map<String, Object>> list = customerService.getSimpleList();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", list);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID获取客户详情
     *
     * @param id 客户ID
     * @return 客户详情
     */
    @ApiOperation("根据ID获取客户详情")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(
            @ApiParam(value = "客户ID", required = true) @PathVariable Long id) {
        CustomerVO customer = customerService.getCustomerById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("data", customer);
        result.put("code", 200);
        result.put("message", "查询成功");
        return ResponseEntity.ok(result);
    }

    /**
     * 新增客户（会员注册）
     *
     * @param dto 客户创建参数
     * @return 操作结果
     */
    @ApiOperation("新增客户")
    @PostMapping
    public ResponseEntity<Map<String, Object>> save(
            @Validated @RequestBody CustomerCreateDTO dto) {
        Long customerId = customerService.createCustomer(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "创建成功");
        result.put("data", customerId);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新客户信息
     *
     * @param id 客户ID
     * @param dto 客户更新参数
     * @return 操作结果
     */
    @ApiOperation("更新客户信息")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @ApiParam(value = "客户ID", required = true) @PathVariable Long id,
            @Validated @RequestBody CustomerUpdateDTO dto) {
        boolean success = customerService.updateCustomer(id, dto);
        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "更新成功" : "更新失败");
        return ResponseEntity.ok(result);
    }

    /**
     * 删除客户（逻辑删除）
     *
     * @param id 客户ID
     * @return 操作结果
     */
    @ApiOperation("删除客户")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(
            @ApiParam(value = "客户ID", required = true) @PathVariable Long id) {
        boolean success = customerService.deleteCustomer(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "删除成功" : "删除失败");
        return ResponseEntity.ok(result);
    }

    /**
     * 查询客户预约记录
     *
     * @param customerId 客户ID
     * @return 预约记录列表
     */
    @ApiOperation("查询客户预约记录")
    @GetMapping("/{id}/appointments")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @ApiParam(value = "客户ID", required = true) @PathVariable("id") Long customerId) {
        // 调用预约服务获取客户的预约记录
        List<AppointmentVO> appointments = appointmentService.getAppointmentsByCustomerId(customerId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", appointments);
        return ResponseEntity.ok(result);
    }

    /**
     * 查询客户订单记录
     *
     * @param customerId 客户ID
     * @return 订单记录列表
     */
    @ApiOperation("查询客户订单记录")
    @GetMapping("/{id}/orders")
    public ResponseEntity<Map<String, Object>> getOrders(
            @ApiParam(value = "客户ID", required = true) @PathVariable("id") Long customerId) {
        // 调用订单服务获取客户的订单记录
        List<OrderVO> orders = orderService.getByCustomerId(customerId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", orders);
        return ResponseEntity.ok(result);
    }

    /**
     * 账户充值
     *
     * @param id 客户ID
     * @param amount 充值金额
     * @param reason 充值原因
     * @return 充值后余额
     */
    @ApiOperation("账户充值")
    @PostMapping("/{id}/recharge")
    public ResponseEntity<Map<String, Object>> recharge(
            @ApiParam(value = "客户ID", required = true) @PathVariable Long id,
            @ApiParam(value = "充值金额", required = true) @RequestParam java.math.BigDecimal amount,
            @ApiParam(value = "充值原因") @RequestParam(required = false, defaultValue = "会员充值") String reason) {
        java.math.BigDecimal newBalance = customerService.recharge(id, amount, reason);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "充值成功");
        result.put("data", newBalance);
        return ResponseEntity.ok(result);
    }

    /**
     * 消费扣款
     *
     * @param id 客户ID
     * @param amount 消费金额
     * @param reason 消费原因
     * @return 扣款后余额
     */
    @ApiOperation("消费扣款")
    @PostMapping("/{id}/consume")
    public ResponseEntity<Map<String, Object>> consume(
            @ApiParam(value = "客户ID", required = true) @PathVariable Long id,
            @ApiParam(value = "消费金额", required = true) @RequestParam java.math.BigDecimal amount,
            @ApiParam(value = "消费原因") @RequestParam(required = false, defaultValue = "服务消费") String reason) {
        java.math.BigDecimal newBalance = customerService.consume(id, amount, reason);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "消费成功");
        result.put("data", newBalance);
        return ResponseEntity.ok(result);
    }

    /**
     * 增加积分
     *
     * @param id 客户ID
     * @param points 积分数量
     * @param reason 增加原因
     * @return 调整后积分
     */
    @ApiOperation("增加积分")
    @PostMapping("/{id}/points/add")
    public ResponseEntity<Map<String, Object>> addPoints(
            @ApiParam(value = "客户ID", required = true) @PathVariable Long id,
            @ApiParam(value = "积分数量", required = true) @RequestParam Integer points,
            @ApiParam(value = "原因") @RequestParam(required = false, defaultValue = "积分奖励") String reason) {
        Integer newPoints = customerService.addPoints(id, points, reason);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "积分增加成功");
        result.put("data", newPoints);
        return ResponseEntity.ok(result);
    }

    /**
     * 扣减积分
     *
     * @param id 客户ID
     * @param points 积分数量
     * @param reason 扣减原因
     * @return 调整后积分
     */
    @ApiOperation("扣减积分")
    @PostMapping("/{id}/points/deduct")
    public ResponseEntity<Map<String, Object>> deductPoints(
            @ApiParam(value = "客户ID", required = true) @PathVariable Long id,
            @ApiParam(value = "积分数量", required = true) @RequestParam Integer points,
            @ApiParam(value = "原因") @RequestParam(required = false, defaultValue = "积分兑换") String reason) {
        Integer newPoints = customerService.deductPoints(id, points, reason);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "积分扣减成功");
        result.put("data", newPoints);
        return ResponseEntity.ok(result);
    }

    /**
     * 调整客户积分（兼容旧接口）
     *
     * @param id 客户ID
     * @param points 积分变化量（正数增加，负数减少）
     * @param reason 调整原因
     * @return 操作结果
     */
    @ApiOperation("调整客户积分")
    @PutMapping("/{id}/points")
    public ResponseEntity<Map<String, Object>> adjustPoints(
            @ApiParam(value = "客户ID", required = true) @PathVariable Long id,
            @ApiParam(value = "积分变化量", required = true) @RequestParam Integer points,
            @ApiParam(value = "调整原因") @RequestParam(required = false) String reason) {
        if (points > 0) {
            customerService.addPoints(id, points, reason);
        } else if (points < 0) {
            customerService.deductPoints(id, -points, reason);
        }
        CustomerVO customer = customerService.getCustomerById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "积分调整成功");
        result.put("data", customer.getPoints());
        return ResponseEntity.ok(result);
    }

    /**
     * 调整客户余额
     *
     * @param id 客户ID
     * @param amount 余额变化量
     * @param reason 调整原因
     * @return 操作结果
     */
    @ApiOperation("调整客户余额")
    @PutMapping("/{id}/balance")
    public ResponseEntity<Map<String, Object>> adjustBalance(
            @ApiParam(value = "客户ID", required = true) @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Object amountObj = body.get("amount");
        if (amountObj == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 400);
            error.put("message", "缺少请求参数: amount");
            return ResponseEntity.badRequest().body(error);
        }
        java.math.BigDecimal amount = new java.math.BigDecimal(amountObj.toString());
        String reason = body.get("reason") != null ? body.get("reason").toString() : null;
        java.math.BigDecimal newBalance;
        if (amount.compareTo(java.math.BigDecimal.ZERO) > 0) {
            newBalance = customerService.recharge(id, amount, reason);
        } else {
            newBalance = customerService.consume(id, amount.negate(), reason);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "余额调整成功");
        result.put("data", newBalance);
        return ResponseEntity.ok(result);
    }

    /**
     * 升级客户会员等级
     *
     * @param id 客户ID
     * @param level 目标等级
     * @return 操作结果
     */
    @ApiOperation("升级客户会员等级")
    @PutMapping("/{id}/level")
    public ResponseEntity<Map<String, Object>> upgradeLevel(
            @ApiParam(value = "客户ID", required = true) @PathVariable Long id,
            @ApiParam(value = "目标等级", required = true) @RequestParam Integer level) {
        boolean success = customerService.upgradeMemberLevel(id, level);
        Map<String, Object> result = new HashMap<>();
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "等级升级成功" : "等级升级失败");
        return ResponseEntity.ok(result);
    }

    /**
     * 检查手机号是否已存在
     *
     * @param phone 手机号码
     * @return 是否存在
     */
    @ApiOperation("检查手机号是否已存在")
    @GetMapping("/check-phone")
    public ResponseEntity<Map<String, Object>> checkPhone(
            @ApiParam(value = "手机号码", required = true) @RequestParam String phone) {
        boolean exists = customerService.isPhoneExists(phone);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", exists);
        return ResponseEntity.ok(result);
    }
}
