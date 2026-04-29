package com.beautysalon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beautysalon.entity.PosCustomerCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户优惠券Mapper
 *
 * @author BeautySalon Team
 */
@Mapper
public interface PosCustomerCouponMapper extends BaseMapper<PosCustomerCoupon> {

    /**
     * 查询客户可用的优惠券
     */
    List<PosCustomerCoupon> selectAvailableByCustomerId(@Param("customerId") Long customerId);

    /**
     * 查询客户已使用的优惠券
     */
    List<PosCustomerCoupon> selectUsedByCustomerId(@Param("customerId") Long customerId);
}
