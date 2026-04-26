package com.ruiyu.outdoor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建伙伴请求
 */
@Data
public class PartnerCreateReq {

    @NotBlank(message = "伙伴名称不能为空")
    private String name;

    private String avatarColor;

    private String contact;

    private String remark;
}
