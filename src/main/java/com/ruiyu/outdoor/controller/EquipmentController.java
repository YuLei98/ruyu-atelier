package com.ruiyu.outdoor.controller;

import com.ruiyu.outdoor.dto.EquipmentCreateReq;
import com.ruiyu.outdoor.model.Equipment;
import com.ruiyu.outdoor.service.EquipmentService;
import icu.ruiyu.framework.common.CommonResult;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 装备 Controller
 */
@RestController
@RequestMapping("/api/equipments")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    /**
     * 创建装备
     */
    @PostMapping
    public CommonResult<Equipment> create(@Valid @RequestBody EquipmentCreateReq req,
                                          @RequestHeader(value = "X-User-Id", defaultValue = "1") Integer userId) {
        Equipment equipment = equipmentService.create(req, userId);
        return CommonResult.success(equipment);
    }

    /**
     * 获取用户的所有装备
     */
    @GetMapping
    public CommonResult<List<Equipment>> list(@RequestHeader(value = "X-User-Id", defaultValue = "1") Integer userId) {
        List<Equipment> equipments = equipmentService.getByUserId(userId);
        return CommonResult.success(equipments);
    }

    /**
     * 删除装备
     */
    @DeleteMapping("/{id}")
    public CommonResult<Void> delete(@PathVariable Long id) {
        equipmentService.delete(id);
        return CommonResult.successMessage("删除成功");
    }
}
