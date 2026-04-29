package com.beautysalon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beautysalon.entity.PosMembershipCard;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员卡Mapper接口
 * 继承MyBatis-Plus BaseMapper，提供基本的CRUD操作
 *
 * @author BeautySalon Team
 */
@Mapper
public interface PosMembershipCardMapper extends BaseMapper<PosMembershipCard> {

    /**
     * 根据卡编码查询
     *
     * @param code 卡编码
     * @return 会员卡信息
     */
    PosMembershipCard selectByCode(String code);
}
