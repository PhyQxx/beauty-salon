package com.beautysalon.controller;

import com.beautysalon.entity.Beautician;
import com.beautysalon.service.BeauticianService;
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
 * 美容师管理 Controller
 *
 * @author BeautySalon Team
 */
@Slf4j
@Api(tags = "美容师管理")
@RestController
@RequestMapping("/beautician")
public class BeauticianController {

    @Resource
    private BeauticianService beauticianService;

    /**
     * 分页查询美容师列表
     */
    @ApiOperation("分页查询美容师列表")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(
            @ApiParam(value = "页码", required = false) @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页数量", required = false) @RequestParam(defaultValue = "10") Integer limit,
            @ApiParam(value = "关键词搜索", required = false) @RequestParam(required = false) String keyword,
            @ApiParam(value = "状态筛选", required = false) @RequestParam(required = false) Integer status) {
        Map<String, Object> result = beauticianService.queryBeauticianPage(page, limit, keyword, status);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID获取美容师详情
     */
    @ApiOperation("根据ID获取美容师详情")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(
            @ApiParam(value = "美容师ID", required = true) @PathVariable Long id) {
        try {
            Beautician beautician = beauticianService.getBeauticianById(id);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", beautician);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 404);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 新增美容师
     */
    @ApiOperation("新增美容师")
    @PostMapping
    public ResponseEntity<Map<String, Object>> save(@RequestBody Beautician beautician) {
        try {
            Long id = beauticianService.createBeautician(beautician);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "创建成功");
            result.put("data", id);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 更新美容师信息
     */
    @ApiOperation("更新美容师信息")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @ApiParam(value = "美容师ID", required = true) @PathVariable Long id,
            @RequestBody Beautician beautician) {
        try {
            boolean success = beauticianService.updateBeautician(id, beautician);
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
     * 删除美容师（逻辑删除）
     */
    @ApiOperation("删除美容师")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(
            @ApiParam(value = "美容师ID", required = true) @PathVariable Long id) {
        try {
            boolean success = beauticianService.deleteBeautician(id);
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
     * 获取在职美容师列表（下拉框用）
     */
    @ApiOperation("获取在职美容师列表")
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveBeauticians() {
        List<Beautician> list = beauticianService.getActiveBeauticians();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", list);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新美容师状态
     */
    @ApiOperation("更新美容师状态")
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @ApiParam(value = "美容师ID", required = true) @PathVariable Long id,
            @ApiParam(value = "状态", required = true) @RequestParam Integer status) {
        try {
            boolean success = beauticianService.updateStatus(id, status);
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
}
