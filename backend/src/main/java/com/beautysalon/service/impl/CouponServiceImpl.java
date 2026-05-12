package com.beautysalon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beautysalon.entity.CrmCustomer;
import com.beautysalon.entity.PosCoupon;
import com.beautysalon.entity.PosCustomerCoupon;
import com.beautysalon.mapper.CrmCustomerMapper;
import com.beautysalon.mapper.PosCouponMapper;
import com.beautysalon.mapper.PosCustomerCouponMapper;
import com.beautysalon.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 优惠券服务实现
 *
 * @author BeautySalon Team
 */
@Slf4j
@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private PosCouponMapper couponMapper;

    @Autowired
    private PosCustomerCouponMapper customerCouponMapper;

    @Autowired
    private CrmCustomerMapper customerMapper;

    @Override
    public Map<String, Object> queryCouponPage(Integer page, Integer limit, String keyword, Integer status) {
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.hasText(keyword)) params.put("keyword", keyword);
        if (status != null) params.put("status", status);
        params.put("offset", (page - 1) * limit);
        params.put("limit", limit);

        List<PosCoupon> list = couponMapper.selectPage(params);
        // 简单计数：从list大小估算，实际应查count
        int total = list.size();

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", page);
        result.put("pageSize", limit);
        return result;
    }

    @Override
    public PosCoupon getCouponById(Long id) {
        PosCoupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }
        return coupon;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCoupon(PosCoupon coupon) {
        coupon.setCode(UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        coupon.setStatus(1);
        coupon.setUseCount(0);
        coupon.setReceiveCount(0);
        coupon.setCreateTime(LocalDateTime.now());
        coupon.setUpdateTime(LocalDateTime.now());
        coupon.setDeleted(0);
        couponMapper.insert(coupon);
        return coupon.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCoupon(Long id, PosCoupon coupon) {
        PosCoupon existing = couponMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("优惠券不存在");
        }
        coupon.setId(id);
        coupon.setUpdateTime(LocalDateTime.now());
        return couponMapper.update(coupon) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCoupon(Long id) {
        PosCoupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }
        coupon.setDeleted(1);
        coupon.setUpdateTime(LocalDateTime.now());
        return couponMapper.update(coupon) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean distributeCoupon(Long couponId, Long customerId) {
        PosCoupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }
        if (coupon.getStatus() != 1) {
            throw new RuntimeException("优惠券未激活");
        }
        if (coupon.getTotalCount() != null && coupon.getReceiveCount() >= coupon.getTotalCount()) {
            throw new RuntimeException("优惠券已领完");
        }
        if (coupon.getEndDate() != null && coupon.getEndDate().isBefore(java.time.LocalDate.now())) {
            throw new RuntimeException("优惠券已过期");
        }

        CrmCustomer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new RuntimeException("会员不存在");
        }

        // 增加领取数量
        coupon.setReceiveCount(coupon.getReceiveCount() == null ? 1 : coupon.getReceiveCount() + 1);
        coupon.setUpdateTime(LocalDateTime.now());
        couponMapper.update(coupon);

        // 创建会员优惠券记录
        PosCustomerCoupon customerCoupon = new PosCustomerCoupon();
        customerCoupon.setCouponId(couponId);
        customerCoupon.setCustomerId(customerId);
        customerCoupon.setReceiveTime(LocalDateTime.now());
        customerCoupon.setStatus(1);
        customerCoupon.setCreateTime(LocalDateTime.now());
        customerCoupon.setUpdateTime(LocalDateTime.now());
        customerCoupon.setDeleted(0);
        if (coupon.getValidDays() != null) {
            customerCoupon.setExpiredTime(LocalDateTime.now().plusDays(coupon.getValidDays()));
        } else if (coupon.getEndDate() != null) {
            customerCoupon.setExpiredTime(coupon.getEndDate().atStartOfDay());
        }
        customerCouponMapper.insert(customerCoupon);

        log.info("发放优惠券: couponId={}, customerId={}", couponId, customerId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean receiveCoupon(Long couponId, Long customerId) {
        // 领取和发放逻辑相同
        return distributeCoupon(couponId, customerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean useCoupon(Long customerCouponId, Long orderId) {
        PosCustomerCoupon customerCoupon = customerCouponMapper.selectById(customerCouponId);
        if (customerCoupon == null) {
            throw new RuntimeException("会员优惠券不存在");
        }
        if (customerCoupon.getStatus() != 1) {
            throw new RuntimeException("优惠券状态不可使用");
        }
        if (customerCoupon.getExpiredTime() != null && customerCoupon.getExpiredTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("优惠券已过期");
        }

        // 更新为已使用
        customerCoupon.setStatus(2);
        customerCoupon.setUseTime(LocalDateTime.now());
        customerCoupon.setOrderId(orderId);
        customerCoupon.setUpdateTime(LocalDateTime.now());
        customerCouponMapper.updateById(customerCoupon);

        // 更新优惠券已使用数量
        PosCoupon coupon = couponMapper.selectById(customerCoupon.getCouponId());
        if (coupon != null) {
            coupon.setUseCount(coupon.getUseCount() == null ? 1 : coupon.getUseCount() + 1);
            couponMapper.update(coupon);
        }

        log.info("使用优惠券: customerCouponId={}, orderId={}", customerCouponId, orderId);
        return true;
    }

    @Override
    public Map<String, Object> getCustomerCoupons(Long customerId, Integer status) {
        Map<String, Object> result = new HashMap<>();

        LambdaQueryWrapper<PosCustomerCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PosCustomerCoupon::getCustomerId, customerId);
        wrapper.eq(PosCustomerCoupon::getDeleted, 0);

        if (status != null) {
            wrapper.eq(PosCustomerCoupon::getStatus, status);
        }

        wrapper.orderByDesc(PosCustomerCoupon::getReceiveTime);
        List<PosCustomerCoupon> list = customerCouponMapper.selectList(wrapper);

        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", list);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateExpiredCouponsStatus() {
        LambdaQueryWrapper<PosCustomerCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PosCustomerCoupon::getStatus, 1);
        wrapper.lt(PosCustomerCoupon::getExpiredTime, LocalDateTime.now());

        List<PosCustomerCoupon> expiredList = customerCouponMapper.selectList(wrapper);
        for (PosCustomerCoupon cc : expiredList) {
            cc.setStatus(3);
            cc.setUpdateTime(LocalDateTime.now());
            customerCouponMapper.updateById(cc);
        }

        log.info("批量更新过期优惠券: 更新数量={}", expiredList.size());
        return expiredList.size();
    }
}
