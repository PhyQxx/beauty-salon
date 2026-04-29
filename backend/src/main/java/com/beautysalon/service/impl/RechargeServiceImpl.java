package com.beautysalon.service.impl;

import com.beautysalon.dto.RechargeDTO;
import com.beautysalon.entity.PosRecharge;
import com.beautysalon.mapper.PosRechargeMapper;
import com.beautysalon.service.RechargeService;
import com.beautysalon.vo.RechargeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 充值服务实现类
 * 实现充值、退款、满送规则、日周月报表等功能
 *
 * @author BeautySalon Team
 */
@Service
public class RechargeServiceImpl implements RechargeService {

    @Autowired
    private PosRechargeMapper rechargeMapper;

    /**
     * 满送规则配置
     * Key: 充值金额下限
     * Value: 赠送金额
     * 规则：充200送20，充500送60，充1000送150，充2000送350，充5000送1000
     */
    private static final Map<BigDecimal, BigDecimal> GIFT_RULES = new LinkedHashMap<>();

    static {
        GIFT_RULES.put(new BigDecimal("5000"), new BigDecimal("1000"));
        GIFT_RULES.put(new BigDecimal("2000"), new BigDecimal("350"));
        GIFT_RULES.put(new BigDecimal("1000"), new BigDecimal("150"));
        GIFT_RULES.put(new BigDecimal("500"), new BigDecimal("60"));
        GIFT_RULES.put(new BigDecimal("200"), new BigDecimal("20"));
    }

    /**
     * 余额充值
     * 1. 验证充值数据
     * 2. 计算赠送金额（满送规则）
     * 3. 获取客户当前余额
     * 4. 计算充值后余额
     * 5. 创建充值记录
     * 6. 更新客户余额
     */
    @Override
    @Transactional
    public Map<String, Object> recharge(RechargeDTO dto) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 验证数据
        Map<String, Object> validateResult = validateRecharge(dto);
        if (!(boolean) validateResult.get("valid")) {
            result.put("success", false);
            result.put("message", validateResult.get("message"));
            return result;
        }
        
        // 2. 计算赠送金额
        BigDecimal giftAmount = getGiftAmount(dto.getAmount());
        
        // 3. 获取当前余额
        BigDecimal currentBalance = getCustomerBalance(dto.getCustomerId());
        if (currentBalance == null) {
            currentBalance = BigDecimal.ZERO;
        }
        
        // 4. 计算充值后余额
        BigDecimal balanceAfter = currentBalance.add(dto.getAmount()).add(giftAmount);
        
        // 5. 创建充值记录
        PosRecharge recharge = new PosRecharge();
        recharge.setRechargeNo(generateRechargeNo());
        recharge.setCustomerId(dto.getCustomerId());
        recharge.setAmount(dto.getAmount());
        recharge.setGiftAmount(giftAmount);
        recharge.setBalanceAfter(balanceAfter);
        recharge.setPayType(dto.getPayType());
        recharge.setRechargeType(1); // 1-充值
        recharge.setOperatorId(dto.getOperatorId());
        recharge.setRemark(dto.getRemark());
        recharge.setCreateTime(LocalDateTime.now());
        recharge.setUpdateTime(LocalDateTime.now());
        recharge.setDeleted(0);
        
        rechargeMapper.insert(recharge);
        
        // 6. 返回结果
        result.put("success", true);
        result.put("message", "充值成功");
        result.put("rechargeId", recharge.getId());
        result.put("rechargeNo", recharge.getRechargeNo());
        result.put("amount", dto.getAmount());
        result.put("giftAmount", giftAmount);
        result.put("balanceBefore", currentBalance);
        result.put("balanceAfter", balanceAfter);
        
        return result;
    }

    /**
     * 获取满送规则赠送金额
     * 按充值金额匹配规则，返回对应的赠送金额
     * 规则优先级：充值金额 >= 规则金额时适用该规则
     */
    @Override
    public BigDecimal getGiftAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        for (Map.Entry<BigDecimal, BigDecimal> entry : GIFT_RULES.entrySet()) {
            if (amount.compareTo(entry.getKey()) >= 0) {
                return entry.getValue();
            }
        }
        
        return BigDecimal.ZERO; // 不满足任何规则，无赠送
    }

    /**
     * 分页查询充值记录
     */
    @Override
    public List<RechargeVO> listPage(int page, int limit, Long customerId, String startDate, String endDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", (page - 1) * limit);
        params.put("limit", limit);
        
        if (customerId != null) {
            params.put("customerId", customerId);
        }
        if (startDate != null && !startDate.isEmpty()) {
            params.put("startDate", startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            params.put("endDate", endDate);
        }
        
        List<PosRecharge> list = rechargeMapper.selectPage(params);
        return convertToVO(list);
    }

    /**
     * 根据ID获取充值记录详情
     */
    @Override
    public RechargeVO getById(Long id) {
        PosRecharge recharge = rechargeMapper.selectById(id);
        if (recharge == null) {
            return null;
        }
        return convertToVO(recharge);
    }

    /**
     * 根据充值订单号获取充值记录
     */
    @Override
    public RechargeVO getByRechargeNo(String rechargeNo) {
        PosRecharge recharge = rechargeMapper.selectByRechargeNo(rechargeNo);
        if (recharge == null) {
            return null;
        }
        return convertToVO(recharge);
    }

    /**
     * 获取客户当前余额
     * 获取该客户最后一次充值后的余额
     */
    @Override
    public BigDecimal getCustomerBalance(Long customerId) {
        PosRecharge lastRecharge = rechargeMapper.selectLastByCustomerId(customerId);
        if (lastRecharge == null) {
            return BigDecimal.ZERO;
        }
        return lastRecharge.getBalanceAfter();
    }

    /**
     * 获取客户充值记录列表
     */
    @Override
    public List<RechargeVO> getByCustomerId(Long customerId) {
        List<PosRecharge> list = rechargeMapper.selectByCustomerId(customerId);
        return convertToVO(list);
    }

    /**
     * 充值退款
     * 仅退还余额部分（充值金额+赠送金额），原路退回或退至余额
     */
    @Override
    @Transactional
    public Map<String, Object> refund(Long orderId, Long customerId, BigDecimal refundAmount, 
                                       String reason, Long operatorId) {
        Map<String, Object> result = new HashMap<>();
        
        if (refundAmount == null || refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            result.put("success", false);
            result.put("message", "退款金额必须大于0");
            return result;
        }
        
        // 获取当前余额
        BigDecimal currentBalance = getCustomerBalance(customerId);
        if (currentBalance.compareTo(refundAmount) < 0) {
            result.put("success", false);
            result.put("message", "余额不足，无法退款");
            return result;
        }
        
        // 计算退款后余额
        BigDecimal balanceAfter = currentBalance.subtract(refundAmount);
        
        // 创建退款记录
        PosRecharge recharge = new PosRecharge();
        recharge.setRechargeNo(generateRechargeNo());
        recharge.setCustomerId(customerId);
        recharge.setAmount(refundAmount.negate()); // 负数表示减少
        recharge.setGiftAmount(BigDecimal.ZERO);
        recharge.setBalanceAfter(balanceAfter);
        recharge.setPayType(5); // 退至会员卡
        recharge.setRechargeType(3); // 3-退款返还
        recharge.setRefundOrderId(orderId);
        recharge.setOperatorId(operatorId);
        recharge.setRemark("退款：" + (reason != null ? reason : ""));
        recharge.setCreateTime(LocalDateTime.now());
        recharge.setUpdateTime(LocalDateTime.now());
        recharge.setDeleted(0);
        
        rechargeMapper.insert(recharge);
        
        result.put("success", true);
        result.put("message", "退款成功");
        result.put("refundAmount", refundAmount);
        result.put("balanceAfter", balanceAfter);
        
        return result;
    }

    /**
     * 日充值报表
     */
    @Override
    public Map<String, Object> getDailyReport(String date) {
        Map<String, Object> result = new HashMap<>();
        
        LocalDateTime startTime = LocalDateTime.parse(date + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse(date + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        BigDecimal totalAmount = rechargeMapper.sumAmountByDateRange(startTime, endTime);
        BigDecimal totalGiftAmount = rechargeMapper.sumGiftAmountByDateRange(startTime, endTime);
        int count = rechargeMapper.countByDateRange(startTime, endTime);
        
        result.put("date", date);
        result.put("totalAmount", totalAmount != null ? totalAmount : BigDecimal.ZERO);
        result.put("totalGiftAmount", totalGiftAmount != null ? totalGiftAmount : BigDecimal.ZERO);
        result.put("rechargeCount", count);
        result.put("actualAmount", totalAmount != null ? totalAmount.subtract(totalGiftAmount) : BigDecimal.ZERO);
        
        return result;
    }

    /**
     * 周充值报表
     */
    @Override
    public Map<String, Object> getWeeklyReport(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        LocalDateTime startTime = LocalDateTime.parse(startDate + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse(endDate + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        BigDecimal totalAmount = rechargeMapper.sumAmountByDateRange(startTime, endTime);
        BigDecimal totalGiftAmount = rechargeMapper.sumGiftAmountByDateRange(startTime, endTime);
        int count = rechargeMapper.countByDateRange(startTime, endTime);
        
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("totalAmount", totalAmount != null ? totalAmount : BigDecimal.ZERO);
        result.put("totalGiftAmount", totalGiftAmount != null ? totalGiftAmount : BigDecimal.ZERO);
        result.put("rechargeCount", count);
        result.put("actualAmount", totalAmount != null ? totalAmount.subtract(totalGiftAmount) : BigDecimal.ZERO);
        
        return result;
    }

    /**
     * 月充值报表
     */
    @Override
    public Map<String, Object> getMonthlyReport(String yearMonth) {
        String[] parts = yearMonth.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        
        LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime endTime = startTime.plusMonths(1).minusSeconds(1);
        
        BigDecimal totalAmount = rechargeMapper.sumAmountByDateRange(startTime, endTime);
        BigDecimal totalGiftAmount = rechargeMapper.sumGiftAmountByDateRange(startTime, endTime);
        int count = rechargeMapper.countByDateRange(startTime, endTime);
        
        Map<String, Object> result = new HashMap<>();
        result.put("yearMonth", yearMonth);
        result.put("totalAmount", totalAmount != null ? totalAmount : BigDecimal.ZERO);
        result.put("totalGiftAmount", totalGiftAmount != null ? totalGiftAmount : BigDecimal.ZERO);
        result.put("rechargeCount", count);
        result.put("actualAmount", totalAmount != null ? totalAmount.subtract(totalGiftAmount) : BigDecimal.ZERO);
        
        return result;
    }

    /**
     * 验证充值数据有效性
     */
    @Override
    public Map<String, Object> validateRecharge(RechargeDTO dto) {
        Map<String, Object> result = new HashMap<>();
        result.put("valid", true);
        
        if (dto.getCustomerId() == null) {
            result.put("valid", false);
            result.put("message", "客户ID不能为空");
            return result;
        }
        
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            result.put("valid", false);
            result.put("message", "充值金额必须大于0");
            return result;
        }
        
        if (dto.getAmount().compareTo(new BigDecimal("50000")) > 0) {
            result.put("valid", false);
            result.put("message", "单次充值金额不能超过50000");
            return result;
        }
        
        if (dto.getPayType() == null) {
            result.put("valid", false);
            result.put("message", "支付方式不能为空");
            return result;
        }
        
        return result;
    }

    /**
     * 生成充值订单号
     * 格式：CZ + yyyyMMddHHmmss + 4位随机数
     */
    private String generateRechargeNo() {
        return "CZ" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", new Random().nextInt(10000));
    }

    /**
     * 将实体转换为VO
     */
    private RechargeVO convertToVO(PosRecharge recharge) {
        RechargeVO vo = new RechargeVO();
        vo.setId(recharge.getId());
        vo.setRechargeNo(recharge.getRechargeNo());
        vo.setCustomerId(recharge.getCustomerId());
        vo.setAmount(recharge.getAmount());
        vo.setGiftAmount(recharge.getGiftAmount());
        vo.setBalanceAfter(recharge.getBalanceAfter());
        vo.setPayTypeName(getPayTypeName(recharge.getPayType()));
        vo.setRechargeTypeName(getRechargeTypeName(recharge.getRechargeType()));
        vo.setOperatorName(recharge.getOperatorName());
        vo.setRemark(recharge.getRemark());
        vo.setCreateTime(recharge.getCreateTime() != null ? 
                recharge.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
        return vo;
    }

    /**
     * 将实体列表转换为VO列表
     */
    private List<RechargeVO> convertToVO(List<PosRecharge> list) {
        List<RechargeVO> voList = new ArrayList<>();
        for (PosRecharge recharge : list) {
            voList.add(convertToVO(recharge));
        }
        return voList;
    }

    /**
     * 获取支付方式名称
     */
    private String getPayTypeName(Integer payType) {
        if (payType == null) return "";
        switch (payType) {
            case 1: return "现金";
            case 2: return "银行卡";
            case 3: return "微信";
            case 4: return "支付宝";
            case 5: return "会员卡";
            default: return "其他";
        }
    }

    /**
     * 获取充值类型名称
     */
    private String getRechargeTypeName(Integer rechargeType) {
        if (rechargeType == null) return "";
        switch (rechargeType) {
            case 1: return "充值";
            case 2: return "赠送";
            case 3: return "退款返还";
            default: return "其他";
        }
    }
}
