package com.beautysalon.service.impl;

import com.beautysalon.dto.CampaignCreateDTO;
import com.beautysalon.dto.CampaignQueryDTO;
import com.beautysalon.entity.PosCampaign;
import com.beautysalon.entity.PosCoupon;
import com.beautysalon.mapper.PosCampaignMapper;
import com.beautysalon.mapper.PosCouponMapper;
import com.beautysalon.service.CampaignService;
import com.beautysalon.vo.CampaignVO;
import com.beautysalon.vo.CouponVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 活动服务实现类
 * 实现活动管理的核心业务逻辑，包括活动创建、优惠券发放核销、活动统计等
 *
 * @author BeautySalon Team
 */
@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private PosCampaignMapper campaignMapper;

    @Autowired
    private PosCouponMapper couponMapper;

    @Autowired
    private ObjectMapper objectMapper;

    // ==================== 活动基础操作 ====================

    @Override
    @Transactional
    public Map<String, Object> createCampaign(CampaignCreateDTO dto) {
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
            if (dto.getEndTime().isBefore(dto.getStartTime())) {
                result.put("success", false);
                result.put("message", "结束时间不能早于开始时间");
                return result;
            }
            if (dto.getRules() == null || dto.getRules().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "活动规则不能为空");
                return result;
            }
            
            // 验证规则JSON格式
            try {
                objectMapper.readTree(dto.getRules());
            } catch (JsonProcessingException e) {
                result.put("success", false);
                result.put("message", "活动规则JSON格式不正确");
                return result;
            }
            
            // 创建活动实体
            PosCampaign campaign = new PosCampaign();
            campaign.setName(dto.getName().trim());
            campaign.setType(dto.getType());
            campaign.setStartTime(dto.getStartTime());
            campaign.setEndTime(dto.getEndTime());
            campaign.setRules(dto.getRules());
            campaign.setDescription(dto.getDescription());
            campaign.setStoreId(dto.getStoreId());
            campaign.setOperatorId(dto.getOperatorId());
            campaign.setStatus(calculateInitialStatus(dto.getStartTime(), dto.getEndTime()));
            campaign.setCreatedAt(LocalDateTime.now());
            campaign.setUpdatedAt(LocalDateTime.now());
            campaign.setDeleted(0);
            
            // 保存活动
            campaignMapper.insert(campaign);
            
            result.put("success", true);
            result.put("message", "活动创建成功");
            result.put("data", CampaignVO.fromEntity(campaign));
            result.put("campaignId", campaign.getId());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建活动失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
        
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> updateCampaign(Long id, CampaignCreateDTO dto) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            PosCampaign campaign = campaignMapper.selectById(id);
            if (campaign == null) {
                result.put("success", false);
                result.put("message", "活动不存在");
                return result;
            }
            
            // 已开始的活动不能修改基本信息
            if (campaign.getStatus() != null && campaign.getStatus() == 0) {
                // 未开始的可以修改
            } else if (campaign.getStatus() != null && campaign.getStatus() == 1) {
                // 进行中的只能修改结束时间
            }
            
            // 更新字段
            if (dto.getName() != null) {
                campaign.setName(dto.getName().trim());
            }
            if (dto.getType() != null) {
                campaign.setType(dto.getType());
            }
            if (dto.getStartTime() != null) {
                campaign.setStartTime(dto.getStartTime());
            }
            if (dto.getEndTime() != null) {
                if (dto.getEndTime().isBefore(campaign.getStartTime())) {
                    result.put("success", false);
                    result.put("message", "结束时间不能早于开始时间");
                    return result;
                }
                campaign.setEndTime(dto.getEndTime());
            }
            if (dto.getRules() != null) {
                try {
                    objectMapper.readTree(dto.getRules());
                    campaign.setRules(dto.getRules());
                } catch (JsonProcessingException e) {
                    result.put("success", false);
                    result.put("message", "活动规则JSON格式不正确");
                    return result;
                }
            }
            if (dto.getDescription() != null) {
                campaign.setDescription(dto.getDescription());
            }
            
            campaign.setUpdatedAt(LocalDateTime.now());
            // 重新计算状态
            campaign.setStatus(calculateInitialStatus(campaign.getStartTime(), campaign.getEndTime()));
            
            campaignMapper.update(campaign);
            
            result.put("success", true);
            result.put("message", "活动更新成功");
            result.put("data", CampaignVO.fromEntity(campaign));
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新活动失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
        
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> cancelCampaign(Long id, Long operatorId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            PosCampaign campaign = campaignMapper.selectById(id);
            if (campaign == null) {
                result.put("success", false);
                result.put("message", "活动不存在");
                return result;
            }
            
            if (campaign.getStatus() != null && campaign.getStatus() == 3) {
                result.put("success", false);
                result.put("message", "活动已取消");
                return result;
            }
            
            campaign.setStatus(3); // 已取消
            campaign.setUpdatedAt(LocalDateTime.now());
            campaignMapper.update(campaign);
            
            result.put("success", true);
            result.put("message", "活动取消成功");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "取消活动失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
        
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> deleteCampaign(Long id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            PosCampaign campaign = campaignMapper.selectById(id);
            if (campaign == null) {
                result.put("success", false);
                result.put("message", "活动不存在");
                return result;
            }
            
            campaignMapper.deleteById(id);
            
            result.put("success", true);
            result.put("message", "活动删除成功");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除活动失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
        
        return result;
    }

    @Override
    public CampaignVO getCampaignById(Long id) {
        PosCampaign campaign = campaignMapper.selectById(id);
        if (campaign == null) {
            return null;
        }
        CampaignVO vo = CampaignVO.fromEntity(campaign);
        // 填充统计数据
        fillCampaignStatistics(vo, id);
        return vo;
    }

    @Override
    public List<CampaignVO> listCampaigns(CampaignQueryDTO queryDTO) {
        Map<String, Object> params = new HashMap<>();
        
        if (queryDTO.getName() != null && !queryDTO.getName().trim().isEmpty()) {
            params.put("name", "%" + queryDTO.getName().trim() + "%");
        }
        if (queryDTO.getType() != null) {
            params.put("type", queryDTO.getType());
        }
        if (queryDTO.getStatus() != null) {
            params.put("status", queryDTO.getStatus());
        }
        if (queryDTO.getStartTimeStart() != null) {
            params.put("startTimeStart", queryDTO.getStartTimeStart());
        }
        if (queryDTO.getStartTimeEnd() != null) {
            params.put("startTimeEnd", queryDTO.getStartTimeEnd());
        }
        if (queryDTO.getEndTimeStart() != null) {
            params.put("endTimeStart", queryDTO.getEndTimeStart());
        }
        if (queryDTO.getEndTimeEnd() != null) {
            params.put("endTimeEnd", queryDTO.getEndTimeEnd());
        }
        if (queryDTO.getStoreId() != null) {
            params.put("storeId", queryDTO.getStoreId());
        }
        
        // 分页参数
        int page = queryDTO.getPage() != null ? queryDTO.getPage() : 1;
        int limit = queryDTO.getLimit() != null ? queryDTO.getLimit() : 10;
        params.put("offset", (page - 1) * limit);
        params.put("limit", limit);
        
        List<PosCampaign> campaigns = campaignMapper.selectPage(params);
        List<CampaignVO> voList = new ArrayList<>();
        
        for (PosCampaign campaign : campaigns) {
            CampaignVO vo = CampaignVO.fromEntity(campaign);
            fillCampaignStatistics(vo, campaign.getId());
            voList.add(vo);
        }
        
        return voList;
    }

    // ==================== 优惠券发放与核销 ====================

    @Override
    @Transactional
    public Map<String, Object> issueCoupon(Long campaignId, Long memberId, String memberName,
                                          String memberPhone, Long operatorId) {
        return batchIssueCoupons(campaignId, memberId, memberName, memberPhone, 1, operatorId);
    }

    @Override
    @Transactional
    public Map<String, Object> batchIssueCoupons(Long campaignId, Long memberId, String memberName,
                                                 String memberPhone, Integer quantity, Long operatorId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 参数验证
            if (campaignId == null) {
                result.put("success", false);
                result.put("message", "活动ID不能为空");
                return result;
            }
            if (memberId == null) {
                result.put("success", false);
                result.put("message", "会员ID不能为空");
                return result;
            }
            if (quantity == null || quantity <= 0) {
                quantity = 1;
            }
            
            // 查询活动
            PosCampaign campaign = campaignMapper.selectById(campaignId);
            if (campaign == null) {
                result.put("success", false);
                result.put("message", "活动不存在");
                return result;
            }
            
            // 检查活动状态
            LocalDateTime now = LocalDateTime.now();
            if (campaign.getStatus() == null || campaign.getStatus() != 1) {
                result.put("success", false);
                result.put("message", "活动未开始或已结束");
                return result;
            }
            if (now.isAfter(campaign.getEndTime())) {
                result.put("success", false);
                result.put("message", "活动已结束");
                return result;
            }
            
            // 解析活动规则获取优惠券信息
            JsonNode rules = objectMapper.readTree(campaign.getRules());
            Double value = getValueFromRules(campaign.getType(), rules);
            Double threshold = getThresholdFromRules(campaign.getType(), rules);
            
            // 生成优惠券列表
            List<PosCoupon> coupons = new ArrayList<>();
            LocalDateTime validEndTime = campaign.getEndTime();
            
            for (int i = 0; i < quantity; i++) {
                PosCoupon coupon = new PosCoupon();
                coupon.setCampaignId(campaignId);
                coupon.setCode(generateCouponCode(campaignId));
                coupon.setStatus(1);
                coupon.setDiscountValue(BigDecimal.valueOf(value));
                coupon.setMinAmount(BigDecimal.valueOf(threshold));
                coupon.setStartDate(campaign.getStartTime().toLocalDate());
                coupon.setEndDate(campaign.getEndTime().toLocalDate());
                coupon.setReceiveCount(0);
                coupon.setUseCount(0);
                coupon.setCreateTime(now);
                coupon.setUpdateTime(now);
                coupon.setDeleted(0);
                coupons.add(coupon);
            }
            
            // 批量插入
            couponMapper.insertBatch(coupons);
            
            result.put("success", true);
            result.put("message", "优惠券发放成功");
            result.put("issuedCount", quantity);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "发放优惠券失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
        
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> redeemCoupon(Long couponId, Long orderId, Long operatorId) {
        Map<String, Object> result = new HashMap<>();

        try {
            PosCoupon coupon = couponMapper.selectById(couponId);
            if (coupon == null) {
                result.put("success", false);
                result.put("message", "优惠券不存在");
                return result;
            }
            if (coupon.getStatus() != null && coupon.getStatus() == 2) {
                result.put("success", false);
                result.put("message", "优惠券已使用");
                return result;
            }
            if (coupon.getStatus() != null && coupon.getStatus() == 3) {
                result.put("success", false);
                result.put("message", "优惠券已过期");
                return result;
            }

            // 检查有效期
            if (coupon.getEndDate() != null && java.time.LocalDate.now().isAfter(coupon.getEndDate())) {
                coupon.setStatus(3);
                coupon.setUpdateTime(java.time.LocalDateTime.now());
                couponMapper.update(coupon);
                result.put("success", false);
                result.put("message", "优惠券已过期");
                return result;
            }

            // 核销优惠券
            coupon.setStatus(2);
            coupon.setUpdateTime(java.time.LocalDateTime.now());
            couponMapper.update(coupon);

            result.put("success", true);
            result.put("message", "优惠券核销成功");
            result.put("discountAmount", coupon.getDiscountValue());

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "核销优惠券失败：" + e.getMessage());
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> redeemCouponByCode(String code, Long orderId, Long operatorId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            PosCoupon coupon = couponMapper.selectByCode(code);
            if (coupon == null) {
                result.put("success", false);
                result.put("message", "优惠券不存在");
                return result;
            }
            
            return redeemCoupon(coupon.getId(), orderId, operatorId);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "核销优惠券失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public CouponVO getCouponById(Long couponId) {
        PosCoupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            return null;
        }
        CouponVO vo = CouponVO.fromEntity(coupon);
        // 填充活动名称
        return vo;
    }

    @Override
    public CouponVO getCouponByCode(String code) {
        PosCoupon coupon = couponMapper.selectByCode(code);
        if (coupon == null) {
            return null;
        }
        return getCouponById(coupon.getId());
    }

    @Override
    public List<CouponVO> getMemberCoupons(Long memberId, Integer status) {
        List<PosCoupon> coupons;
        if (status != null) {
            coupons = couponMapper.selectByMemberIdAndStatus(memberId, status);
        } else {
            coupons = couponMapper.selectByMemberId(memberId);
        }
        
        List<CouponVO> voList = new ArrayList<>();
        for (PosCoupon coupon : coupons) {
            CouponVO vo = getCouponById(coupon.getId());
            if (vo != null) {
                voList.add(vo);
            }
        }
        return voList;
    }

    @Override
    public List<CouponVO> getAvailableCoupons(Long memberId) {
        LocalDateTime now = LocalDateTime.now();
        List<PosCoupon> coupons = couponMapper.selectAvailableByMemberId(memberId, now);
        
        List<CouponVO> voList = new ArrayList<>();
        for (PosCoupon coupon : coupons) {
            CouponVO vo = getCouponById(coupon.getId());
            if (vo != null) {
                voList.add(vo);
            }
        }
        return voList;
    }

    // ==================== 活动查询 ====================

    @Override
    public List<CampaignVO> getActiveCampaigns(LocalDateTime startTime, LocalDateTime endTime) {
        List<PosCampaign> campaigns = campaignMapper.selectByDateRange(startTime, endTime);
        List<CampaignVO> voList = new ArrayList<>();
        
        LocalDateTime now = LocalDateTime.now();
        for (PosCampaign campaign : campaigns) {
            // 只返回进行中的活动
            if (now.isAfter(campaign.getStartTime()) && now.isBefore(campaign.getEndTime())) {
                CampaignVO vo = CampaignVO.fromEntity(campaign);
                vo.setStatus(1); // 进行中
                fillCampaignStatistics(vo, campaign.getId());
                voList.add(vo);
            }
        }
        
        return voList;
    }

    @Override
    public List<CampaignVO> getAvailableCampaignsForMember(Long memberId) {
        LocalDateTime now = LocalDateTime.now();
        List<PosCampaign> campaigns = campaignMapper.selectOngoing(now);
        
        List<CampaignVO> voList = new ArrayList<>();
        for (PosCampaign campaign : campaigns) {
            CampaignVO vo = CampaignVO.fromEntity(campaign);
            vo.setStatus(1);
            fillCampaignStatistics(vo, campaign.getId());
            voList.add(vo);
        }
        
        return voList;
    }

    @Override
    public List<CouponVO> getCampaignCoupons(Long campaignId) {
        List<PosCoupon> coupons = couponMapper.selectByCampaignId(campaignId);
        List<CouponVO> voList = new ArrayList<>();
        
        for (PosCoupon coupon : coupons) {
            CouponVO vo = getCouponById(coupon.getId());
            if (vo != null) {
                voList.add(vo);
            }
        }
        
        return voList;
    }

    // ==================== 活动效果统计 ====================

    @Override
    public Map<String, Object> getCampaignStatistics(Long campaignId) {
        Map<String, Object> statistics = new HashMap<>();
        
        PosCampaign campaign = campaignMapper.selectById(campaignId);
        if (campaign == null) {
            return statistics;
        }
        
        statistics.put("campaignId", campaignId);
        statistics.put("campaignName", campaign.getName());
        statistics.put("campaignType", campaign.getType());
        statistics.put("campaignStatus", campaign.getStatus());
        statistics.put("startTime", campaign.getStartTime());
        statistics.put("endTime", campaign.getEndTime());
        
        // 发放数量
        int issuedCount = couponMapper.countByCampaignId(campaignId);
        statistics.put("issuedCount", issuedCount);
        
        // 已使用数量
        int usedCount = couponMapper.countUsedByCampaignId(campaignId);
        statistics.put("usedCount", usedCount);
        
        // 已过期数量
        int expiredCount = couponMapper.countExpiredByCampaignId(campaignId);
        statistics.put("expiredCount", expiredCount);
        
        // 待使用数量
        int pendingCount = issuedCount - usedCount - expiredCount;
        statistics.put("pendingCount", pendingCount);
        
        // 参与人数
        int participantCount = couponMapper.countDistinctMembersByCampaignId(campaignId);
        statistics.put("participantCount", participantCount);
        
        // 使用率
        double usageRate = issuedCount > 0 ? (double) usedCount / issuedCount * 100 : 0;
        statistics.put("usageRate", String.format("%.2f", usageRate) + "%");
        
        return statistics;
    }

    @Override
    public List<Map<String, Object>> getCampaignDailyStatistics(Long campaignId, String startDate, String endDate) {
        return couponMapper.selectDailyStatisticsByCampaignId(campaignId, startDate, endDate);
    }

    @Override
    public Map<String, Object> getStoreCampaignSummary(Long storeId, String startDate, String endDate) {
        Map<String, Object> summary = new HashMap<>();
        
        LocalDateTime start = LocalDateTime.parse(startDate + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse(endDate + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        // 查询时间范围内的活动
        List<PosCampaign> campaigns = campaignMapper.selectByDateRange(start, end);
        
        int totalCampaigns = 0;
        int totalIssued = 0;
        int totalUsed = 0;
        int totalParticipants = 0;
        
        for (PosCampaign campaign : campaigns) {
            if (storeId == null || campaign.getStoreId() == null || campaign.getStoreId().equals(storeId)) {
                totalCampaigns++;
                totalIssued += couponMapper.countByCampaignId(campaign.getId());
                totalUsed += couponMapper.countUsedByCampaignId(campaign.getId());
                totalParticipants += couponMapper.countDistinctMembersByCampaignId(campaign.getId());
            }
        }
        
        summary.put("totalCampaigns", totalCampaigns);
        summary.put("totalIssued", totalIssued);
        summary.put("totalUsed", totalUsed);
        summary.put("totalParticipants", totalParticipants);
        summary.put("startDate", startDate);
        summary.put("endDate", endDate);
        
        return summary;
    }

    // ==================== 定时任务支持 ====================

    @Override
    public int updateExpiredCampaignsStatus() {
        LocalDateTime now = LocalDateTime.now();
        // 将状态为1（进行中）且结束时间已过的活动更新为2（已结束）
        return campaignMapper.batchUpdateStatusByTime(1, 2, now);
    }

    @Override
    public int updateExpiredCouponsStatus() {
        LocalDateTime now = LocalDateTime.now();
        return couponMapper.batchUpdateExpiredStatus(now);
    }

    // ==================== 辅助方法 ====================

    @Override
    public String generateCouponCode(Long campaignId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.format("%04d", new Random().nextInt(10000));
        return "C" + campaignId + timestamp.substring(timestamp.length() - 8) + random;
    }

    @Override
    public Map<String, Object> validateCoupon(Long couponId, Double orderAmount) {
        Map<String, Object> result = new HashMap<>();
        
        PosCoupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            result.put("valid", false);
            result.put("message", "优惠券不存在");
            return result;
        }
        
        // 检查状态
        if (coupon.getStatus() != null && coupon.getStatus() == 2) {
            result.put("valid", false);
            result.put("message", "优惠券已使用");
            return result;
        }
        if (coupon.getStatus() != null && coupon.getStatus() == 3) {
            result.put("valid", false);
            result.put("message", "优惠券已过期");
            return result;
        }
        
        // 检查有效期
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartDate() != null && now.toLocalDate().isBefore(coupon.getStartDate())) {
            result.put("valid", false);
            result.put("message", "优惠券未到使用时间");
            return result;
        }
        if (coupon.getEndDate() != null && now.toLocalDate().isAfter(coupon.getEndDate())) {
            result.put("valid", false);
            result.put("message", "优惠券已过期");
            return result;
        }

        // 检查门槛金额
        if (coupon.getMinAmount() != null && orderAmount < coupon.getMinAmount().doubleValue()) {
            result.put("valid", false);
            result.put("message", "订单金额未达到优惠券使用门槛（满" + coupon.getMinAmount() + "元）");
            return result;
        }

        result.put("valid", true);
        result.put("message", "优惠券可用");
        result.put("discountAmount", coupon.getDiscountValue());
        result.put("coupon", CouponVO.fromEntity(coupon));
        
        return result;
    }

    @Override
    public Double calculateDiscount(Long campaignId, Double orderAmount) {
        PosCampaign campaign = campaignMapper.selectById(campaignId);
        if (campaign == null) {
            return 0.0;
        }
        
        try {
            JsonNode rules = objectMapper.readTree(campaign.getRules());
            
            switch (campaign.getType()) {
                case 1: // 打折
                    return calculateDiscountType(orderAmount, rules);
                case 2: // 满减
                    return calculateFullReduceType(orderAmount, rules);
                case 4: // 限时特价
                    return calculateLimitedSpecialType(orderAmount, rules);
                default:
                    return 0.0;
            }
        } catch (Exception e) {
            return 0.0;
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 计算初始状态
     */
    private Integer calculateInitialStatus(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime)) {
            return 0; // 未开始
        } else if (now.isAfter(endTime)) {
            return 2; // 已结束
        } else {
            return 1; // 进行中
        }
    }

    /**
     * 填充活动统计数据
     */
    private void fillCampaignStatistics(CampaignVO vo, Long campaignId) {
        if (campaignId == null) return;
        
        int issuedCount = couponMapper.countByCampaignId(campaignId);
        vo.setIssuedCount(issuedCount);
        
        int usedCount = couponMapper.countUsedByCampaignId(campaignId);
        vo.setUsedCount(usedCount);
        
        int expiredCount = couponMapper.countExpiredByCampaignId(campaignId);
        vo.setExpiredCount(expiredCount);
        
        int participantCount = couponMapper.countDistinctMembersByCampaignId(campaignId);
        vo.setParticipantCount(participantCount);
    }

    /**
     * 从规则JSON中获取优惠券面值
     */
    private Double getValueFromRules(Integer type, JsonNode rules) {
        try {
            switch (type) {
                case 1: // 打折
                    if (rules.has("discountType") && rules.get("discountType").asInt() == 1) {
                        // 折扣率，如0.85表示85折
                        return rules.get("discountValue").asDouble();
                    } else {
                        // 固定金额减免
                        return rules.has("reduceAmount") ? rules.get("reduceAmount").asDouble() : 0.0;
                    }
                case 2: // 满减
                    return rules.has("reduceAmount") ? rules.get("reduceAmount").asDouble() : 0.0;
                case 4: // 限时特价
                    if (rules.has("originalPrice") && rules.has("specialPrice")) {
                        return rules.get("originalPrice").asDouble() - rules.get("specialPrice").asDouble();
                    }
                    return rules.has("specialPrice") ? rules.get("specialPrice").asDouble() : 0.0;
                default:
                    return 0.0;
            }
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * 从规则JSON中获取使用门槛
     */
    private Double getThresholdFromRules(Integer type, JsonNode rules) {
        try {
            switch (type) {
                case 2: // 满减
                    return rules.has("threshold") ? rules.get("threshold").asDouble() : 0.0;
                case 4: // 限时特价
                    return rules.has("specialPrice") ? rules.get("specialPrice").asDouble() : 0.0;
                default:
                    return 0.0;
            }
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * 计算打折优惠金额
     */
    private Double calculateDiscountType(Double orderAmount, JsonNode rules) {
        try {
            if (rules.has("discountType") && rules.get("discountType").asInt() == 1) {
                // 折扣率
                double rate = rules.get("discountValue").asDouble();
                return orderAmount * (1 - rate);
            } else {
                // 固定金额减免
                return rules.has("reduceAmount") ? rules.get("reduceAmount").asDouble() : 0.0;
            }
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * 计算满减优惠金额
     */
    private Double calculateFullReduceType(Double orderAmount, JsonNode rules) {
        try {
            double threshold = rules.has("threshold") ? rules.get("threshold").asDouble() : 0.0;
            double reduceAmount = rules.has("reduceAmount") ? rules.get("reduceAmount").asDouble() : 0.0;
            
            if (orderAmount >= threshold) {
                return reduceAmount;
            }
            return 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * 计算限时特价优惠金额
     */
    private Double calculateLimitedSpecialType(Double orderAmount, JsonNode rules) {
        try {
            if (rules.has("originalPrice") && rules.has("specialPrice")) {
                double originalPrice = rules.get("originalPrice").asDouble();
                double specialPrice = rules.get("specialPrice").asDouble();
                return originalPrice - specialPrice;
            }
            return 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
}
