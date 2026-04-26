package com.ruiyu.outdoor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruiyu.outdoor.dto.EquipmentCreateReq;
import com.ruiyu.outdoor.model.Equipment;

import java.util.List;

/**
 * 装备服务接口
 */
public interface EquipmentService extends IService<Equipment> {

    Equipment create(EquipmentCreateReq req, Integer userId);

    List<Equipment> getByUserId(Integer userId);

    void delete(Long id);
}
