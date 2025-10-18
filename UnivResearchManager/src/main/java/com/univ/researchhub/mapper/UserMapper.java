package com.univ.researchhub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.univ.researchhub.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据用户名查询用户（登录时使用）
     * 适配User实体类的username字段（对应数据库username字段）
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(String username);

    /**
     * 检查用户名是否已存在（添加用户时校验）
     */
    @Select("SELECT COUNT(*) FROM user WHERE username = #{username}")
    Integer countByUsername(String username);
}
