package com.ruiyu.outdoor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * 创建活动请求
 */
@Data
public class ActivityCreateReq {

    @NotBlank(message = "活动名称不能为空")
    private String name;

    /** 活动类型: hiking, camping, climbing, cycling */
    private String type;

    private String location;

    private LocalDate startDate;

    private String duration;

    private String description;
}
