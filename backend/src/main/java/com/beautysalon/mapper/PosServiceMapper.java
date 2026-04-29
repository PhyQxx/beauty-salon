package com.beautysalon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beautysalon.entity.PosService;
import org.apache.ibatis.annotations.Mapper;

/**
 * 服务项目Mapper接口
 * 继承MyBatis-Plus BaseMapper，提供基本的CRUD操作
 *
 * @author BeautySalon Team
 */
@Mapper
public interface PosServiceMapper extends BaseMapper<PosService> {

    /**
     * 根据项目编码查询
     *
     * @param code 项目编码
     * @return 服务项目信息
     */
    PosService selectByCode(String code);
}
