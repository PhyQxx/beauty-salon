package com.beautysalon.service;

import com.beautysalon.dto.CustomerCreateDTO;
import com.beautysalon.dto.CustomerQueryDTO;
import com.beautysalon.dto.CustomerUpdateDTO;
import com.beautysalon.entity.CrmCustomer;
import com.beautysalon.vo.CustomerVO;
import java.util.Map;

/**
 * 客户管理服务接口
 * 定义客户相关的业务操作
 *
 * @author BeautySalon Team
 */
public interface CustomerService {

    /**
     * 创建客户（会员注册）
     *
     * @param dto 创建客户请求参数
     * @return 客户ID
     */
    Long createCustomer(CustomerCreateDTO dto);

    /**
     * 更新客户信息
     *
     * @param id 客户ID
     * @param dto 更新请求参数
     * @return 是否成功
     */
    boolean updateCustomer(Long id, CustomerUpdateDTO dto);

    /**
     * 删除客户（逻辑删除）
     *
     * @param id 客户ID
     * @return 是否成功
     */
    boolean deleteCustomer(Long id);

    /**
     * 根据ID获取客户详情
     *
     * @param id 客户ID
     * @return 客户详情VO
     */
    CustomerVO getCustomerById(Long id);

    /**
     * 分页查询客户列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Map<String, Object> queryCustomerPage(CustomerQueryDTO queryDTO);

    /**
     * 账户充值
     *
     * @param id 客户ID
     * @param amount 充值金额
     * @param reason 充值原因
     * @return 充值后余额
     */
    java.math.BigDecimal recharge(Long id, java.math.BigDecimal amount, String reason);

    /**
     * 消费扣款
     *
     * @param id 客户ID
     * @param amount 消费金额
     * @param reason 消费原因
     * @return 扣款后余额
     */
    java.math.BigDecimal consume(Long id, java.math.BigDecimal amount, String reason);

    /**
     * 积分增加
     *
     * @param id 客户ID
     * @param points 积分数量
     * @param reason 增加原因
     * @return 调整后积分
     */
    Integer addPoints(Long id, Integer points, String reason);

    /**
     * 积分扣减
     *
     * @param id 客户ID
     * @param points 积分数量
     * @param reason 扣减原因
     * @return 调整后积分
     */
    Integer deductPoints(Long id, Integer points, String reason);

    /**
     * 升级会员等级
     *
     * @param id 客户ID
     * @param targetLevel 目标等级
     * @return 是否成功
     */
    boolean upgradeMemberLevel(Long id, Integer targetLevel);

    /**
     * 检查手机号是否已存在
     *
     * @param phone 手机号码
     * @return 是否存在
     */
    boolean isPhoneExists(String phone);

    /**
     * 获取客户简要列表（下拉框用）
     *
     * @return 客户简要信息列表
     */
    java.util.List<java.util.Map<String, Object>> getSimpleList();
}
