package com.beautysalon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beautysalon.entity.Appointment;
import com.beautysalon.entity.CrmCustomer;
import com.beautysalon.entity.SysNotification;
import com.beautysalon.mapper.AppointmentMapper;
import com.beautysalon.mapper.CrmCustomerMapper;
import com.beautysalon.mapper.SysNotificationMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.beautysalon.service.SysNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 消息通知服务实现类
 *
 * @author BeautySalon Team
 */
@Slf4j
@Service
public class SysNotificationServiceImpl extends ServiceImpl<SysNotificationMapper, SysNotification> implements SysNotificationService {

    @Resource
    private AppointmentMapper appointmentMapper;

    @Resource
    private CrmCustomerMapper customerMapper;

    @Override
    public void send(SysNotification notification) {
        // 设置默认值
        if (notification.getStatus() == null) {
            notification.setStatus(0);
        }
        if (notification.getReadStatus() == null) {
            notification.setReadStatus(0);
        }
        if (notification.getCreateTime() == null) {
            notification.setCreateTime(LocalDateTime.now());
        }
        
        // 保存通知记录
        this.save(notification);
        
        // 实际发送逻辑 (模拟发送)
        try {
            log.info("正在发送通知: {}, 渠道: {}", notification.getTitle(), notification.getChannel());
            
            // 模拟发送延迟
            Thread.sleep(100);
            
            notification.setStatus(1); // 发送成功
            notification.setSendTime(LocalDateTime.now());
            this.updateById(notification);
            
            log.info("通知发送成功: id={}", notification.getId());
        } catch (Exception e) {
            log.error("通知发送失败: {}", e.getMessage());
            notification.setStatus(2); // 发送失败
            notification.setErrorMsg(e.getMessage());
            this.updateById(notification);
        }
    }

    @Async
    @Override
    public void sendAppointmentSuccess(Long appointmentId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            log.warn("发送预约成功通知失败：预约ID {} 不存在", appointmentId);
            return;
        }

        SysNotification notification = new SysNotification();
        notification.setStoreId(appointment.getStoreId());
        notification.setCustomerId(appointment.getCustomerId());
        notification.setTitle("预约成功通知");
        notification.setContent(String.format("尊敬的客户，您的预约已成功！项目：%s，时间：%s %s，美容师：%s。期待您的光临！",
                appointment.getServiceItemName(),
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getBeauticianName()));
        notification.setType(1); // 预约成功
        notification.setChannel(2); // 模拟短信渠道
        
        this.send(notification);
    }
}
