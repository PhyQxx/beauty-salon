package com.beautysalon.controller;

import com.beautysalon.entity.PosService;
import com.beautysalon.service.PosServiceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务项目 Controller (POS模块)
 * 负责服务项目的增删改查、上下架等操作
 *
 * @author BeautySalon Team
 */
@Slf4j
@Api(tags = "服务项目管理")
@RestController
@RequestMapping("/pos/service")
public class PosServiceController {

    @Resource
    private PosServiceService serviceService;

    /**
     * 分页查询服务项目列表
     */
    @ApiOperation("分页查询服务项目列表")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(
            @ApiParam(value = "页码", required = false) @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页数量", required = false) @RequestParam(defaultValue = "10") Integer limit,
            @ApiParam(value = "关键词搜索", required = false) @RequestParam(required = false) String keyword,
            @ApiParam(value = "项目分类", required = false) @RequestParam(required = false) String category,
            @ApiParam(value = "是否上架", required = false) @RequestParam(required = false) Integer isActive) {
        Map<String, Object> result = serviceService.queryServicePage(page, limit, keyword, category, isActive);
        result.put("code", 200);
        result.put("message", "查询成功");
        return ResponseEntity.ok(result);
    }

    /**
     * 获取所有服务分类
     */
    @ApiOperation("获取所有服务分类")
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getCategories() {
        List<String> categories = serviceService.getAllCategories();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", categories);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID获取服务项目详情
     */
    @ApiOperation("根据ID获取服务项目详情")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(
            @ApiParam(value = "项目ID", required = true) @PathVariable Long id) {
        try {
            PosService service = serviceService.getServiceById(id);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", service);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 404);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 新增服务项目
     */
    @ApiOperation("新增服务项目")
    @PostMapping
    public ResponseEntity<Map<String, Object>> save(@RequestBody PosService service) {
        try {
            Long serviceId = serviceService.createService(service);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "创建成功");
            result.put("data", serviceId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 更新服务项目
     */
    @ApiOperation("更新服务项目")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @ApiParam(value = "项目ID", required = true) @PathVariable Long id,
            @RequestBody PosService service) {
        try {
            boolean success = serviceService.updateService(id, service);
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
     * 删除服务项目（逻辑删除）
     */
    @ApiOperation("删除服务项目")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(
            @ApiParam(value = "项目ID", required = true) @PathVariable Long id) {
        try {
            boolean success = serviceService.deleteService(id);
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
     * 上下架服务项目
     */
    @ApiOperation("上下架服务项目")
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @ApiParam(value = "项目ID", required = true) @PathVariable Long id,
            @ApiParam(value = "是否上架", required = true) @RequestParam Integer isActive) {
        try {
            boolean success = serviceService.updateStatus(id, isActive);
            Map<String, Object> result = new HashMap<>();
            result.put("code", success ? 200 : 500);
            result.put("message", success ? "状态更新成功" : "状态更新失败");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 查询上架的服务项目（下拉框用）
     */
    @ApiOperation("查询上架的服务项目")
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveServices() {
        List<PosService> services = serviceService.getActiveServices();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", services);
        return ResponseEntity.ok(result);
    }
}
