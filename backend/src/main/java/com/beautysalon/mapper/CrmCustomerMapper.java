package com.beautysalon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beautysalon.entity.CrmCustomer;
import org.apache.ibatis.annotations.Mapper;

/**
 * CRM客户Mapper接口
 * 继承MyBatis-Plus BaseMapper，提供基本的CRUD操作
 *
 * @author BeautySalon Team
 */
@Mapper
public interface CrmCustomerMapper extends BaseMapper<CrmCustomer> {

    /**
     * 根据手机号查询客户
     *
     * @param phone 手机号码
     * @return 客户信息
     */
    CrmCustomer selectByPhone(String phone);

    /**
     * 根据手机号统计客户数量
     *
     * @param phone 手机号码
     * @return 客户数量
     */
    Integer countByPhone(String phone);
}
