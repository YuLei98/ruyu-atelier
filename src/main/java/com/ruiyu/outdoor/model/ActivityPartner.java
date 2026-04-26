package com.ruiyu.outdoor.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 活动伙伴关联实体
 */
@Data
@TableName("activity_partners")
@AllArgsConstructor
@NoArgsConstructor
public class ActivityPartner {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 活动ID */
    private Long activityId;

    /** 伙伴ID */
    private Long partnerId;

    /** 角色: organizer-发起人, member-成员 */
    private String role;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
