package com.ruiyu.outdoor.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * GPS轨迹点实体
 */
@Data
@TableName("track_points")
@AllArgsConstructor
@NoArgsConstructor
public class TrackPoint {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 活动ID */
    private Long activityId;

    /** 纬度 */
    private Double latitude;

    /** 经度 */
    private Double longitude;

    /** 海拔(米) */
    private Double altitude;

    /** 速度(km/h) */
    private Double speed;

    /** 定位时间 */
    private LocalDateTime timestamp;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
