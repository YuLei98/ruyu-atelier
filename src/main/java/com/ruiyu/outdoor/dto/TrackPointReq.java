package com.ruiyu.outdoor.dto;

import lombok.Data;

/**
 * 轨迹点请求
 */
@Data
public class TrackPointReq {

    private Long activityId;

    private Double latitude;

    private Double longitude;

    private Double altitude;

    private Double speed;
}
