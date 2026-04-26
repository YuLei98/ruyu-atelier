package com.ruiyu.outdoor.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 活动照片实体
 */
@Data
@TableName("activity_photos")
@AllArgsConstructor
@NoArgsConstructor
public class ActivityPhoto {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 活动ID */
    private Long activityId;

    /** 照片URL */
    private String url;

    /** 照片描述 */
    private String description;

    /** 拍摄时间 */
    private LocalDateTime takenAt;

    /** 拍摄地点纬度 */
    private Double latitude;

    /** 拍摄地点经度 */
    private Double longitude;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
