package com.beautysalon.controller;

import com.beautysalon.common.JwtUtil;
import com.beautysalon.entity.SysUser;
import com.beautysalon.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统用户管理 Controller
 * 负责用户登录、登出、CRUD等操作
 *
 * @author BeautySalon Team
 */
@Slf4j
@Api(tags = "系统用户管理")
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    @Resource
    private SysUserService userService;

    @Resource
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @ApiParam(value = "用户名", required = true) @RequestParam String username,
            @ApiParam(value = "密码", required = true) @RequestParam String password) {
        try {
            Map<String, Object> result = userService.login(username, password);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 401);
            error.put("message", e.getMessage());
            return ResponseEntity.status(401).body(error);
        }
    }

    /**
     * 用户登出
     */
    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            @ApiParam(value = "Token", required = true) @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            userService.logout(token);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "登出成功");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 获取当前登录用户信息
     */
    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @ApiParam(value = "Token", required = true) @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 从token中解析用户信息
            if (token == null || !token.startsWith("Bearer ")) {
                throw new RuntimeException("无效的Token");
            }
            token = token.substring(7);
            if (jwtUtil.isTokenExpired(token)) {
                throw new RuntimeException("Token已过期");
            }
            Long userId = jwtUtil.getUserIdFromToken(token);
            SysUser user = userService.getUserById(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", user);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 401);
            error.put("message", e.getMessage());
            return ResponseEntity.status(401).body(error);
        }
    }

    /**
     * 分页查询用户列表
     */
    @ApiOperation("分页查询用户列表")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(
            @ApiParam(value = "页码", required = false) @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页数量", required = false) @RequestParam(defaultValue = "10") Integer limit,
            @ApiParam(value = "关键词搜索", required = false) @RequestParam(required = false) String keyword,
            @ApiParam(value = "角色筛选", required = false) @RequestParam(required = false) Integer role) {
        Map<String, Object> result = userService.queryUserPage(page, limit, keyword, role);
        result.put("code", 200);
        result.put("message", "查询成功");
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID获取用户详情
     */
    @ApiOperation("根据ID获取用户详情")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id) {
        try {
            SysUser user = userService.getUserById(id);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", user);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 404);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 新增用户
     */
    @ApiOperation("新增用户")
    @PostMapping
    public ResponseEntity<Map<String, Object>> save(@RequestBody SysUser user) {
        try {
            Long userId = userService.createUser(user);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "创建成功");
            result.put("data", userId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 更新用户信息
     */
    @ApiOperation("更新用户信息")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id,
            @RequestBody SysUser user) {
        try {
            boolean success = userService.updateUser(id, user);
            Map<String, Object> result = new HashMap<>();
            result.put("code", success ? 200 : 500);
            result.put("message", success ? "更新成功" : "更新失败");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 删除用户（逻辑删除）
     */
    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id) {
        try {
            boolean success = userService.deleteUser(id);
            Map<String, Object> result = new HashMap<>();
            result.put("code", success ? 200 : 500);
            result.put("message", success ? "删除成功" : "删除失败");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 修改密码
     */
    @ApiOperation("修改密码")
    @PutMapping("/password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long id,
            @ApiParam(value = "旧密码", required = true) @RequestParam String oldPassword,
            @ApiParam(value = "新密码", required = true) @RequestParam String newPassword) {
        try {
            boolean success = userService.changePassword(id, oldPassword, newPassword);
            Map<String, Object> result = new HashMap<>();
            result.put("code", success ? 200 : 500);
            result.put("message", success ? "密码修改成功" : "密码修改失败");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 重置密码
     */
    @ApiOperation("重置密码")
    @PutMapping("/{id}/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id,
            @ApiParam(value = "新密码", required = false, defaultValue = "123456") @RequestParam(required = false, defaultValue = "123456") String newPassword) {
        try {
            boolean success = userService.resetPassword(id, newPassword);
            Map<String, Object> result = new HashMap<>();
            result.put("code", success ? 200 : 500);
            result.put("message", success ? "密码重置成功" : "密码重置失败");
            result.put("data", newPassword);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }
}
