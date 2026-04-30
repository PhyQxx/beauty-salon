package com.beautysalon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beautysalon.dto.CustomerCreateDTO;
import com.beautysalon.dto.CustomerQueryDTO;
import com.beautysalon.dto.CustomerUpdateDTO;
import com.beautysalon.entity.CrmCustomer;
import com.beautysalon.mapper.CrmCustomerMapper;
import com.beautysalon.service.CustomerService;
import com.beautysalon.vo.CustomerVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户管理服务实现类
 * 实现客户相关的所有业务逻辑
 *
 * @author BeautySalon Team
 */
@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    @Resource
    private CrmCustomerMapper customerMapper;

    /**
     * 会员等级映射
     */
    private static final Map<Integer, String> MEMBER_LEVEL_MAP = new HashMap<>();
    private static final Map<Integer, String> GENDER_MAP = new HashMap<>();
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();

    static {
        MEMBER_LEVEL_MAP.put(1, "普通会员");
        MEMBER_LEVEL_MAP.put(2, "银卡会员");
        MEMBER_LEVEL_MAP.put(3, "金卡会员");
        MEMBER_LEVEL_MAP.put(4, "钻石会员");

        GENDER_MAP.put(0, "未知");
        GENDER_MAP.put(1, "男");
        GENDER_MAP.put(2, "女");

        STATUS_MAP.put(0, "无效");
        STATUS_MAP.put(1, "有效");
    }

    /**
     * 创建客户（会员注册）
     *
     * @param dto 创建客户请求参数
     * @return 客户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCustomer(CustomerCreateDTO dto) {
        // 检查手机号是否已存在
        if (isPhoneExists(dto.getPhone())) {
            throw new RuntimeException("手机号已存在，无法重复注册");
        }

        CrmCustomer customer = new CrmCustomer();
        BeanUtils.copyProperties(dto, customer);

        // 设置默认值
        if (customer.getMemberLevel() == null) {
            customer.setMemberLevel(1); // 默认普通会员
        }
        if (customer.getPoints() == null) {
            customer.setPoints(0);
        }
        if (customer.getBalance() == null) {
            customer.setBalance(BigDecimal.ZERO);
        }
        if (customer.getStatus() == null) {
            customer.setStatus(1); // 默认有效
        }
        if (customer.getGender() == null) {
            customer.setGender(0);
        }

        customer.setCreateTime(LocalDateTime.now());
        customer.setUpdateTime(LocalDateTime.now());

        customerMapper.insert(customer);
        log.info("创建客户成功: id={}, name={}, phone={}", customer.getId(), customer.getName(), customer.getPhone());

        return customer.getId();
    }

    /**
     * 更新客户信息
     *
     * @param id 客户ID
     * @param dto 更新请求参数
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCustomer(Long id, CustomerUpdateDTO dto) {
        CrmCustomer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }

        // 如果更新手机号，检查是否与其他客户重复
        if (StringUtils.hasText(dto.getPhone()) && !dto.getPhone().equals(customer.getPhone())) {
            if (isPhoneExists(dto.getPhone())) {
                throw new RuntimeException("手机号已被其他客户使用");
            }
            customer.setPhone(dto.getPhone());
        }

        // 选择性更新非空字段
        if (StringUtils.hasText(dto.getName())) {
            customer.setName(dto.getName());
        }
        if (dto.getGender() != null) {
            customer.setGender(dto.getGender());
        }
        if (dto.getBirthday() != null) {
            customer.setBirthday(dto.getBirthday());
        }
        if (dto.getMemberLevel() != null) {
            customer.setMemberLevel(dto.getMemberLevel());
        }
        if (dto.getAvatar() != null) {
            customer.setAvatar(dto.getAvatar());
        }
        if (dto.getRemark() != null) {
            customer.setRemark(dto.getRemark());
        }
        if (dto.getStatus() != null) {
            customer.setStatus(dto.getStatus());
        }

        customer.setUpdateTime(LocalDateTime.now());
        int rows = customerMapper.updateById(customer);

        log.info("更新客户信息: id={}, rows={}", id, rows);
        return rows > 0;
    }

    /**
     * 删除客户（逻辑删除）
     *
     * @param id 客户ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCustomer(Long id) {
        CrmCustomer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }

        // 逻辑删除
        customer.setDeleted(1);
        customer.setUpdateTime(LocalDateTime.now());
        int rows = customerMapper.updateById(customer);

        log.info("删除客户: id={}, rows={}", id, rows);
        return rows > 0;
    }

    /**
     * 根据ID获取客户详情
     *
     * @param id 客户ID
     * @return 客户详情VO
     */
    @Override
    public CustomerVO getCustomerById(Long id) {
        CrmCustomer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }
        return convertToVO(customer);
    }

    /**
     * 分页查询客户列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @Override
    public Map<String, Object> queryCustomerPage(CustomerQueryDTO queryDTO) {
        LambdaQueryWrapper<CrmCustomer> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（姓名/手机号）
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w.like(CrmCustomer::getName, queryDTO.getKeyword())
                    .or()
                    .like(CrmCustomer::getPhone, queryDTO.getKeyword()));
        }

        // 状态筛选
        if (queryDTO.getStatus() != null) {
            wrapper.eq(CrmCustomer::getStatus, queryDTO.getStatus());
        }

        // 会员等级筛选
        if (queryDTO.getMemberLevel() != null) {
            wrapper.eq(CrmCustomer::getMemberLevel, queryDTO.getMemberLevel());
        }

        // 日期范围筛选
        if (StringUtils.hasText(queryDTO.getStartDate())) {
            wrapper.ge(CrmCustomer::getCreateTime, queryDTO.getStartDate());
        }
        if (StringUtils.hasText(queryDTO.getEndDate())) {
            wrapper.le(CrmCustomer::getCreateTime, queryDTO.getEndDate() + " 23:59:59");
        }

        // 排序
        String orderField = StringUtils.hasText(queryDTO.getOrderField()) ? queryDTO.getOrderField() : "create_time";
        boolean isAsc = "asc".equalsIgnoreCase(queryDTO.getOrderDirection());

        if ("name".equals(orderField)) {
            wrapper.orderBy(true, isAsc, CrmCustomer::getName);
        } else if ("points".equals(orderField)) {
            wrapper.orderBy(true, isAsc, CrmCustomer::getPoints);
        } else if ("balance".equals(orderField)) {
            wrapper.orderBy(true, isAsc, CrmCustomer::getBalance);
        } else if ("member_level".equals(orderField)) {
            wrapper.orderBy(true, isAsc, CrmCustomer::getMemberLevel);
        } else {
            wrapper.orderBy(true, isAsc, CrmCustomer::getCreateTime);
        }

        // 分页查询
        Page<CrmCustomer> page = new Page<>(queryDTO.getPage(), queryDTO.getLimit());
        IPage<CrmCustomer> pageResult = customerMapper.selectPage(page, wrapper);

        // 转换结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", pageResult.getRecords().stream().map(this::convertToVO).toArray());
        result.put("total", pageResult.getTotal());
        result.put("page", pageResult.getCurrent());
        result.put("limit", pageResult.getSize());
        result.put("pages", pageResult.getPages());

        return result;
    }

    /**
     * 账户充值
     *
     * @param id 客户ID
     * @param amount 充值金额
     * @param reason 充值原因
     * @return 充值后余额
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal recharge(Long id, BigDecimal amount, String reason) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("充值金额必须大于0");
        }

        CrmCustomer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }

        // 增加余额
        BigDecimal newBalance = customer.getBalance().add(amount);
        customer.setBalance(newBalance);
        customer.setUpdateTime(LocalDateTime.now());
        customerMapper.updateById(customer);

        // 记录充值流水日志
        log.info("[充值流水] 客户ID: {}, 充值金额: {}, 充值后余额: {}, 充值原因: {}", id, amount, newBalance, reason);

        log.info("客户充值成功: id={}, amount={}, reason={}, newBalance={}", id, amount, reason, newBalance);
        return newBalance;
    }

    /**
     * 消费扣款
     *
     * @param id 客户ID
     * @param amount 消费金额
     * @param reason 消费原因
     * @return 扣款后余额
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal consume(Long id, BigDecimal amount, String reason) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("消费金额必须大于0");
        }

        CrmCustomer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }

        // 检查余额是否充足
        if (customer.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足，当前余额: " + customer.getBalance());
        }

        // 扣减余额
        BigDecimal newBalance = customer.getBalance().subtract(amount);
        customer.setBalance(newBalance);
        customer.setUpdateTime(LocalDateTime.now());
        customerMapper.updateById(customer);

        // 记录消费流水日志
        log.info("[消费流水] 客户ID: {}, 消费金额: {}, 消费后余额: {}, 消费原因: {}", id, amount, newBalance, reason);

        log.info("客户消费成功: id={}, amount={}, reason={}, newBalance={}", id, amount, reason, newBalance);
        return newBalance;
    }

    /**
     * 积分增加
     *
     * @param id 客户ID
     * @param points 积分数量
     * @param reason 增加原因
     * @return 调整后积分
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addPoints(Long id, Integer points, String reason) {
        if (points == null || points <= 0) {
            throw new RuntimeException("积分数量必须大于0");
        }

        CrmCustomer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }

        // 增加积分
        int newPoints = customer.getPoints() + points;
        customer.setPoints(newPoints);
        customer.setUpdateTime(LocalDateTime.now());
        customerMapper.updateById(customer);

        // 记录积分变动日志
        log.info("[积分变动] 客户ID: {}, 增加积分: {}, 变动后积分: {}, 变动原因: {}", id, points, newPoints, reason);

        log.info("客户积分增加: id={}, points={}, reason={}, newPoints={}", id, points, reason, newPoints);
        return newPoints;
    }

    /**
     * 积分扣减
     *
     * @param id 客户ID
     * @param points 积分数量
     * @param reason 扣减原因
     * @return 调整后积分
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deductPoints(Long id, Integer points, String reason) {
        if (points == null || points <= 0) {
            throw new RuntimeException("积分数量必须大于0");
        }

        CrmCustomer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }

        // 检查积分是否充足
        if (customer.getPoints() < points) {
            throw new RuntimeException("积分不足，当前积分: " + customer.getPoints());
        }

        // 扣减积分
        int newPoints = customer.getPoints() - points;
        customer.setPoints(newPoints);
        customer.setUpdateTime(LocalDateTime.now());
        customerMapper.updateById(customer);

        // 记录积分变动日志
        log.info("[积分变动] 客户ID: {}, 扣减积分: {}, 变动后积分: {}, 变动原因: {}", id, points, newPoints, reason);

        log.info("客户积分扣减: id={}, points={}, reason={}, newPoints={}", id, points, reason, newPoints);
        return newPoints;
    }

    /**
     * 升级会员等级
     *
     * @param id 客户ID
     * @param targetLevel 目标等级
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean upgradeMemberLevel(Long id, Integer targetLevel) {
        // 验证目标等级
        if (!MEMBER_LEVEL_MAP.containsKey(targetLevel)) {
            throw new RuntimeException("无效的会员等级: " + targetLevel);
        }

        CrmCustomer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }

        // 检查是否降级
        if (customer.getMemberLevel() != null && targetLevel < customer.getMemberLevel()) {
            throw new RuntimeException("不允许降低会员等级");
        }

        // 等级不能相同
        if (customer.getMemberLevel() != null && targetLevel.equals(customer.getMemberLevel())) {
            throw new RuntimeException("当前已是该会员等级");
        }

        Integer oldLevel = customer.getMemberLevel();
        customer.setMemberLevel(targetLevel);
        customer.setUpdateTime(LocalDateTime.now());
        int rows = customerMapper.updateById(customer);

        log.info("客户会员等级升级: id={}, oldLevel={}, newLevel={}", id, oldLevel, targetLevel);
        return rows > 0;
    }

    /**
     * 检查手机号是否已存在
     *
     * @param phone 手机号码
     * @return 是否存在
     */
    @Override
    public boolean isPhoneExists(String phone) {
        if (!StringUtils.hasText(phone)) {
            return false;
        }
        LambdaQueryWrapper<CrmCustomer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CrmCustomer::getPhone, phone);
        return customerMapper.selectCount(wrapper) > 0;
    }

    /**
     * 将实体对象转换为VO
     *
     * @param customer 客户实体
     * @return 客户VO
     */
    private CustomerVO convertToVO(CrmCustomer customer) {
        CustomerVO vo = new CustomerVO();
        BeanUtils.copyProperties(customer, vo);

        // 转换文本字段
        vo.setGenderText(GENDER_MAP.getOrDefault(customer.getGender(), "未知"));
        vo.setMemberLevelText(MEMBER_LEVEL_MAP.getOrDefault(customer.getMemberLevel(), "普通会员"));
        vo.setStatusText(STATUS_MAP.getOrDefault(customer.getStatus(), "未知"));

        // 计算年龄
        if (customer.getBirthday() != null) {
            int age = Period.between(customer.getBirthday(), LocalDate.now()).getYears();
            vo.setAge(age);
        }

        return vo;
    }
}
