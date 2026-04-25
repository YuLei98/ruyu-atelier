package icu.ruiyu.framework.integration.mysql.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("users")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String username;

    private String passwordHash;

    private String roles;

    private Integer enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String nickname;

    private Integer age;
}
