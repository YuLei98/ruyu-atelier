package com.ruiyu.outdoor.controller;

import com.ruiyu.outdoor.dto.TrackPointReq;
import com.ruiyu.outdoor.model.TrackPoint;
import com.ruiyu.outdoor.service.TrackPointService;
import icu.ruiyu.framework.common.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轨迹 Controller
 */
@RestController
@RequestMapping("/api/activities/{activityId}/track")
public class TrackPointController {

    @Autowired
    private TrackPointService trackPointService;

    /**
     * 添加轨迹点
     */
    @PostMapping
    public CommonResult<TrackPoint> addPoint(@PathVariable Long activityId,
                                              @RequestBody TrackPointReq req) {
        req.setActivityId(activityId);
        TrackPoint point = trackPointService.addPoint(req);
        return CommonResult.success(point);
    }

    /**
     * 获取活动的轨迹
     */
    @GetMapping
    public CommonResult<List<TrackPoint>> getTrack(@PathVariable Long activityId) {
        List<TrackPoint> points = trackPointService.getByActivityId(activityId);
        return CommonResult.success(points);
    }

    /**
     * 删除轨迹
     */
    @DeleteMapping
    public CommonResult<Void> deleteTrack(@PathVariable Long activityId) {
        trackPointService.deleteByActivityId(activityId);
        return CommonResult.successMessage("删除成功");
    }
}
