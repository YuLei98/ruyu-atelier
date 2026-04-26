package com.ruiyu.outdoor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建装备请求
 */
@Data
public class EquipmentCreateReq {

    @NotBlank(message = "装备名称不能为空")
    private String name;

    private String category;

    private String icon;

    private Integer quantity;

    private String remark;
}
