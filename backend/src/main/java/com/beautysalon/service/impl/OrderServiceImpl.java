package com.beautysalon.service.impl;

import com.beautysalon.common.SecurityUtils;
import com.beautysalon.dto.OrderCreateDTO;
import com.beautysalon.entity.PosOrder;
import com.beautysalon.entity.PosOrderItem;
import com.beautysalon.mapper.PosOrderMapper;
import com.beautysalon.mapper.PosRechargeMapper;
import com.beautysalon.service.OrderService;
import com.beautysalon.service.RechargeService;
import com.beautysalon.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 订单服务实现类
 * 实现订单创建、支付、退款、取消、完成、日周月报表等功能
 *
 * @author BeautySalon Team
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private PosOrderMapper orderMapper;

    @Autowired
    private PosRechargeMapper rechargeMapper;

    @Autowired
    private RechargeService rechargeService;

    /**
     * 订单状态常量
     */
    private static final int STATUS_UNPAID = 0;      // 待支付
    private static final int STATUS_PAID = 1;         // 已支付（进行中）
    private static final int STATUS_COMPLETED = 2;    // 已完成
    private static final int STATUS_CANCELLED = 3;    // 已取消
    private static final int STATUS_REFUNDED = 4;    // 已退款

    /**
     * 支付状态常量
     */
    private static final int PAY_STATUS_UNPAID = 0;    // 未支付
    private static final int PAY_STATUS_PAID = 1;      // 已支付
    private static final int PAY_STATUS_PARTIAL = 2;  // 部分支付
    private static final int PAY_STATUS_REFUNDED = 3; // 已退款

    /**
     * 订单类型常量
     */
    private static final int ORDER_TYPE_SERVICE = 1;  // 服务订单
    private static final int ORDER_TYPE_PRODUCT = 2;  // 商品订单
    private static final int ORDER_TYPE_PACKAGE = 3;  // 套餐订单

    // ==================== 订单创建 ====================

    /**
     * 创建服务订单
     */
    @Override
    @Transactional
    public Map<String, Object> createServiceOrder(OrderCreateDTO dto) {
        dto.setOrderType(ORDER_TYPE_SERVICE);
        return createOrder(dto);
    }

    /**
     * 创建商品订单
     */
    @Override
    @Transactional
    public Map<String, Object> createProductOrder(OrderCreateDTO dto) {
        dto.setOrderType(ORDER_TYPE_PRODUCT);
        return createOrder(dto);
    }

    /**
     * 创建套餐订单
     */
    @Override
    @Transactional
    public Map<String, Object> createPackageOrder(OrderCreateDTO dto) {
        dto.setOrderType(ORDER_TYPE_PACKAGE);
        return createOrder(dto);
    }

    /**
     * 通用订单创建逻辑
     */
    private Map<String, Object> createOrder(OrderCreateDTO dto) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 验证订单数据
        Map<String, Object> validateResult = validateOrder(dto);
        if (!(boolean) validateResult.get("valid")) {
            result.put("success", false);
            result.put("message", validateResult.get("message"));
            return result;
        }
        
        // 2. 计算订单金额
        Map<String, BigDecimal> amountInfo = calculateAmount(dto);
        BigDecimal totalAmount = amountInfo.get("totalAmount");
        BigDecimal discountAmount = amountInfo.get("discountAmount");
        BigDecimal payAmount = amountInfo.get("payAmount");
        
        // 3. 生成订单号
        String orderNo = generateOrderNo("DD");
        
        // 4. 创建订单
        PosOrder order = new PosOrder();
        order.setOrderNo(orderNo);
        order.setCustomerId(dto.getCustomerId());
        order.setMembershipCardId(dto.getMembershipCardId());
        order.setOrderType(dto.getOrderType());
        order.setSource(dto.getSource() != null ? dto.getSource() : 1); // 默认POS
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setPayAmount(payAmount);
        order.setBalancePayAmount(dto.getBalancePayAmount() != null ? dto.getBalancePayAmount() : BigDecimal.ZERO);
        order.setCashPayAmount(dto.getCashPayAmount() != null ? dto.getCashPayAmount() : BigDecimal.ZERO);
        order.setThirdPayAmount(dto.getThirdPayAmount() != null ? dto.getThirdPayAmount() : BigDecimal.ZERO);
        order.setPayStatus(PAY_STATUS_UNPAID);
        order.setStatus(STATUS_UNPAID);
        order.setBeauticianId(dto.getBeauticianId());
        order.setAppointmentId(dto.getAppointmentId());
        order.setRemark(dto.getRemark());
        order.setOperatorId(dto.getOperatorId());
        order.setStoreId(dto.getStoreId());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setDeleted(0);
        
        orderMapper.insert(order);
        
        // 5. 创建订单明细
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<PosOrderItem> items = new ArrayList<>();
            int sortOrder = 1;
            
            for (OrderCreateDTO.OrderItemDTO itemDTO : dto.getItems()) {
                PosOrderItem item = new PosOrderItem();
                item.setOrderId(order.getId());
                item.setOrderNo(orderNo);
                item.setItemType(itemDTO.getItemType());
                item.setProductId(itemDTO.getProductId());
                item.setProductName(itemDTO.getProductName());
                item.setUnitPrice(itemDTO.getUnitPrice());
                item.setQuantity(itemDTO.getQuantity());
                item.setSubtotal(itemDTO.getSubtotal());
                item.setDiscountAmount(BigDecimal.ZERO);
                item.setDiscountedPrice(itemDTO.getSubtotal());
                item.setIsGift(0);
                item.setBeauticianId(itemDTO.getBeauticianId());
                
                // 套餐设置有效期
                if (dto.getOrderType() == ORDER_TYPE_PACKAGE && itemDTO.getValidDays() != null) {
                    item.setValidFrom(LocalDateTime.now());
                    item.setValidTo(LocalDateTime.now().plusDays(itemDTO.getValidDays()));
                }
                
                item.setUsed(0);
                item.setSortOrder(sortOrder++);
                item.setCreateTime(LocalDateTime.now());
                item.setUpdateTime(LocalDateTime.now());
                
                items.add(item);
            }
            
            orderMapper.insertItems(items);
        }
        
        result.put("success", true);
        result.put("message", "订单创建成功");
        result.put("orderId", order.getId());
        result.put("orderNo", orderNo);
        result.put("totalAmount", totalAmount);
        result.put("discountAmount", discountAmount);
        result.put("payAmount", payAmount);
        
        return result;
    }

    /**
     * 支付订单
     */
    @Override
    @Transactional
    public Map<String, Object> pay(Long orderId, Integer payType, Long operatorId) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 查询订单
        PosOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }
        
        // 2. 检查订单状态
        if (order.getPayStatus() == PAY_STATUS_PAID) {
            result.put("success", false);
            result.put("message", "订单已支付");
            return result;
        }
        
        if (order.getStatus() == STATUS_CANCELLED || order.getStatus() == STATUS_REFUNDED) {
            result.put("success", false);
            result.put("message", "订单已取消或已退款");
            return result;
        }
        
        // 3. 如果使用余额支付，扣减余额
        BigDecimal balancePayAmount = order.getBalancePayAmount();
        if (balancePayAmount != null && balancePayAmount.compareTo(BigDecimal.ZERO) > 0) {
            Map<String, Object> deductResult = rechargeService.refund(
                    orderId, 
                    order.getCustomerId(), 
                    balancePayAmount, 
                    "订单支付：" + order.getOrderNo(), 
                    operatorId
            );
            if (!(boolean) deductResult.get("success")) {
                result.put("success", false);
                result.put("message", "余额不足");
                return result;
            }
        }
        
        // 4. 更新订单状态
        order.setPayStatus(PAY_STATUS_PAID);
        order.setStatus(STATUS_PAID);
        order.setPayType(payType);
        order.setPayTime(LocalDateTime.now());
        order.setOperatorId(operatorId);
        order.setUpdateTime(LocalDateTime.now());
        
        orderMapper.update(order);
        
        result.put("success", true);
        result.put("message", "支付成功");
        result.put("orderId", orderId);
        result.put("orderNo", order.getOrderNo());
        result.put("payAmount", order.getPayAmount());
        
        return result;
    }

    /**
     * 消费扣款（使用会员卡余额）
     */
    @Override
    @Transactional
    public Map<String, Object> deductBalance(Long customerId, BigDecimal amount, Long orderId, Long operatorId) {
        Map<String, Object> result = new HashMap<>();
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            result.put("success", false);
            result.put("message", "扣款金额必须大于0");
            return result;
        }
        
        // 获取当前余额
        BigDecimal currentBalance = rechargeService.getCustomerBalance(customerId);
        if (currentBalance.compareTo(amount) < 0) {
            result.put("success", false);
            result.put("message", "余额不足，当前余额：" + currentBalance);
            return result;
        }
        
        // 扣减余额（通过退款返还的方式减少余额）
        Map<String, Object> deductResult = rechargeService.refund(
                orderId,
                customerId,
                amount,
                "消费扣款",
                operatorId
        );
        
        return deductResult;
    }

    /**
     * 订单退款
     */
    @Override
    @Transactional
    public Map<String, Object> refund(Long orderId, BigDecimal refundAmount, String reason, Long operatorId) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 查询订单
        PosOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }
        
        // 2. 检查订单状态
        if (order.getPayStatus() != PAY_STATUS_PAID) {
            result.put("success", false);
            result.put("message", "订单未支付，无法退款");
            return result;
        }
        
        if (order.getStatus() == STATUS_REFUNDED) {
            result.put("success", false);
            result.put("message", "订单已退款");
            return result;
        }
        
        // 3. 检查退款金额
        BigDecimal maxRefundAmount = order.getPayAmount().subtract(
                order.getRefundAmount() != null ? order.getRefundAmount() : BigDecimal.ZERO);
        
        if (refundAmount.compareTo(maxRefundAmount) > 0) {
            result.put("success", false);
            result.put("message", "退款金额超出可退金额：" + maxRefundAmount);
            return result;
        }
        
        // 4. 计算退款后余额变化
        BigDecimal currentBalance = rechargeService.getCustomerBalance(order.getCustomerId());
        BigDecimal newBalance = currentBalance.subtract(refundAmount);
        
        // 5. 更新订单
        BigDecimal newRefundAmount = (order.getRefundAmount() != null ? order.getRefundAmount() : BigDecimal.ZERO).add(refundAmount);
        order.setRefundAmount(newRefundAmount);
        order.setRefundReason(reason);
        order.setRefundTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        // 如果全额退款，更新状态
        if (newRefundAmount.compareTo(order.getPayAmount()) >= 0) {
            order.setPayStatus(PAY_STATUS_REFUNDED);
            order.setStatus(STATUS_REFUNDED);
        }
        
        orderMapper.update(order);
        
        // 6. 退还余额（如果是会员卡支付或部分使用余额）
        if (order.getBalancePayAmount() != null && order.getBalancePayAmount().compareTo(BigDecimal.ZERO) > 0) {
            rechargeService.refund(orderId, order.getCustomerId(), 
                    refundAmount.min(order.getBalancePayAmount()), 
                    "退款返还：" + order.getOrderNo(), 
                    operatorId);
        }
        
        result.put("success", true);
        result.put("message", "退款成功");
        result.put("refundAmount", refundAmount);
        result.put("remainingBalance", newBalance);
        
        return result;
    }

    /**
     * 取消订单
     */
    @Override
    @Transactional
    public Map<String, Object> cancel(Long orderId, String reason, Long operatorId) {
        Map<String, Object> result = new HashMap<>();
        
        PosOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }
        
        if (order.getStatus() == STATUS_COMPLETED) {
            result.put("success", false);
            result.put("message", "订单已完成，无法取消");
            return result;
        }
        
        if (order.getStatus() == STATUS_CANCELLED) {
            result.put("success", false);
            result.put("message", "订单已取消");
            return result;
        }
        
        // 如果已支付，先退款
        if (order.getPayStatus() == PAY_STATUS_PAID) {
            refund(orderId, order.getPayAmount(), "订单取消", operatorId);
        }
        
        order.setStatus(STATUS_CANCELLED);
        order.setCancelReason(reason);
        order.setUpdateTime(LocalDateTime.now());
        
        orderMapper.update(order);
        
        result.put("success", true);
        result.put("message", "订单已取消");
        
        return result;
    }

    /**
     * 完成订单
     */
    @Override
    @Transactional
    public Map<String, Object> complete(Long orderId, Long operatorId) {
        Map<String, Object> result = new HashMap<>();
        
        PosOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }
        
        if (order.getStatus() != STATUS_PAID) {
            result.put("success", false);
            result.put("message", "订单未支付或已取消");
            return result;
        }
        
        order.setStatus(STATUS_COMPLETED);
        order.setCompleteTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        orderMapper.update(order);
        
        result.put("success", true);
        result.put("message", "订单已完成");
        
        return result;
    }

    // ==================== 查询操作 ====================

    /**
     * 分页查询订单列表
     */
    @Override
    public List<OrderVO> listPage(int page, int limit, String orderNo, Long customerId, Integer orderType,
                                 Integer payStatus, Integer status, String startDate, String endDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", (page - 1) * limit);
        params.put("limit", limit);
        
        // 数据隔离：如果不是超级管理员，只能查看自己门店的订单
        if (!SecurityUtils.isSuperAdmin()) {
            params.put("storeId", SecurityUtils.getCurrentStoreId());
        }
        
        if (orderNo != null && !orderNo.isEmpty()) {
            params.put("orderNo", orderNo);
        }
        if (customerId != null) {
            params.put("customerId", customerId);
        }
        if (orderType != null) {
            params.put("orderType", orderType);
        }
        if (payStatus != null) {
            params.put("payStatus", payStatus);
        }
        if (status != null) {
            params.put("status", status);
        }
        if (startDate != null && !startDate.isEmpty()) {
            params.put("startDate", startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            params.put("endDate", endDate);
        }
        
        List<PosOrder> list = orderMapper.selectPage(params);
        return convertToVO(list);
    }

    /**
     * 根据ID获取订单详情
     */
    @Override
    public OrderVO getById(Long orderId) {
        PosOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return null;
        }
        
        OrderVO vo = convertToVO(order);
        
        // 加载订单明细
        List<PosOrderItem> items = orderMapper.selectItemsByOrderId(orderId);
        vo.setItems(convertItemToVO(items));
        
        return vo;
    }

    /**
     * 根据订单号获取订单
     */
    @Override
    public OrderVO getByOrderNo(String orderNo) {
        PosOrder order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return null;
        }
        return getById(order.getId());
    }

    /**
     * 获取客户的订单列表
     */
    @Override
    public List<OrderVO> getByCustomerId(Long customerId) {
        List<PosOrder> list = orderMapper.selectByCustomerId(customerId);
        return convertToVO(list);
    }

    /**
     * 使用套餐明细
     */
    @Override
    @Transactional
    public Map<String, Object> usePackageItem(Long itemId, Long beauticianId) {
        Map<String, Object> result = new HashMap<>();
        
        PosOrderItem item = orderMapper.selectItemById(itemId);
        if (item == null) {
            result.put("success", false);
            result.put("message", "套餐明细不存在");
            return result;
        }
        
        if (item.getItemType() != 3) { // 3-套餐
            result.put("success", false);
            result.put("message", "只有套餐项目才能使用");
            return result;
        }
        
        if (item.getUsed() == 1) {
            result.put("success", false);
            result.put("message", "套餐已使用");
            return result;
        }
        
        // 检查有效期
        if (item.getValidTo() != null && LocalDateTime.now().isAfter(item.getValidTo())) {
            result.put("success", false);
            result.put("message", "套餐已过期");
            return result;
        }
        
        orderMapper.markItemUsed(itemId, LocalDateTime.now());
        
        result.put("success", true);
        result.put("message", "套餐使用成功");
        
        return result;
    }

    // ==================== 报表统计 ====================

    /**
     * 日结报表
     */
    @Override
    public Map<String, Object> getDailyReport(String date) {
        Map<String, Object> result = new HashMap<>();
        
        LocalDateTime startTime = LocalDateTime.parse(date + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse(date + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        // 订单统计
        BigDecimal totalPayAmount = orderMapper.sumPayAmountByDateRange(startTime, endTime);
        BigDecimal totalRefundAmount = orderMapper.sumRefundAmountByDateRange(startTime, endTime);
        int orderCount = orderMapper.countByDateRange(startTime, endTime);
        
        // 按支付方式统计
        List<Map<String, Object>> payTypeStats = orderMapper.sumAmountByPayType(startTime, endTime);
        
        // 按订单类型统计
        List<Map<String, Object>> orderTypeStats = orderMapper.sumAmountByOrderType(startTime, endTime);
        
        result.put("date", date);
        result.put("orderCount", orderCount);
        result.put("totalPayAmount", totalPayAmount != null ? totalPayAmount : BigDecimal.ZERO);
        result.put("totalRefundAmount", totalRefundAmount != null ? totalRefundAmount : BigDecimal.ZERO);
        result.put("netAmount", (totalPayAmount != null ? totalPayAmount : BigDecimal.ZERO)
                .subtract(totalRefundAmount != null ? totalRefundAmount : BigDecimal.ZERO));
        result.put("payTypeStats", payTypeStats);
        result.put("orderTypeStats", orderTypeStats);
        
        return result;
    }

    /**
     * 周报表
     */
    @Override
    public Map<String, Object> getWeeklyReport(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        LocalDateTime startTime = LocalDateTime.parse(startDate + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse(endDate + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        BigDecimal totalPayAmount = orderMapper.sumPayAmountByDateRange(startTime, endTime);
        BigDecimal totalRefundAmount = orderMapper.sumRefundAmountByDateRange(startTime, endTime);
        int orderCount = orderMapper.countByDateRange(startTime, endTime);
        
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("orderCount", orderCount);
        result.put("totalPayAmount", totalPayAmount != null ? totalPayAmount : BigDecimal.ZERO);
        result.put("totalRefundAmount", totalRefundAmount != null ? totalRefundAmount : BigDecimal.ZERO);
        result.put("netAmount", (totalPayAmount != null ? totalPayAmount : BigDecimal.ZERO)
                .subtract(totalRefundAmount != null ? totalRefundAmount : BigDecimal.ZERO));
        
        return result;
    }

    /**
     * 月报表
     */
    @Override
    public Map<String, Object> getMonthlyReport(String yearMonth) {
        String[] parts = yearMonth.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        
        LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime endTime = startTime.plusMonths(1).minusSeconds(1);
        
        BigDecimal totalPayAmount = orderMapper.sumPayAmountByDateRange(startTime, endTime);
        BigDecimal totalRefundAmount = orderMapper.sumRefundAmountByDateRange(startTime, endTime);
        int orderCount = orderMapper.countByDateRange(startTime, endTime);
        
        Map<String, Object> result = new HashMap<>();
        result.put("yearMonth", yearMonth);
        result.put("orderCount", orderCount);
        result.put("totalPayAmount", totalPayAmount != null ? totalPayAmount : BigDecimal.ZERO);
        result.put("totalRefundAmount", totalRefundAmount != null ? totalRefundAmount : BigDecimal.ZERO);
        result.put("netAmount", (totalPayAmount != null ? totalPayAmount : BigDecimal.ZERO)
                .subtract(totalRefundAmount != null ? totalRefundAmount : BigDecimal.ZERO));
        
        return result;
    }

    /**
     * 营业统计
     */
    @Override
    public Map<String, Object> getStatistics(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        LocalDateTime startTime = LocalDateTime.parse(startDate + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse(endDate + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        // 订单统计
        BigDecimal totalPayAmount = orderMapper.sumPayAmountByDateRange(startTime, endTime);
        BigDecimal totalRefundAmount = orderMapper.sumRefundAmountByDateRange(startTime, endTime);
        int orderCount = orderMapper.countByDateRange(startTime, endTime);
        
        // 充值统计
        Map<String, Object> rechargeStats = rechargeService.getWeeklyReport(startDate, endDate);
        
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("orderCount", orderCount);
        result.put("totalPayAmount", totalPayAmount != null ? totalPayAmount : BigDecimal.ZERO);
        result.put("totalRefundAmount", totalRefundAmount != null ? totalRefundAmount : BigDecimal.ZERO);
        result.put("netAmount", (totalPayAmount != null ? totalPayAmount : BigDecimal.ZERO)
                .subtract(totalRefundAmount != null ? totalRefundAmount : BigDecimal.ZERO));
        result.put("rechargeTotalAmount", rechargeStats.get("totalAmount") != null ? rechargeStats.get("totalAmount") : BigDecimal.ZERO);
        result.put("rechargeGiftAmount", rechargeStats.get("totalGiftAmount") != null ? rechargeStats.get("totalGiftAmount") : BigDecimal.ZERO);
        result.put("rechargeCount", rechargeStats.get("rechargeCount") != null ? rechargeStats.get("rechargeCount") : 0);
        
        return result;
    }

    // ==================== 辅助方法 ====================

    /**
     * 验证订单数据
     */
    @Override
    public Map<String, Object> validateOrder(OrderCreateDTO dto) {
        Map<String, Object> result = new HashMap<>();
        result.put("valid", true);
        
        if (dto.getCustomerId() == null) {
            result.put("valid", false);
            result.put("message", "客户ID不能为空");
            return result;
        }
        
        if (dto.getOrderType() == null) {
            result.put("valid", false);
            result.put("message", "订单类型不能为空");
            return result;
        }
        
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            result.put("valid", false);
            result.put("message", "订单明细不能为空");
            return result;
        }
        
        for (OrderCreateDTO.OrderItemDTO item : dto.getItems()) {
            if (item.getProductId() == null) {
                result.put("valid", false);
                result.put("message", "商品/服务ID不能为空");
                return result;
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                result.put("valid", false);
                result.put("message", "商品/服务数量必须大于0");
                return result;
            }
        }
        
        return result;
    }

    /**
     * 生成订单号
     */
    @Override
    public String generateOrderNo(String prefix) {
        return prefix + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", new Random().nextInt(10000));
    }

    /**
     * 计算订单金额
     */
    @Override
    public Map<String, BigDecimal> calculateAmount(OrderCreateDTO dto) {
        Map<String, BigDecimal> result = new HashMap<>();
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal discountAmount = dto.getDiscountAmount() != null ? dto.getDiscountAmount() : BigDecimal.ZERO;
        
        if (dto.getItems() != null) {
            for (OrderCreateDTO.OrderItemDTO item : dto.getItems()) {
                if (item.getSubtotal() != null) {
                    totalAmount = totalAmount.add(item.getSubtotal());
                } else if (item.getUnitPrice() != null && item.getQuantity() != null) {
                    totalAmount = totalAmount.add(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
                }
            }
        }
        
        BigDecimal payAmount = totalAmount.subtract(discountAmount);
        
        result.put("totalAmount", totalAmount);
        result.put("discountAmount", discountAmount);
        result.put("payAmount", payAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : payAmount);
        
        return result;
    }

    // ==================== 转换方法 ====================

    /**
     * 将订单实体转换为VO
     */
    private OrderVO convertToVO(PosOrder order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setCustomerId(order.getCustomerId());
        vo.setCustomerName(order.getCustomerName());
        vo.setCustomerPhone(order.getCustomerPhone());
        vo.setOrderType(order.getOrderType());
        vo.setOrderTypeName(getOrderTypeName(order.getOrderType()));
        vo.setSourceName(getSourceName(order.getSource()));
        vo.setTotalAmount(order.getTotalAmount());
        vo.setDiscountAmount(order.getDiscountAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setBalancePayAmount(order.getBalancePayAmount());
        vo.setCashPayAmount(order.getCashPayAmount());
        vo.setThirdPayAmount(order.getThirdPayAmount());
        vo.setPayStatus(order.getPayStatus());
        vo.setPayStatusName(getPayStatusName(order.getPayStatus()));
        vo.setPayTypeName(getPayTypeName(order.getPayType()));
        vo.setPayTime(order.getPayTime() != null ? order.getPayTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
        vo.setStatus(order.getStatus());
        vo.setStatusName(getStatusName(order.getStatus()));
        vo.setCompleteTime(order.getCompleteTime() != null ? order.getCompleteTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
        vo.setBeauticianName(order.getBeauticianName());
        vo.setRemark(order.getRemark());
        vo.setRefundAmount(order.getRefundAmount());
        vo.setOperatorName(order.getOperatorName());
        vo.setCreateTime(order.getCreateTime() != null ? order.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
        return vo;
    }

    /**
     * 将订单明细实体转换为VO
     */
    private List<OrderVO.OrderItemVO> convertItemToVO(List<PosOrderItem> items) {
        List<OrderVO.OrderItemVO> voList = new ArrayList<>();
        for (PosOrderItem item : items) {
            OrderVO.OrderItemVO vo = new OrderVO.OrderItemVO();
            vo.setId(item.getId());
            vo.setItemTypeName(getItemTypeName(item.getItemType()));
            vo.setProductName(item.getProductName());
            vo.setCategory(item.getCategory());
            vo.setUnitPrice(item.getUnitPrice());
            vo.setQuantity(item.getQuantity());
            vo.setSubtotal(item.getSubtotal());
            vo.setDiscountAmount(item.getDiscountAmount());
            vo.setDiscountedPrice(item.getDiscountedPrice());
            vo.setIsGiftName(item.getIsGift() == 1 ? "是" : "否");
            vo.setBeauticianName(item.getBeauticianName());
            vo.setUsedName(item.getUsed() == 1 ? "已使用" : "未使用");
            
            if (item.getValidFrom() != null && item.getValidTo() != null) {
                vo.setValidPeriod(item.getValidFrom().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) 
                        + " 至 " + item.getValidTo().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 将订单列表转换为VO列表
     */
    private List<OrderVO> convertToVO(List<PosOrder> list) {
        List<OrderVO> voList = new ArrayList<>();
        for (PosOrder order : list) {
            voList.add(convertToVO(order));
        }
        return voList;
    }

    // ==================== 名称转换方法 ====================

    private String getOrderTypeName(Integer orderType) {
        if (orderType == null) return "";
        switch (orderType) {
            case 1: return "服务订单";
            case 2: return "商品订单";
            case 3: return "套餐订单";
            case 4: return "充值订单";
            default: return "其他";
        }
    }

    private String getSourceName(Integer source) {
        if (source == null) return "";
        switch (source) {
            case 1: return "POS";
            case 2: return "小程序";
            case 3: return "美团";
            case 4: return "大众点评";
            case 5: return "其他";
            default: return "其他";
        }
    }

    private String getPayStatusName(Integer payStatus) {
        if (payStatus == null) return "";
        switch (payStatus) {
            case 0: return "未支付";
            case 1: return "已支付";
            case 2: return "部分支付";
            case 3: return "已退款";
            default: return "其他";
        }
    }

    private String getPayTypeName(Integer payType) {
        if (payType == null) return "";
        switch (payType) {
            case 1: return "现金";
            case 2: return "银行卡";
            case 3: return "微信";
            case 4: return "支付宝";
            case 5: return "会员卡";
            case 6: return "组合支付";
            default: return "其他";
        }
    }

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "待支付";
            case 1: return "进行中";
            case 2: return "已完成";
            case 3: return "已取消";
            case 4: return "已退款";
            default: return "其他";
        }
    }

    private String getItemTypeName(Integer itemType) {
        if (itemType == null) return "";
        switch (itemType) {
            case 1: return "服务";
            case 2: return "商品";
            case 3: return "套餐";
            default: return "其他";
        }
    }
}
