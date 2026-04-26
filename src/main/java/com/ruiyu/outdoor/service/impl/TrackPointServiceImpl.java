package com.ruiyu.outdoor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruiyu.outdoor.dto.TrackPointReq;
import com.ruiyu.outdoor.mapper.TrackPointMapper;
import com.ruiyu.outdoor.model.TrackPoint;
import com.ruiyu.outdoor.service.TrackPointService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 轨迹服务实现
 */
@Service
public class TrackPointServiceImpl extends ServiceImpl<TrackPointMapper, TrackPoint> implements TrackPointService {

    @Override
    public TrackPoint addPoint(TrackPointReq req) {
        TrackPoint point = new TrackPoint();
        point.setActivityId(req.getActivityId());
        point.setLatitude(req.getLatitude());
        point.setLongitude(req.getLongitude());
        point.setAltitude(req.getAltitude());
        point.setSpeed(req.getSpeed());
        point.setTimestamp(LocalDateTime.now());
        point.setCreatedAt(LocalDateTime.now());

        this.save(point);
        return point;
    }

    @Override
    public List<TrackPoint> getByActivityId(Long activityId) {
        return this.list(new LambdaQueryWrapper<TrackPoint>()
                .eq(TrackPoint::getActivityId, activityId)
                .orderByAsc(TrackPoint::getTimestamp));
    }

    @Override
    public void deleteByActivityId(Long activityId) {
        this.remove(new LambdaQueryWrapper<TrackPoint>()
                .eq(TrackPoint::getActivityId, activityId));
    }
}
