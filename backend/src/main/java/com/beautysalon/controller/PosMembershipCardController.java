package com.beautysalon.controller;

import com.beautysalon.entity.PosMembershipCard;
import com.beautysalon.service.PosMembershipCardService;
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
 * 会员卡/套餐 Controller (POS模块)
 * 负责会员卡、套餐的增删改查等操作
 *
 * @author BeautySalon Team
 */
@Slf4j
@Api(tags = "会员卡/套餐管理")
@RestController
@RequestMapping("/pos/membership-card")
public class PosMembershipCardController {

    @Resource
    private PosMembershipCardService cardService;

    /**
     * 分页查询会员卡/套餐列表
     */
    @ApiOperation("分页查询会员卡/套餐列表")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(
            @ApiParam(value = "页码", required = false) @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页数量", required = false) @RequestParam(defaultValue = "10") Integer limit,
            @ApiParam(value = "关键词搜索", required = false) @RequestParam(required = false) String keyword,
            @ApiParam(value = "卡类型", required = false) @RequestParam(required = false) Integer type,
            @ApiParam(value = "是否上架", required = false) @RequestParam(required = false) Integer isActive) {
        Map<String, Object> result = cardService.queryCardPage(page, limit, keyword, type, isActive);
        result.put("code", 200);
        result.put("message", "查询成功");
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID获取会员卡/套餐详情
     */
    @ApiOperation("根据ID获取会员卡/套餐详情")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(
            @ApiParam(value = "卡ID", required = true) @PathVariable Long id) {
        try {
            PosMembershipCard card = cardService.getCardById(id);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", card);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 404);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 新增会员卡/套餐
     */
    @ApiOperation("新增会员卡/套餐")
    @PostMapping
    public ResponseEntity<Map<String, Object>> save(@RequestBody PosMembershipCard card) {
        try {
            Long cardId = cardService.createCard(card);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "创建成功");
            result.put("data", cardId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 更新会员卡/套餐
     */
    @ApiOperation("更新会员卡/套餐")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @ApiParam(value = "卡ID", required = true) @PathVariable Long id,
            @RequestBody PosMembershipCard card) {
        try {
            boolean success = cardService.updateCard(id, card);
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
     * 删除会员卡/套餐（逻辑删除）
     */
    @ApiOperation("删除会员卡/套餐")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(
            @ApiParam(value = "卡ID", required = true) @PathVariable Long id) {
        try {
            boolean success = cardService.deleteCard(id);
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
     * 上下架会员卡/套餐
     */
    @ApiOperation("上下架会员卡/套餐")
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @ApiParam(value = "卡ID", required = true) @PathVariable Long id,
            @ApiParam(value = "是否上架", required = true) @RequestParam Integer isActive) {
        try {
            boolean success = cardService.updateStatus(id, isActive);
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
     * 查询上架的会员卡/套餐
     */
    @ApiOperation("查询上架的会员卡/套餐")
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveCards() {
        List<PosMembershipCard> cards = cardService.getActiveCards();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", cards);
        return ResponseEntity.ok(result);
    }
}
