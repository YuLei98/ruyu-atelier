package com.ruiyu.outdoor.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 装备实体
 */
@Data
@TableName("equipments")
@AllArgsConstructor
@NoArgsConstructor
public class Equipment {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 装备名称 */
    private String name;

    /** 装备类型: backpack-背包, shoes-鞋, clothing-衣物, tools-工具 */
    private String category;

    /** 装备图标 */
    private String icon;

    /** 数量 */
    private Integer quantity;

    /** 备注 */
    private String remark;

    /** 所属用户ID */
    private Integer userId;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
