package com.beautysalon.controller;

import com.beautysalon.common.JwtUtil;
import com.beautysalon.config.JwtAuthenticationFilter;
import com.beautysalon.entity.SysUser;
import com.beautysalon.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统用户管理 Controller
 * 负责用户登录、登出、CRUD等操作
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

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return badRequest("用户名和密码不能为空");
        }
        try {
            Map<String, Object> result = userService.login(username, password);
            result.put("code", 200);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return unauthorized(e.getMessage());
        }
    }

    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            log.info("用户登出: {}", auth.getName());
        }
        SecurityContextHolder.clearContext();
        return ok("登出成功");
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return unauthorized("未登录或Token无效");
        }
        JwtAuthenticationFilter.JwtAuthDetails details =
                (JwtAuthenticationFilter.JwtAuthDetails) auth.getDetails();
        if (details == null) {
            return unauthorized("认证信息异常");
        }
        Long userId = toLong(details.getUserId());
        if (userId == null) {
            return unauthorized("无法获取用户ID");
        }
        SysUser user = userService.getUserById(userId);
        if (user == null) {
            return notFound("用户不存在");
        }
        // 脱敏：移除密码字段
        user.setPassword(null);
        return ok(user, "获取成功");
    }

    @ApiOperation("分页查询用户列表")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer role) {
        Map<String, Object> result = userService.queryUserPage(page, limit, keyword, role);
        result.put("code", 200);
        result.put("message", "查询成功");
        return ResponseEntity.ok(result);
    }

    @ApiOperation("根据ID获取用户详情")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        SysUser user = userService.getUserById(id);
        if (user == null) {
            return notFound("用户不存在");
        }
        user.setPassword(null);
        return ok(user, "获取成功");
    }

    @ApiOperation("新增用户")
    @PostMapping
    public ResponseEntity<Map<String, Object>> save(@RequestBody SysUser user) {
        try {
            Long userId = userService.createUser(user);
            Map<String, Object> data = new HashMap<>();
            data.put("id", userId);
            return ok(data, "创建成功");
        } catch (RuntimeException e) {
            return badRequest(e.getMessage());
        }
    }

    @ApiOperation("更新用户信息")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody SysUser user) {
        boolean success = userService.updateUser(id, user);
        if (!success) {
            return notFound("用户不存在");
        }
        return ok("更新成功");
    }

    @ApiOperation("删除用户（逻辑删除）")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        boolean success = userService.deleteUser(id);
        if (!success) {
            return notFound("用户不存在");
        }
        return ok("删除成功");
    }

    @ApiOperation("修改密码")
    @PutMapping("/password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestParam Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            return badRequest("新密码长度不能少于6位");
        }
        try {
            userService.changePassword(id, oldPassword, newPassword);
            return ok("密码修改成功");
        } catch (RuntimeException e) {
            return badRequest(e.getMessage());
        }
    }

    @ApiOperation("重置密码")
    @PutMapping("/{id}/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "123456") String newPassword) {
        try {
            userService.resetPassword(id, newPassword);
            Map<String, Object> data = new HashMap<>();
            data.put("password", newPassword);
            return ok(data, "密码重置成功");
        } catch (RuntimeException e) {
            return badRequest(e.getMessage());
        }
    }

    // ========== 私有辅助方法 ==========

    private ResponseEntity<Map<String, Object>> ok(Object data, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", 200);
        body.put("message", message);
        body.put("data", data);
        return ResponseEntity.ok(body);
    }

    private ResponseEntity<Map<String, Object>> ok(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", 200);
        body.put("message", message);
        return ResponseEntity.ok(body);
    }

    private ResponseEntity<Map<String, Object>> badRequest(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", 400);
        body.put("message", message);
        return ResponseEntity.status(400).body(body);
    }

    private ResponseEntity<Map<String, Object>> unauthorized(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", 401);
        body.put("message", message);
        return ResponseEntity.status(401).body(body);
    }

    private ResponseEntity<Map<String, Object>> notFound(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", 404);
        body.put("message", message);
        return ResponseEntity.status(404).body(body);
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
