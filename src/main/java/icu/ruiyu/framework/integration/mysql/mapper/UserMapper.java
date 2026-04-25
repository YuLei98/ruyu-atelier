package icu.ruiyu.framework.integration.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import icu.ruiyu.framework.integration.mysql.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM users WHERE username = #{username}")
    User selectByUsername(String username);

    @Select("SELECT COUNT(*) > 0 FROM users WHERE username = #{username}")
    boolean existsByUsername(String username);
}