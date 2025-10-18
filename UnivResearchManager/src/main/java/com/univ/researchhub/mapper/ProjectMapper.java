package com.univ.researchhub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.univ.researchhub.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
    // 继承BaseMapper，获得基础CRUD方法（selectById、insert、updateById等）

    // 自定义方法1：根据项目状态查询
    @Select("SELECT * FROM project WHERE status = #{status}")
    List<Project> selectByStatus(String status);

    // 自定义方法2：根据负责人ID查询项目
    @Select("SELECT * FROM project WHERE leader_id = #{leaderId}")
    List<Project> selectByLeaderId(Integer leaderId);

    // 自定义方法3：查询指定类型且在有效期内的项目
    @Select("SELECT * FROM project WHERE project_type = #{type} AND start_date <= NOW() AND end_date >= NOW()")
    List<Project> selectValidByType(String type);
}
