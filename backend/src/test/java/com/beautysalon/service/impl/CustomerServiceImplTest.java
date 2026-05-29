package com.beautysalon.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.beautysalon.dto.CustomerCreateDTO;
import com.beautysalon.dto.CustomerUpdateDTO;
import com.beautysalon.entity.CrmCustomer;
import com.beautysalon.mapper.CrmCustomerMapper;
import com.beautysalon.vo.CustomerVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    private CrmCustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private CrmCustomer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new CrmCustomer();
        testCustomer.setId(1L);
        testCustomer.setName("Test User");
        testCustomer.setPhone("13800138000");
        testCustomer.setBalance(new BigDecimal("100.00"));
        testCustomer.setPoints(100);
        testCustomer.setMemberLevel(1);
        testCustomer.setGender(1);
        testCustomer.setStatus(1);
        testCustomer.setDeleted(0);
        testCustomer.setCreateTime(LocalDateTime.now());
        testCustomer.setUpdateTime(LocalDateTime.now());
    }

    @Test
    void createCustomer_Success() {
        CustomerCreateDTO dto = new CustomerCreateDTO();
        dto.setName("New User");
        dto.setPhone("13900139000");

        when(customerMapper.selectCount(any())).thenReturn(0L);
        when(customerMapper.insert(any(CrmCustomer.class))).thenAnswer(invocation -> {
            CrmCustomer customer = invocation.getArgument(0);
            customer.setId(2L);
            return 1;
        });

        Long id = customerService.createCustomer(dto);

        assertEquals(2L, id);
        verify(customerMapper).insert(any(CrmCustomer.class));
    }

    @Test
    void createCustomer_PhoneExists_ThrowsException() {
        CustomerCreateDTO dto = new CustomerCreateDTO();
        dto.setPhone("13800138000");

        when(customerMapper.selectCount(any())).thenReturn(1L);

        Exception exception = assertThrows(RuntimeException.class, () -> customerService.createCustomer(dto));
        assertEquals("手机号已存在，无法重复注册", exception.getMessage());
    }

    @Test
    void updateCustomer_Success() {
        when(customerMapper.selectById(1L)).thenReturn(testCustomer);
        when(customerMapper.updateById(any(CrmCustomer.class))).thenReturn(1);

        CustomerUpdateDTO dto = new CustomerUpdateDTO();
        dto.setName("Updated Name");

        boolean result = customerService.updateCustomer(1L, dto);

        assertTrue(result);
        assertEquals("Updated Name", testCustomer.getName());
        verify(customerMapper).updateById(testCustomer);
    }

    @Test
    void recharge_Success() {
        when(customerMapper.selectById(1L)).thenReturn(testCustomer);
        when(customerMapper.updateById(any(CrmCustomer.class))).thenReturn(1);

        BigDecimal newBalance = customerService.recharge(1L, new BigDecimal("50.00"), "Recharge test");

        assertEquals(new BigDecimal("150.00"), newBalance);
        verify(customerMapper).updateById(testCustomer);
    }

    @Test
    void consume_Success() {
        when(customerMapper.selectById(1L)).thenReturn(testCustomer);
        when(customerMapper.updateById(any(CrmCustomer.class))).thenReturn(1);

        BigDecimal newBalance = customerService.consume(1L, new BigDecimal("40.00"), "Consume test");

        assertEquals(new BigDecimal("60.00"), newBalance);
        verify(customerMapper).updateById(testCustomer);
    }

    @Test
    void consume_InsufficientBalance_ThrowsException() {
        when(customerMapper.selectById(1L)).thenReturn(testCustomer);

        Exception exception = assertThrows(RuntimeException.class, () -> customerService.consume(1L, new BigDecimal("200.00"), "Consume test"));
        assertTrue(exception.getMessage().contains("余额不足"));
    }

    @Test
    void addPoints_Success() {
        when(customerMapper.selectById(1L)).thenReturn(testCustomer);
        when(customerMapper.updateById(any(CrmCustomer.class))).thenReturn(1);

        Integer newPoints = customerService.addPoints(1L, 50, "Add points test");

        assertEquals(150, newPoints);
        verify(customerMapper).updateById(testCustomer);
    }

    @Test
    void deductPoints_Success() {
        when(customerMapper.selectById(1L)).thenReturn(testCustomer);
        when(customerMapper.updateById(any(CrmCustomer.class))).thenReturn(1);

        Integer newPoints = customerService.deductPoints(1L, 30, "Deduct points test");

        assertEquals(70, newPoints);
        verify(customerMapper).updateById(testCustomer);
    }

    @Test
    void upgradeMemberLevel_Success() {
        when(customerMapper.selectById(1L)).thenReturn(testCustomer);
        when(customerMapper.updateById(any(CrmCustomer.class))).thenReturn(1);

        boolean result = customerService.upgradeMemberLevel(1L, 2);

        assertTrue(result);
        assertEquals(2, testCustomer.getMemberLevel());
        verify(customerMapper).updateById(testCustomer);
    }
}
