package com.beautysalon.service.impl;

import com.beautysalon.dto.OrderCreateDTO;
import com.beautysalon.entity.PosOrder;
import com.beautysalon.entity.PosOrderItem;
import com.beautysalon.mapper.PosOrderMapper;
import com.beautysalon.mapper.PosRechargeMapper;
import com.beautysalon.service.RechargeService;
import com.beautysalon.vo.OrderVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private PosOrderMapper orderMapper;

    @Mock
    private PosRechargeMapper rechargeMapper;

    @Mock
    private RechargeService rechargeService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private PosOrder testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new PosOrder();
        testOrder.setId(1L);
        testOrder.setOrderNo("DD202305290001");
        testOrder.setCustomerId(1L);
        testOrder.setPayAmount(new BigDecimal("100.00"));
        testOrder.setPayStatus(0); // UNPAID
        testOrder.setStatus(0); // UNPAID
        testOrder.setCreateTime(LocalDateTime.now());
        testOrder.setUpdateTime(LocalDateTime.now());
    }

    @Test
    void createServiceOrder_Success() {
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setCustomerId(1L);
        dto.setOrderType(1); // SERVICE
        List<OrderCreateDTO.OrderItemDTO> items = new ArrayList<>();
        OrderCreateDTO.OrderItemDTO item = new OrderCreateDTO.OrderItemDTO();
        item.setProductId(1L);
        item.setProductName("Test Service");
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("100.00"));
        item.setSubtotal(new BigDecimal("100.00"));
        item.setItemType(1);
        items.add(item);
        dto.setItems(items);

        when(orderMapper.insert(any(PosOrder.class))).thenAnswer(invocation -> {
            PosOrder order = invocation.getArgument(0);
            order.setId(1L);
            return 1;
        });

        Map<String, Object> result = orderService.createServiceOrder(dto);

        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("orderId"));
        verify(orderMapper).insert(any(PosOrder.class));
        verify(orderMapper).insertItems(anyList());
    }

    @Test
    void pay_Success() {
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        Map<String, Object> result = orderService.pay(1L, 1, 1L); // Pay type 1 (Cash)

        assertTrue((Boolean) result.get("success"));
        assertEquals(1, testOrder.getPayStatus()); // PAID
        assertEquals(1, testOrder.getStatus()); // PAID
        verify(orderMapper).update(testOrder);
    }

    @Test
    void pay_WithBalance_Success() {
        testOrder.setBalancePayAmount(new BigDecimal("50.00"));
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        Map<String, Object> rechargeResult = new HashMap<>();
        rechargeResult.put("success", true);
        when(rechargeService.refund(anyLong(), anyLong(), any(BigDecimal.class), anyString(), anyLong())).thenReturn(rechargeResult);

        Map<String, Object> result = orderService.pay(1L, 5, 1L); // Pay type 5 (Member Card)

        assertTrue((Boolean) result.get("success"));
        verify(rechargeService).refund(eq(1L), eq(1L), eq(new BigDecimal("50.00")), anyString(), eq(1L));
    }

    @Test
    void refund_Success() {
        testOrder.setPayStatus(1); // PAID
        testOrder.setStatus(1); // PAID
        testOrder.setPayAmount(new BigDecimal("100.00"));
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(rechargeService.getCustomerBalance(1L)).thenReturn(new BigDecimal("500.00"));

        Map<String, Object> result = orderService.refund(1L, new BigDecimal("100.00"), "Refund test", 1L);

        assertTrue((Boolean) result.get("success"));
        assertEquals(3, testOrder.getPayStatus()); // REFUNDED
        assertEquals(4, testOrder.getStatus()); // REFUNDED
        verify(orderMapper).update(testOrder);
    }

    @Test
    void cancel_Unpaid_Success() {
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        Map<String, Object> result = orderService.cancel(1L, "Cancel test", 1L);

        assertTrue((Boolean) result.get("success"));
        assertEquals(3, testOrder.getStatus()); // CANCELLED
        verify(orderMapper).update(testOrder);
    }

    @Test
    void complete_Success() {
        testOrder.setPayStatus(1); // PAID
        testOrder.setStatus(1); // PAID
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        Map<String, Object> result = orderService.complete(1L, 1L);

        assertTrue((Boolean) result.get("success"));
        assertEquals(2, testOrder.getStatus()); // COMPLETED
        verify(orderMapper).update(testOrder);
    }
}
