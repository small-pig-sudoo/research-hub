package com.univ.researchhub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.univ.researchhub.entity.ProjectMember;

import java.util.List;

/**
 * 项目成员业务逻辑接口
 * 处理项目与用户的关联关系（如添加成员、查询成员、修改角色等）
 */
public interface ProjectMemberService extends IService<ProjectMember> {
    // 1. 新增项目成员（带校验：项目和用户存在、避免重复关联）
    boolean addProjectMember(ProjectMember projectMember);

    // 2. 根据项目ID查询所有成员
    List<ProjectMember> getMembersByProjectId(Integer projectId);

    // 3. 根据用户ID查询参与的项目
    List<ProjectMember> getProjectsByUserId(Integer userId);

    // 4. 修改用户在项目中的角色
    boolean updateMemberRole(Integer projectId, Integer userId, String newRole);

    // 5. 从项目中移除成员
    boolean removeMember(Integer projectId, Integer userId);

    // 6. 校验用户是否为项目成员
    boolean isMemberOfProject(Integer projectId, Integer userId);
}
