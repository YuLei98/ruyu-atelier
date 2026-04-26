package com.ruiyu.outdoor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruiyu.outdoor.dto.ActivityCreateReq;
import com.ruiyu.outdoor.model.Activity;

import java.util.List;

/**
 * 活动服务接口
 */
public interface ActivityService extends IService<Activity> {

    /**
     * 创建活动
     */
    Activity create(ActivityCreateReq req, Integer userId);

    /**
     * 获取用户的所有活动
     */
    List<Activity> getByUserId(Integer userId);

    /**
     * 更新活动
     */
    Activity update(Long id, ActivityCreateReq req);

    /**
     * 删除活动
     */
    void delete(Long id);

    /**
     * 切换收藏状态
     */
    Activity toggleFavorite(Long id);
}
