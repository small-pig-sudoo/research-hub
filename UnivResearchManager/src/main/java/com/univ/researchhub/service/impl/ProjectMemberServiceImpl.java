package com.univ.researchhub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univ.researchhub.entity.Project;
import com.univ.researchhub.entity.ProjectMember;
import com.univ.researchhub.entity.User;
import com.univ.researchhub.mapper.ProjectMapper;
import com.univ.researchhub.mapper.ProjectMemberMapper;
import com.univ.researchhub.mapper.UserMapper;
import com.univ.researchhub.service.ProjectMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 项目成员业务逻辑实现类
 * 处理项目与用户的关联关系，包含完整的业务校验和事务控制
 */
@Service
public class ProjectMemberServiceImpl extends ServiceImpl<ProjectMemberMapper, ProjectMember> implements ProjectMemberService {

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Autowired
    private ProjectMapper projectMapper; // 用于校验项目存在性

    @Autowired
    private UserMapper userMapper; // 用于校验用户存在性

    /**
     * 新增项目成员（带完整业务校验）
     * 1. 校验项目是否存在
     * 2. 校验用户是否存在
     * 3. 校验用户是否已加入项目（避免重复关联）
     */
    @Override
    @Transactional // 确保操作的原子性
    public boolean addProjectMember(ProjectMember projectMember) {
        Integer projectId = projectMember.getProjectId();
        Integer userId = projectMember.getUserId();

        // 校验1：项目是否存在
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            logError("新增失败：项目ID不存在（projectId=" + projectId + "）");
            return false;
        }

        // 校验2：用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            logError("新增失败：用户ID不存在（userId=" + userId + "）");
            return false;
        }

        // 校验3：用户是否已加入该项目（通过联合查询避免重复）
        LambdaQueryWrapper<ProjectMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectMember::getProjectId, projectId)
                .eq(ProjectMember::getUserId, userId);
        ProjectMember existingMember = baseMapper.selectOne(wrapper);
        if (existingMember != null) {
            logError("新增失败：用户已加入项目（projectId=" + projectId + ", userId=" + userId + "）");
            return false;
        }

        // 校验通过，执行新增
        int rows = baseMapper.insert(projectMember);
        return rows > 0;
    }

    /**
     * 根据项目ID查询所有成员
     */
    @Override
    public List<ProjectMember> getMembersByProjectId(Integer projectId) {
        // 调用Mapper层自定义方法
        return projectMemberMapper.selectByProjectId(projectId);
    }

    /**
     * 根据用户ID查询参与的所有项目
     */
    @Override
    public List<ProjectMember> getProjectsByUserId(Integer userId) {
        return projectMemberMapper.selectByUserId(userId);
    }

    /**
     * 修改用户在项目中的角色
     * 1. 先校验用户是否为项目成员
     * 2. 校验新角色是否合法（非空）
     * 3. 执行更新操作
     */
    @Override
    @Transactional
    public boolean updateMemberRole(Integer projectId, Integer userId, String newRole) {
        // 校验1：用户是否为项目成员
        if (!isMemberOfProject(projectId, userId)) {
            logError("修改角色失败：用户不是项目成员（projectId=" + projectId + ", userId=" + userId + "）");
            return false;
        }

        // 校验2：新角色不能为空
        if (newRole == null || newRole.trim().isEmpty()) {
            logError("修改角色失败：角色名称不能为空");
            return false;
        }

        // 执行更新
        LambdaQueryWrapper<ProjectMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectMember::getProjectId, projectId)
                .eq(ProjectMember::getUserId, userId);

        ProjectMember updateEntity = new ProjectMember();
        updateEntity.setRoleInProject(newRole.trim());

        int rows = baseMapper.update(updateEntity, wrapper);
        return rows > 0;
    }

    /**
     * 从项目中移除成员
     */
    @Override
    @Transactional
    public boolean removeMember(Integer projectId, Integer userId) {
        LambdaQueryWrapper<ProjectMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectMember::getProjectId, projectId)
                .eq(ProjectMember::getUserId, userId);

        int rows = baseMapper.delete(wrapper);
        if (rows == 0) {
            logError("移除成员失败：成员不存在（projectId=" + projectId + ", userId=" + userId + "）");
        }
        return rows > 0;
    }

    /**
     * 校验用户是否为项目成员
     */
    @Override
    public boolean isMemberOfProject(Integer projectId, Integer userId) {
        LambdaQueryWrapper<ProjectMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectMember::getProjectId, projectId)
                .eq(ProjectMember::getUserId, userId);
        return baseMapper.selectCount(wrapper) > 0;
    }

    /**
     * 日志打印工具方法
     */
    private void logError(String message) {
        // 实际项目中可替换为日志框架（如SLF4J）
        System.err.println("[ProjectMemberService] " + message);
    }
}
