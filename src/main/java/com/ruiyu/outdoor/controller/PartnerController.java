package com.ruiyu.outdoor.controller;

import com.ruiyu.outdoor.dto.PartnerCreateReq;
import com.ruiyu.outdoor.model.Partner;
import com.ruiyu.outdoor.service.PartnerService;
import icu.ruiyu.framework.common.CommonResult;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 伙伴 Controller
 */
@RestController
@RequestMapping("/api/partners")
public class PartnerController {

    @Autowired
    private PartnerService partnerService;

    /**
     * 创建伙伴
     */
    @PostMapping
    public CommonResult<Partner> create(@Valid @RequestBody PartnerCreateReq req,
                                        @RequestHeader(value = "X-User-Id", defaultValue = "1") Integer userId) {
        Partner partner = partnerService.create(req, userId);
        return CommonResult.success(partner);
    }

    /**
     * 获取用户的所有伙伴
     */
    @GetMapping
    public CommonResult<List<Partner>> list(@RequestHeader(value = "X-User-Id", defaultValue = "1") Integer userId) {
        List<Partner> partners = partnerService.getByUserId(userId);
        return CommonResult.success(partners);
    }

    /**
     * 删除伙伴
     */
    @DeleteMapping("/{id}")
    public CommonResult<Void> delete(@PathVariable Long id) {
        partnerService.delete(id);
        return CommonResult.successMessage("删除成功");
    }
}
