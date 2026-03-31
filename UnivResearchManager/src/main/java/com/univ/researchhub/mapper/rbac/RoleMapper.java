package com.univ.researchhub.mapper.rbac;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.univ.researchhub.entity.rbac.Role;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {}
