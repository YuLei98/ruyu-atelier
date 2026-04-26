package com.ruiyu.outdoor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruiyu.outdoor.dto.PartnerCreateReq;
import com.ruiyu.outdoor.model.Partner;

import java.util.List;

/**
 * 伙伴服务接口
 */
public interface PartnerService extends IService<Partner> {

    Partner create(PartnerCreateReq req, Integer userId);

    List<Partner> getByUserId(Integer userId);

    void delete(Long id);
}
