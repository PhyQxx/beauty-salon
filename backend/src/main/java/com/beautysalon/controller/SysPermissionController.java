package com.beautysalon.controller;

import com.beautysalon.annotation.OperLog;
import com.beautysalon.entity.SysPermission;
import com.beautysalon.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/permission")
public class SysPermissionController {

    @Autowired
    private SysPermissionService permissionService;

    /**
     * 获取权限树（所有权限）
     */
    @GetMapping("/tree")
    public Map<String, Object> getPermissionTree() {
        List<SysPermission> tree = permissionService.getPermissionTree();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", tree);
        return result;
    }

    /**
     * 获取角色已分配的权限
     */
    @GetMapping("/role/{roleId}")
    public Map<String, Object> getPermissionsByRoleId(@PathVariable Integer roleId) {
        List<SysPermission> list = permissionService.getPermissionsByRoleId(roleId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    /**
     * 更新角色权限
     */
    @OperLog(module = "系统管理", businessType = 4)
    @PutMapping("/role/{roleId}")
    public Map<String, Object> updateRolePermissions(
            @PathVariable Integer roleId,
            @RequestBody Map<String, List<Long>> body) {
        List<Long> permissionIds = body.get("permissionIds");
        permissionService.updateRolePermissions(roleId, permissionIds);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "权限分配成功");
        return result;
    }

    /**
     * 新增权限
     */
    @OperLog(module = "系统管理", businessType = 1)
    @PostMapping
    public Map<String, Object> addPermission(@RequestBody SysPermission permission) {
        permissionService.addPermission(permission);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "新增权限成功");
        return result;
    }

    /**
     * 更新权限
     */
    @OperLog(module = "系统管理", businessType = 2)
    @PutMapping("/{id}")
    public Map<String, Object> updatePermission(@PathVariable Long id, @RequestBody SysPermission permission) {
        permission.setId(id);
        permissionService.updatePermission(permission);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "更新权限成功");
        return result;
    }

    /**
     * 删除权限
     */
    @OperLog(module = "系统管理", businessType = 3)
    @DeleteMapping("/{id}")
    public Map<String, Object> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "删除权限成功");
        return result;
    }
}
