package com.beautysalon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beautysalon.entity.Beautician;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 美容师Mapper接口
 * 继承MyBatis-Plus BaseMapper，提供基本的CRUD操作
 *
 * @author BeautySalon Team
 */
@Mapper
public interface BeauticianMapper extends BaseMapper<Beautician> {

    /**
     * 根据工号查询美容师
     *
     * @param code 工号
     * @return 美容师信息
     */
    Beautician selectByCode(@Param("code") String code);

    /**
     * 根据手机号查询美容师
     *
     * @param phone 手机号
     * @return 美容师信息
     */
    Beautician selectByPhone(@Param("phone") String phone);

    /**
     * 查询在职美容师列表
     *
     * @return 在职美容师列表
     */
    List<Beautician> selectActiveList();

    /**
     * 根据等级查询美容师列表
     *
     * @param level 等级
     * @return 美容师列表
     */
    List<Beautician> selectByLevel(@Param("level") Integer level);
}
