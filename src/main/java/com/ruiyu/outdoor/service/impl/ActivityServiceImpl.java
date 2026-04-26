package com.ruiyu.outdoor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruiyu.outdoor.dto.ActivityCreateReq;
import com.ruiyu.outdoor.mapper.ActivityMapper;
import com.ruiyu.outdoor.model.Activity;
import com.ruiyu.outdoor.service.ActivityService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动服务实现
 */
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Override
    public Activity create(ActivityCreateReq req, Integer userId) {
        Activity activity = new Activity();
        activity.setName(req.getName());
        activity.setType(req.getType());
        activity.setLocation(req.getLocation());
        activity.setStartDate(req.getStartDate());
        activity.setDuration(req.getDuration());
        activity.setDescription(req.getDescription());
        activity.setUserId(userId);
        activity.setFavorite(false);
        activity.setCreatedAt(LocalDateTime.now());
        activity.setUpdatedAt(LocalDateTime.now());

        // 设置默认封面图标
        if (req.getType() != null) {
            activity.setCoverIcon(switch (req.getType()) {
                case "hiking" -> "🏔️";
                case "camping" -> "⛺";
                case "climbing" -> "🧗";
                case "cycling" -> "🚴";
                default -> "📍";
            });
        }

        this.save(activity);
        return activity;
    }

    @Override
    public List<Activity> getByUserId(Integer userId) {
        return this.list(new LambdaQueryWrapper<Activity>()
                .eq(Activity::getUserId, userId)
                .orderByDesc(Activity::getCreatedAt));
    }

    @Override
    public Activity update(Long id, ActivityCreateReq req) {
        Activity activity = this.getById(id);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }

        if (req.getName() != null) activity.setName(req.getName());
        if (req.getType() != null) activity.setType(req.getType());
        if (req.getLocation() != null) activity.setLocation(req.getLocation());
        if (req.getStartDate() != null) activity.setStartDate(req.getStartDate());
        if (req.getDuration() != null) activity.setDuration(req.getDuration());
        if (req.getDescription() != null) activity.setDescription(req.getDescription());
        activity.setUpdatedAt(LocalDateTime.now());

        this.updateById(activity);
        return activity;
    }

    @Override
    public void delete(Long id) {
        this.removeById(id);
    }

    @Override
    public Activity toggleFavorite(Long id) {
        Activity activity = this.getById(id);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        activity.setFavorite(!activity.getFavorite());
        this.updateById(activity);
        return activity;
    }
}
