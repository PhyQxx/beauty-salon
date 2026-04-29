package com.beautysalon.controller;

import com.beautysalon.dto.CampaignCreateDTO;
import com.beautysalon.dto.CampaignQueryDTO;
import com.beautysalon.service.CampaignService;
import com.beautysalon.vo.CampaignVO;
import com.beautysalon.vo.CouponVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 活动管理 Controller (POS模块)
 * 负责活动的创建、查询、优惠券发放核销、活动统计等操作
 *
 * @author BeautySalon Team
 */
@Api(tags = "活动管理")
@RestController
@RequestMapping("/pos/campaign")
public class PosCampaignController {

    @Autowired
    private CampaignService campaignService;

    // ==================== 活动基础操作 ====================

    /**
     * 创建活动
     *
     * @param dto 活动创建参数
     * @return 创建结果
     */
    @ApiOperation("创建活动")
    @PostMapping
    public Map<String, Object> createCampaign(@RequestBody CampaignCreateDTO dto) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 参数验证
            if (dto.getName() == null || dto.getName().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "活动名称不能为空");
                return result;
            }
            if (dto.getType() == null) {
                result.put("success", false);
                result.put("message", "活动类型不能为空");
                return result;
            }
            if (dto.getStartTime() == null || dto.getEndTime() == null) {
                result.put("success", false);
                result.put("message", "活动时间不能为空");
                return result;
            }
            if (dto.getRules() == null || dto.getRules().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "活动规则不能为空");
                return result;
            }
            
            result = campaignService.createCampaign(dto);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建活动失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 更新活动
     *
     * @param id 活动ID
     * @param dto 活动更新参数
     * @return 更新结果
     */
    @ApiOperation("更新活动")
    @PutMapping("/{id}")
    public Map<String, Object> updateCampaign(@PathVariable Long id, @RequestBody CampaignCreateDTO dto) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            result = campaignService.updateCampaign(id, dto);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新活动失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 取消活动
     *
     * @param id 活动ID
     * @param operatorId 操作员ID
     * @return 取消结果
     */
    @ApiOperation("取消活动")
    @PutMapping("/{id}/cancel")
    public Map<String, Object> cancelCampaign(@PathVariable Long id, @RequestParam Long operatorId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            result = campaignService.cancelCampaign(id, operatorId);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "取消活动失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 删除活动
     *
     * @param id 活动ID
     * @return 删除结果
     */
    @ApiOperation("删除活动")
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteCampaign(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            result = campaignService.deleteCampaign(id);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除活动失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 根据ID获取活动详情
     *
     * @param id 活动ID
     * @return 活动详情
     */
    @ApiOperation("根据ID获取活动详情")
    @GetMapping("/{id}")
    public Map<String, Object> getCampaignById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            CampaignVO campaign = campaignService.getCampaignById(id);
            if (campaign == null) {
                result.put("success", false);
                result.put("message", "活动不存在");
            } else {
                result.put("success", true);
                result.put("data", campaign);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询活动失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 分页查询活动列表
     *
     * @param name 活动名称（模糊查询）
     * @param type 活动类型
     * @param status 活动状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param storeId 门店ID
     * @param page 页码
     * @param limit 每页数量
     * @return 活动列表
     */
    @ApiOperation("分页查询活动列表")
    @GetMapping("/list")
    public Map<String, Object> listCampaigns(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long storeId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            CampaignQueryDTO queryDTO = new CampaignQueryDTO();
            queryDTO.setName(name);
            queryDTO.setType(type);
            queryDTO.setStatus(status);
            queryDTO.setStoreId(storeId);
            queryDTO.setPage(page);
            queryDTO.setLimit(limit);
            
            // 处理日期范围
            if (startDate != null && !startDate.isEmpty()) {
                queryDTO.setStartTimeStart(LocalDateTime.parse(startDate + " 00:00:00", 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
            if (endDate != null && !endDate.isEmpty()) {
                queryDTO.setEndTimeEnd(LocalDateTime.parse(endDate + " 23:59:59", 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
            
            List<CampaignVO> list = campaignService.listCampaigns(queryDTO);
            
            result.put("success", true);
            result.put("data", list);
            result.put("page", page);
            result.put("limit", limit);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询活动列表失败：" + e.getMessage());
        }
        
        return result;
    }

    // ==================== 优惠券操作 ====================

    /**
     * 发放优惠券
     *
     * @param campaignId 活动ID
     * @param memberId 会员ID
     * @param memberName 会员名称
     * @param memberPhone 会员手机号
     * @param operatorId 操作员ID
     * @return 发放结果
     */
    @ApiOperation("发放优惠券")
    @PostMapping("/coupon/issue")
    public Map<String, Object> issueCoupon(
            @RequestParam Long campaignId,
            @RequestParam Long memberId,
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) String memberPhone,
            @RequestParam(required = false) Long operatorId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            result = campaignService.issueCoupon(campaignId, memberId, memberName, memberPhone, operatorId);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "发放优惠券失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 批量发放优惠券
     *
     * @param campaignId 活动ID
     * @param memberId 会员ID
     * @param memberName 会员名称
     * @param memberPhone 会员手机号
     * @param quantity 发放数量
     * @param operatorId 操作员ID
     * @return 发放结果
     */
    @ApiOperation("批量发放优惠券")
    @PostMapping("/coupon/batch-issue")
    public Map<String, Object> batchIssueCoupons(
            @RequestParam Long campaignId,
            @RequestParam Long memberId,
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) String memberPhone,
            @RequestParam(defaultValue = "1") Integer quantity,
            @RequestParam(required = false) Long operatorId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            result = campaignService.batchIssueCoupons(campaignId, memberId, memberName, 
                    memberPhone, quantity, operatorId);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量发放优惠券失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 核销优惠券
     *
     * @param couponId 优惠券ID
     * @param orderId 订单ID
     * @param operatorId 操作员ID
     * @return 核销结果
     */
    @ApiOperation("核销优惠券")
    @PostMapping("/coupon/redeem")
    public Map<String, Object> redeemCoupon(
            @RequestParam Long couponId,
            @RequestParam Long orderId,
            @RequestParam(required = false) Long operatorId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            result = campaignService.redeemCoupon(couponId, orderId, operatorId);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "核销优惠券失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 根据优惠券码核销
     *
     * @param code 优惠券码
     * @param orderId 订单ID
     * @param operatorId 操作员ID
     * @return 核销结果
     */
    @ApiOperation("根据优惠券码核销")
    @PostMapping("/coupon/redeem-by-code")
    public Map<String, Object> redeemCouponByCode(
            @RequestParam String code,
            @RequestParam Long orderId,
            @RequestParam(required = false) Long operatorId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            result = campaignService.redeemCouponByCode(code, orderId, operatorId);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "核销优惠券失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 根据ID获取优惠券详情
     *
     * @param couponId 优惠券ID
     * @return 优惠券详情
     */
    @ApiOperation("根据ID获取优惠券详情")
    @GetMapping("/coupon/{couponId}")
    public Map<String, Object> getCouponById(@PathVariable Long couponId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            CouponVO coupon = campaignService.getCouponById(couponId);
            if (coupon == null) {
                result.put("success", false);
                result.put("message", "优惠券不存在");
            } else {
                result.put("success", true);
                result.put("data", coupon);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询优惠券失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 根据优惠券码获取优惠券
     *
     * @param code 优惠券码
     * @return 优惠券详情
     */
    @ApiOperation("根据优惠券码获取优惠券")
    @GetMapping("/coupon/code/{code}")
    public Map<String, Object> getCouponByCode(@PathVariable String code) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            CouponVO coupon = campaignService.getCouponByCode(code);
            if (coupon == null) {
                result.put("success", false);
                result.put("message", "优惠券不存在");
            } else {
                result.put("success", true);
                result.put("data", coupon);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询优惠券失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取会员的优惠券列表
     *
     * @param memberId 会员ID
     * @param status 优惠券状态（可选）
     * @return 优惠券列表
     */
    @ApiOperation("获取会员的优惠券列表")
    @GetMapping("/member/{memberId}/coupons")
    public Map<String, Object> getMemberCoupons(
            @PathVariable Long memberId,
            @RequestParam(required = false) Integer status) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<CouponVO> coupons = campaignService.getMemberCoupons(memberId, status);
            result.put("success", true);
            result.put("data", coupons);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询会员优惠券失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取会员可用的优惠券
     *
     * @param memberId 会员ID
     * @return 可用优惠券列表
     */
    @ApiOperation("获取会员可用的优惠券")
    @GetMapping("/member/{memberId}/available-coupons")
    public Map<String, Object> getAvailableCoupons(@PathVariable Long memberId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<CouponVO> coupons = campaignService.getAvailableCoupons(memberId);
            result.put("success", true);
            result.put("data", coupons);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询可用优惠券失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取活动的优惠券列表
     *
     * @param campaignId 活动ID
     * @return 优惠券列表
     */
    @ApiOperation("获取活动的优惠券列表")
    @GetMapping("/{campaignId}/coupons")
    public Map<String, Object> getCampaignCoupons(@PathVariable Long campaignId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<CouponVO> coupons = campaignService.getCampaignCoupons(campaignId);
            result.put("success", true);
            result.put("data", coupons);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询活动优惠券失败：" + e.getMessage());
        }
        
        return result;
    }

    // ==================== 活动查询 ====================

    /**
     * 查询时间范围内正在进行的活动
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 进行中的活动列表
     */
    @ApiOperation("查询时间范围内正在进行的活动")
    @GetMapping("/active")
    public Map<String, Object> getActiveCampaigns(
            @RequestParam String startTime,
            @RequestParam String endTime) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            LocalDateTime start = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime end = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            List<CampaignVO> campaigns = campaignService.getActiveCampaigns(start, end);
            result.put("success", true);
            result.put("data", campaigns);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询进行中活动失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 查询会员可参与的活动
     *
     * @param memberId 会员ID
     * @return 可参与的活动列表
     */
    @ApiOperation("查询会员可参与的活动")
    @GetMapping("/member/{memberId}/available")
    public Map<String, Object> getAvailableCampaignsForMember(@PathVariable Long memberId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<CampaignVO> campaigns = campaignService.getAvailableCampaignsForMember(memberId);
            result.put("success", true);
            result.put("data", campaigns);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询可参与活动失败：" + e.getMessage());
        }
        
        return result;
    }

    // ==================== 活动统计 ====================

    /**
     * 获取活动效果统计
     *
     * @param campaignId 活动ID
     * @return 统计数据
     */
    @ApiOperation("获取活动效果统计")
    @GetMapping("/{campaignId}/statistics")
    public Map<String, Object> getCampaignStatistics(@PathVariable Long campaignId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> statistics = campaignService.getCampaignStatistics(campaignId);
            result.put("success", true);
            result.put("data", statistics);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取活动统计失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取活动每日统计
     *
     * @param campaignId 活动ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每日统计数据
     */
    @ApiOperation("获取活动每日统计")
    @GetMapping("/{campaignId}/daily-statistics")
    public Map<String, Object> getCampaignDailyStatistics(
            @PathVariable Long campaignId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> statistics = campaignService.getCampaignDailyStatistics(
                    campaignId, startDate, endDate);
            result.put("success", true);
            result.put("data", statistics);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取每日统计失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取门店活动汇总统计
     *
     * @param storeId 门店ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 汇总统计数据
     */
    @ApiOperation("获取门店活动汇总统计")
    @GetMapping("/store/{storeId}/summary")
    public Map<String, Object> getStoreCampaignSummary(
            @PathVariable Long storeId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> summary = campaignService.getStoreCampaignSummary(storeId, startDate, endDate);
            result.put("success", true);
            result.put("data", summary);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取门店汇总统计失败：" + e.getMessage());
        }
        
        return result;
    }

    // ==================== 辅助接口 ====================

    /**
     * 验证优惠券是否可用
     *
     * @param couponId 优惠券ID
     * @param orderAmount 订单金额
     * @return 验证结果
     */
    @ApiOperation("验证优惠券是否可用")
    @GetMapping("/coupon/{couponId}/validate")
    public Map<String, Object> validateCoupon(
            @PathVariable Long couponId,
            @RequestParam Double orderAmount) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            result = campaignService.validateCoupon(couponId, orderAmount);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "验证优惠券失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 计算优惠金额
     *
     * @param campaignId 活动ID
     * @param orderAmount 订单金额
     * @return 优惠金额
     */
    @ApiOperation("计算优惠金额")
    @GetMapping("/{campaignId}/calculate-discount")
    public Map<String, Object> calculateDiscount(
            @PathVariable Long campaignId,
            @RequestParam Double orderAmount) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Double discount = campaignService.calculateDiscount(campaignId, orderAmount);
            result.put("success", true);
            result.put("discountAmount", discount);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "计算优惠金额失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 生成优惠券码
     *
     * @param campaignId 活动ID
     * @return 优惠券码
     */
    @ApiOperation("生成优惠券码")
    @GetMapping("/{campaignId}/generate-code")
    public Map<String, Object> generateCouponCode(@PathVariable Long campaignId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String code = campaignService.generateCouponCode(campaignId);
            result.put("success", true);
            result.put("code", code);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成优惠券码失败：" + e.getMessage());
        }
        
        return result;
    }

    // ==================== 定时任务接口 ====================

    /**
     * 更新过期活动状态
     * 定时任务调用接口
     *
     * @return 更新数量
     */
    @ApiOperation("更新过期活动状态")
    @PutMapping("/update-expired-status")
    public Map<String, Object> updateExpiredCampaignsStatus() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            int count = campaignService.updateExpiredCampaignsStatus();
            result.put("success", true);
            result.put("updatedCount", count);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新过期活动状态失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 更新过期优惠券状态
     * 定时任务调用接口
     *
     * @return 更新数量
     */
    @ApiOperation("更新过期优惠券状态")
    @PutMapping("/coupon/update-expired-status")
    public Map<String, Object> updateExpiredCouponsStatus() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            int count = campaignService.updateExpiredCouponsStatus();
            result.put("success", true);
            result.put("updatedCount", count);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新过期优惠券状态失败：" + e.getMessage());
        }
        
        return result;
    }
}
