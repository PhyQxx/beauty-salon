package com.beautysalon.service;

import com.beautysalon.entity.SysPermission;
import com.beautysalon.entity.SysRolePermission;
import com.beautysalon.mapper.SysPermissionMapper;
import com.beautysalon.mapper.SysRolePermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysPermissionService {

    @Autowired
    private SysPermissionMapper permissionMapper;

    @Autowired
    private SysRolePermissionMapper rolePermissionMapper;

    /**
     * 获取所有权限（树形结构）
     */
    public List<SysPermission> getPermissionTree() {
        List<SysPermission> all = permissionMapper.selectAll();
        return buildTree(all);
    }

    /**
     * 根据角色ID获取权限树
     */
    public List<SysPermission> getPermissionsByRoleId(Integer roleId) {
        return permissionMapper.selectByRoleId(roleId);
    }

    /**
     * 根据角色ID获取权限编码列表
     */
    public Set<String> getPermissionCodesByRoleId(Integer roleId) {
        List<String> codes = permissionMapper.selectCodesByRoleId(roleId);
        return new HashSet<>(codes);
    }

    /**
     * 校验角色是否拥有某权限
     */
    public boolean hasPermission(Integer roleId, String permissionCode) {
        Set<String> codes = getPermissionCodesByRoleId(roleId);
        return codes.contains(permissionCode);
    }

    /**
     * 更新角色权限
     */
    @Transactional
    public void updateRolePermissions(Integer roleId, List<Long> permissionIds) {
        // 先删除原有权限
        rolePermissionMapper.deleteByRoleId(roleId);
        // 再插入新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            rolePermissionMapper.insertBatch(roleId, permissionIds);
        }
    }

    /**
     * 新增权限
     */
    public void addPermission(SysPermission permission) {
        permissionMapper.insert(permission);
    }

    /**
     * 更新权限
     */
    public void updatePermission(SysPermission permission) {
        permissionMapper.update(permission);
    }

    /**
     * 删除权限
     */
    public void deletePermission(Long id) {
        permissionMapper.deleteById(id);
    }

    /**
     * 构建权限树
     */
    private List<SysPermission> buildTree(List<SysPermission> permissions) {
        Map<Long, List<SysPermission>> childrenMap = permissions.stream()
                .filter(p -> p.getParentId() != null && p.getParentId() > 0)
                .collect(Collectors.groupingBy(SysPermission::getParentId));

        return permissions.stream()
                .filter(p -> p.getParentId() == null || p.getParentId() == 0)
                .peek(p -> setChildren(p, childrenMap))
                .collect(Collectors.toList());
    }

    private void setChildren(SysPermission parent, Map<Long, List<SysPermission>> childrenMap) {
        List<SysPermission> children = childrenMap.get(parent.getId());
        if (children != null) {
            children.sort(Comparator.comparingInt(SysPermission::getSortOrder));
            children.forEach(c -> setChildren(c, childrenMap));
        }
    }
}
