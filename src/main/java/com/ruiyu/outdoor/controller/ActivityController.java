package com.ruiyu.outdoor.controller;

import com.ruiyu.outdoor.dto.ActivityCreateReq;
import com.ruiyu.outdoor.model.Activity;
import com.ruiyu.outdoor.service.ActivityService;
import icu.ruiyu.framework.common.CommonResult;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 活动 Controller
 */
@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    /**
     * 创建活动
     */
    @PostMapping
    public CommonResult<Activity> create(@Valid @RequestBody ActivityCreateReq req,
                                          @RequestHeader(value = "X-User-Id", defaultValue = "1") Integer userId) {
        Activity activity = activityService.create(req, userId);
        return CommonResult.success(activity);
    }

    /**
     * 获取用户的所有活动
     */
    @GetMapping
    public CommonResult<List<Activity>> list(@RequestHeader(value = "X-User-Id", defaultValue = "1") Integer userId) {
        List<Activity> activities = activityService.getByUserId(userId);
        return CommonResult.success(activities);
    }

    /**
     * 获取活动详情
     */
    @GetMapping("/{id}")
    public CommonResult<Activity> get(@PathVariable Long id) {
        Activity activity = activityService.getById(id);
        if (activity == null) {
            return CommonResult.fail("活动不存在");
        }
        return CommonResult.success(activity);
    }

    /**
     * 更新活动
     */
    @PutMapping("/{id}")
    public CommonResult<Activity> update(@PathVariable Long id,
                                         @Valid @RequestBody ActivityCreateReq req) {
        Activity activity = activityService.update(id, req);
        return CommonResult.success(activity);
    }

    /**
     * 删除活动
     */
    @DeleteMapping("/{id}")
    public CommonResult<Void> delete(@PathVariable Long id) {
        activityService.delete(id);
        return CommonResult.successMessage("删除成功");
    }

    /**
     * 切换收藏状态
     */
    @PostMapping("/{id}/favorite")
    public CommonResult<Activity> toggleFavorite(@PathVariable Long id) {
        Activity activity = activityService.toggleFavorite(id);
        return CommonResult.success(activity);
    }
}
