package com.beautysalon.controller;

import com.beautysalon.common.Result;
import com.beautysalon.entity.PosProduct;
import com.beautysalon.service.PosProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 产品信息控制器
 */
@Api(tags = "产品信息管理")
@RestController
@RequestMapping("/pos/product")
public class PosProductController {

    @Resource
    private PosProductService productService;

    @ApiOperation("分页查询产品")
    @GetMapping("/page")
    public Result<Map<String, Object>> queryPage(@RequestParam Map<String, Object> params) {
        return Result.success(productService.queryProductPage(params));
    }

    @ApiOperation("获取产品详情")
    @GetMapping("/{id}")
    public Result<PosProduct> getById(@PathVariable Long id) {
        return Result.success(productService.getById(id));
    }

    @ApiOperation("新增产品")
    @PostMapping
    public Result<Boolean> save(@RequestBody PosProduct product) {
        if (productService.isCodeExists(product.getCode(), null)) {
            return Result.error("产品编码已存在");
        }
        return Result.success(productService.save(product));
    }

    @ApiOperation("修改产品")
    @PutMapping
    public Result<Boolean> update(@RequestBody PosProduct product) {
        if (productService.isCodeExists(product.getCode(), product.getId())) {
            return Result.error("产品编码已存在");
        }
        return Result.success(productService.updateById(product));
    }

    @ApiOperation("删除产品")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(productService.removeById(id));
    }
}
