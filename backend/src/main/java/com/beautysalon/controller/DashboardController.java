package com.beautysalon.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beautysalon.entity.CrmCustomer;
import com.beautysalon.mapper.CrmCustomerMapper;
import com.beautysalon.service.AppointmentService;
import com.beautysalon.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 首页数据 Controller
 *
 * @author BeautySalon Team
 */
@Slf4j
@Api(tags = "首页数据")
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Resource
    private AppointmentService appointmentService;

    @Resource
    private OrderService orderService;

    @Resource
    private CrmCustomerMapper customerMapper;

    /**
     * 获取首页统计数据
     */
    @ApiOperation("获取首页统计数据")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> stats = new HashMap<>();

            // 今日预约数
            Map<String, Object> appointmentStats = appointmentService.getTodayOverview();
            stats.put("todayAppointments", appointmentStats.get("total") != null ? appointmentStats.get("total") : 0);
            stats.put("pendingAppointments", appointmentStats.get("pending") != null ? appointmentStats.get("pending") : 0);

            // 今日订单数
            Map<String, Object> orderStats = orderService.getStatistics(null, null);
            stats.put("todayOrders", orderStats.get("orderCount") != null ? orderStats.get("orderCount") : 0);
            stats.put("todayRevenue", orderStats.get("totalAmount") != null ? orderStats.get("totalAmount") : 0);

            // 客户总数
            LambdaQueryWrapper<CrmCustomer> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CrmCustomer::getDeleted, 0);
            Long totalCustomers = customerMapper.selectCount(wrapper);
            stats.put("totalCustomers", totalCustomers);

            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", stats);
        } catch (Exception e) {
            log.error("获取首页统计数据失败", e);
            result.put("code", 500);
            result.put("message", "查询失败");
            result.put("data", new HashMap<>());
        }
        return ResponseEntity.ok(result);
    }
}
