package com.beautysalon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.beautysalon.entity.SysNotification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息通知记录 Mapper 接口
 *
 * @author BeautySalon Team
 */
@Mapper
public interface SysNotificationMapper extends BaseMapper<SysNotification> {
}
