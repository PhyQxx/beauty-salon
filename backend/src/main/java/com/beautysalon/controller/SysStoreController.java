package com.beautysalon.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.beautysalon.common.Result;
import com.beautysalon.entity.SysStore;
import com.beautysalon.service.SysStoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 门店管理 Controller
 *
 * @author BeautySalon Team
 */
@Api(tags = "门店管理")
@RestController
@RequestMapping("/system/store")
public class SysStoreController {

    @Autowired
    private SysStoreService sysStoreService;

    /**
     * 分页查询门店列表
     */
    @ApiOperation("分页查询门店列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @ApiParam(value = "页码", required = false) @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页数量", required = false) @RequestParam(defaultValue = "10") Integer limit,
            @ApiParam(value = "关键词搜索", required = false) @RequestParam(required = false) String keyword,
            @ApiParam(value = "状态筛选", required = false) @RequestParam(required = false) Integer status) {

        Page<SysStore> storePage = new Page<>(page, limit);
        LambdaQueryWrapper<SysStore> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(w -> w.like(SysStore::getName, keyword)
                    .or().like(SysStore::getCode, keyword)
                    .or().like(SysStore::getManager, keyword));
        }
        
        if (status != null) {
            queryWrapper.eq(SysStore::getStatus, status);
        }
        
        queryWrapper.orderByDesc(SysStore::getCreateTime);
        
        sysStoreService.page(storePage, queryWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("list", storePage.getRecords());
        result.put("total", storePage.getTotal());
        result.put("page", page);
        result.put("limit", limit);
        
        return Result.success("查询成功", result);
    }

    /**
     * 根据ID获取门店详情
     */
    @ApiOperation("根据ID获取门店详情")
    @GetMapping("/{id}")
    public Result<SysStore> getById(@PathVariable Long id) {
        SysStore store = sysStoreService.getById(id);
        if (store == null) {
            return Result.notFound("门店不存在");
        }
        return Result.success("查询成功", store);
    }

    /**
     * 新增门店
     */
    @ApiOperation("新增门店")
    @PostMapping
    public Result<Long> save(@RequestBody SysStore store) {
        boolean success = sysStoreService.save(store);
        if (success) {
            return Result.success("创建成功", store.getId());
        }
        return Result.error("创建失败");
    }

    /**
     * 更新门店信息
     */
    @ApiOperation("更新门店信息")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysStore store) {
        store.setId(id);
        boolean success = sysStoreService.updateById(store);
        if (success) {
            return Result.success("更新成功", null);
        }
        return Result.error("更新失败");
    }

    /**
     * 删除门店（逻辑删除）
     */
    @ApiOperation("删除门店")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = sysStoreService.removeById(id);
        if (success) {
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败");
    }

    /**
     * 获取所有可用门店简要列表（下拉框用）
     */
    @ApiOperation("获取所有可用门店简要列表")
    @GetMapping("/simple-list")
    public Result<Map<String, Object>> getSimpleList() {
        LambdaQueryWrapper<SysStore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysStore::getStatus, 1);
        queryWrapper.select(SysStore::getId, SysStore::getName);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", sysStoreService.list(queryWrapper));
        return Result.success("查询成功", result);
    }
}
