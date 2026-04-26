package com.ruiyu.outdoor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruiyu.outdoor.dto.TrackPointReq;
import com.ruiyu.outdoor.model.TrackPoint;

import java.util.List;

/**
 * 轨迹服务接口
 */
public interface TrackPointService extends IService<TrackPoint> {

    /**
     * 添加轨迹点
     */
    TrackPoint addPoint(TrackPointReq req);

    /**
     * 获取活动的所有轨迹点
     */
    List<TrackPoint> getByActivityId(Long activityId);

    /**
     * 删除活动的轨迹
     */
    void deleteByActivityId(Long activityId);
}
