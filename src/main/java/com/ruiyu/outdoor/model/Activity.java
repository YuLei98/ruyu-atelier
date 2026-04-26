package com.ruiyu.outdoor.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 户外活动实体
 */
@Data
@TableName("activities")
@AllArgsConstructor
@NoArgsConstructor
public class Activity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 活动名称 */
    private String name;

    /** 活动类型: hiking-徒步, camping-露营, climbing-登山, cycling-骑行 */
    private String type;

    /** 活动地点 */
    private String location;

    /** 开始日期 */
    private LocalDate startDate;

    /** 预计时长 */
    private String duration;

    /** 活动描述 */
    private String description;

    /** 总距离(公里) */
    private Double totalDistance;

    /** 总爬升(米) */
    private Integer totalElevation;

    /** 封面图标 */
    private String coverIcon;

    /** 用户ID */
    private Integer userId;

    /** 是否收藏 */
    private Boolean favorite;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
