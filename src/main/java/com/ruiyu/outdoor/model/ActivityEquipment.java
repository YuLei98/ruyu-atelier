package com.ruiyu.outdoor.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 活动装备关联实体
 */
@Data
@TableName("activity_equipments")
@AllArgsConstructor
@NoArgsConstructor
public class ActivityEquipment {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 活动ID */
    private Long activityId;

    /** 装备ID */
    private Long equipmentId;

    /** 是否已打包 */
    private Boolean packed;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
