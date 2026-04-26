package com.ruiyu.outdoor.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 伙伴实体
 */
@Data
@TableName("partners")
@AllArgsConstructor
@NoArgsConstructor
public class Partner {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 伙伴名称 */
    private String name;

    /** 伙伴头像颜色 */
    private String avatarColor;

    /** 联系方式 */
    private String contact;

    /** 备注 */
    private String remark;

    /** 所属用户ID */
    private Integer userId;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
