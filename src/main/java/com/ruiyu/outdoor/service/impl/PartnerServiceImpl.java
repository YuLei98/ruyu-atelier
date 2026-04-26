package com.ruiyu.outdoor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruiyu.outdoor.dto.PartnerCreateReq;
import com.ruiyu.outdoor.mapper.PartnerMapper;
import com.ruiyu.outdoor.model.Partner;
import com.ruiyu.outdoor.service.PartnerService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 伙伴服务实现
 */
@Service
public class PartnerServiceImpl extends ServiceImpl<PartnerMapper, Partner> implements PartnerService {

    @Override
    public Partner create(PartnerCreateReq req, Integer userId) {
        Partner partner = new Partner();
        partner.setName(req.getName());
        partner.setAvatarColor(req.getAvatarColor());
        partner.setContact(req.getContact());
        partner.setRemark(req.getRemark());
        partner.setUserId(userId);
        partner.setCreatedAt(LocalDateTime.now());
        partner.setUpdatedAt(LocalDateTime.now());

        this.save(partner);
        return partner;
    }

    @Override
    public List<Partner> getByUserId(Integer userId) {
        return this.list(new LambdaQueryWrapper<Partner>()
                .eq(Partner::getUserId, userId)
                .orderByDesc(Partner::getCreatedAt));
    }

    @Override
    public void delete(Long id) {
        this.removeById(id);
    }
}
