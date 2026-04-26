package com.ruiyu.outdoor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruiyu.outdoor.dto.EquipmentCreateReq;
import com.ruiyu.outdoor.mapper.EquipmentMapper;
import com.ruiyu.outdoor.model.Equipment;
import com.ruiyu.outdoor.service.EquipmentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 装备服务实现
 */
@Service
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements EquipmentService {

    @Override
    public Equipment create(EquipmentCreateReq req, Integer userId) {
        Equipment equipment = new Equipment();
        equipment.setName(req.getName());
        equipment.setCategory(req.getCategory());
        equipment.setIcon(req.getIcon());
        equipment.setQuantity(req.getQuantity());
        equipment.setRemark(req.getRemark());
        equipment.setUserId(userId);
        equipment.setCreatedAt(LocalDateTime.now());
        equipment.setUpdatedAt(LocalDateTime.now());

        this.save(equipment);
        return equipment;
    }

    @Override
    public List<Equipment> getByUserId(Integer userId) {
        return this.list(new LambdaQueryWrapper<Equipment>()
                .eq(Equipment::getUserId, userId)
                .orderByDesc(Equipment::getCreatedAt));
    }

    @Override
    public void delete(Long id) {
        this.removeById(id);
    }
}
