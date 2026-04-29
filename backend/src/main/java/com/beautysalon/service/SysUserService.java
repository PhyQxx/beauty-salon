package com.beautysalon.service;

import com.beautysalon.entity.SysUser;
import java.util.Map;

/**
 * 系统用户服务接口
 * 定义用户相关的业务操作
 *
 * @author BeautySalon Team
 */
public interface SysUserService {

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果（包含token）
     */
    Map<String, Object> login(String username, String password);

    /**
     * 用户登出
     *
     * @param token 登录token
     * @return 是否成功
     */
    boolean logout(String token);

    /**
     * 根据ID获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    SysUser getUserById(Long id);

    /**
     * 分页查询用户列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @param keyword 关键词搜索
     * @param role 角色筛选
     * @return 分页结果
     */
    Map<String, Object> queryUserPage(Integer page, Integer limit, String keyword, Integer role);

    /**
     * 新增用户
     *
     * @param user 用户信息
     * @return 用户ID
     */
    Long createUser(SysUser user);

    /**
     * 更新用户信息
     *
     * @param id 用户ID
     * @param user 用户信息
     * @return 是否成功
     */
    boolean updateUser(Long id, SysUser user);

    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户ID
     * @return 是否成功
     */
    boolean deleteUser(Long id);

    /**
     * 修改密码
     *
     * @param id 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean changePassword(Long id, String oldPassword, String newPassword);

    /**
     * 重置密码
     *
     * @param id 用户ID
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean resetPassword(Long id, String newPassword);
}
