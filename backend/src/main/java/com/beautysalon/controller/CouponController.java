package com.beautysalon.controller;

import com.beautysalon.entity.PosCoupon;
import com.beautysalon.service.CouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 优惠券管理 Controller
 *
 * @author BeautySalon Team
 */
@Slf4j
@Api(tags = "优惠券管理")
@RestController
@RequestMapping("/coupon")
public class CouponController {

    @Resource
    private CouponService couponService;

    /**
     * 分页查询优惠券列表
     */
    @ApiOperation("分页查询优惠券列表")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(
            @ApiParam(value = "页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页数量") @RequestParam(defaultValue = "10") Integer limit,
            @ApiParam(value = "关键词搜索") @RequestParam(required = false) String keyword,
            @ApiParam(value = "状态筛选") @RequestParam(required = false) Integer status) {
        Map<String, Object> result = couponService.queryCouponPage(page, limit, keyword, status);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取优惠券详情
     */
    @ApiOperation("获取优惠券详情")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        try {
            PosCoupon coupon = couponService.getCouponById(id);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", coupon);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 404);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 创建优惠券
     */
    @ApiOperation("创建优惠券")
    @PostMapping
    public ResponseEntity<Map<String, Object>> save(@RequestBody PosCoupon coupon) {
        try {
            Long id = couponService.createCoupon(coupon);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "创建成功");
            result.put("data", id);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 更新优惠券
     */
    @ApiOperation("更新优惠券")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody PosCoupon coupon) {
        try {
            boolean success = couponService.updateCoupon(id, coupon);
            Map<String, Object> result = new HashMap<>();
            result.put("code", success ? 200 : 500);
            result.put("message", success ? "更新成功" : "更新失败");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 删除优惠券
     */
    @ApiOperation("删除优惠券")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        try {
            boolean success = couponService.deleteCoupon(id);
            Map<String, Object> result = new HashMap<>();
            result.put("code", success ? 200 : 500);
            result.put("message", success ? "删除成功" : "删除失败");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 发放优惠券给客户
     */
    @ApiOperation("发放优惠券给客户")
    @PostMapping("/distribute")
    public ResponseEntity<Map<String, Object>> distribute(
            @ApiParam(value = "优惠券ID") @RequestParam Long couponId,
            @ApiParam(value = "客户ID") @RequestParam Long customerId) {
        try {
            boolean success = couponService.distributeCoupon(couponId, customerId);
            Map<String, Object> result = new HashMap<>();
            result.put("code", success ? 200 : 500);
            result.put("message", success ? "发放成功" : "发放失败");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 客户领取优惠券
     */
    @ApiOperation("客户领取优惠券")
    @PostMapping("/receive")
    public ResponseEntity<Map<String, Object>> receive(
            @ApiParam(value = "优惠券ID") @RequestParam Long couponId,
            @ApiParam(value = "客户ID") @RequestParam Long customerId) {
        try {
            boolean success = couponService.receiveCoupon(couponId, customerId);
            Map<String, Object> result = new HashMap<>();
            result.put("code", success ? 200 : 500);
            result.put("message", success ? "领取成功" : "领取失败");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 使用优惠券
     */
    @ApiOperation("使用优惠券")
    @PostMapping("/use")
    public ResponseEntity<Map<String, Object>> use(
            @ApiParam(value = "客户优惠券ID") @RequestParam Long customerCouponId,
            @ApiParam(value = "订单ID") @RequestParam Long orderId) {
        try {
            boolean success = couponService.useCoupon(customerCouponId, orderId);
            Map<String, Object> result = new HashMap<>();
            result.put("code", success ? 200 : 500);
            result.put("message", success ? "使用成功" : "使用失败");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 获取客户优惠券列表
     */
    @ApiOperation("获取客户优惠券列表")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Map<String, Object>> getCustomerCoupons(
            @PathVariable Long customerId,
            @ApiParam(value = "状态") @RequestParam(required = false) Integer status) {
        Map<String, Object> result = couponService.getCustomerCoupons(customerId, status);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新过期优惠券状态
     */
    @ApiOperation("更新过期优惠券状态")
    @PutMapping("/expire-check")
    public ResponseEntity<Map<String, Object>> updateExpired() {
        int count = couponService.updateExpiredCouponsStatus();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "更新了 " + count + " 张过期优惠券");
        return ResponseEntity.ok(result);
    }
}
