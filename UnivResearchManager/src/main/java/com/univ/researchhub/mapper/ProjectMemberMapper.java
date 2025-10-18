package com.univ.researchhub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.univ.researchhub.entity.ProjectMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProjectMemberMapper extends BaseMapper<ProjectMember> {
    // 已有方法...
    @Select("SELECT * FROM project_member WHERE project_id = #{projectId}")
    List<ProjectMember> selectByProjectId(Integer projectId);

    @Select("SELECT * FROM project_member WHERE user_id = #{userId}")
    List<ProjectMember> selectByUserId(Integer userId);

    @Select("SELECT role_in_project FROM project_member WHERE project_id = #{projectId} AND user_id = #{userId}")
    String selectRoleByProjectAndUser(Integer projectId, Integer userId);

    // 新增：联表查询项目成员的详细信息（包含用户名）
    @Select("SELECT pm.*, u.username " +
            "FROM project_member pm " +
            "LEFT JOIN user u ON pm.user_id = u.user_id " +
            "WHERE pm.project_id = #{projectId}")
    List<Map<String, Object>> selectProjectMembersWithUsername(Integer projectId);
}
