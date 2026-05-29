package com.beautysalon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.beautysalon.entity.SysNotification;

/**
 * 消息通知服务类
 *
 * @author BeautySalon Team
 */
public interface SysNotificationService extends IService<SysNotification> {

    /**
     * 发送通知
     * 
     * @param notification 通知信息
     */
    void send(SysNotification notification);

    /**
     * 异步发送预约成功通知
     * 
     * @param appointmentId 预约ID
     */
    void sendAppointmentSuccess(Long appointmentId);
}
