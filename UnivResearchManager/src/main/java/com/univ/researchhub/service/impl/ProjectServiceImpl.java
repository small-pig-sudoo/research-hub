package com.univ.researchhub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univ.researchhub.entity.Project;
import com.univ.researchhub.entity.User;
import com.univ.researchhub.mapper.ProjectMapper;
import com.univ.researchhub.mapper.UserMapper;
import com.univ.researchhub.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private UserMapper userMapper; // 用于校验负责人是否存在（关联user表）

    /**
     * 新增项目（带完整业务校验）
     */
    @Override
    @Transactional
    public boolean addProject(Project project) {
        // 校验1：负责人是否存在（leader_id关联user表）
        Integer leaderId = project.getLeaderId();
        User leader = userMapper.selectById(leaderId);
        if (leader == null) {
            logError("新增失败：负责人ID不存在（leaderId=" + leaderId + "）");
            return false;
        }

        // 校验2：项目名称不能为空
        if (project.getName() == null || project.getName().trim().isEmpty()) {
            logError("新增失败：项目名称不能为空");
            return false;
        }

        // 校验3：开始日期不能晚于结束日期
        Date startDate = project.getStartDate();
        Date endDate = project.getEndDate();
        if (startDate == null || endDate == null || startDate.after(endDate)) {
            logError("新增失败：开始日期不能晚于结束日期");
            return false;
        }

        // 校验4：预算不能为负数
        if (!isFundingValid(project.getFunding())) {
            logError("新增失败：项目预算不能为负数");
            return false;
        }

        // 校验5：状态只能是指定值（如"未开始"、"进行中"、"已结项"）
        String status = project.getStatus();
        if (!isStatusValid(status)) {
            logError("新增失败：无效的项目状态（status=" + status + "）");
            return false;
        }

        // 校验通过，执行新增
        return save(project);
    }

    /**
     * 根据状态查询项目
     */
    @Override
    public List<Project> getProjectsByStatus(String status) {
        return projectMapper.selectByStatus(status);
    }

    /**
     * 根据负责人ID查询项目
     */
    @Override
    public List<Project> getProjectsByLeaderId(Integer leaderId) {
        return projectMapper.selectByLeaderId(leaderId);
    }

    /**
     * 更新项目状态
     */
    @Override
    @Transactional
    public boolean updateProjectStatus(Integer projectId, String newStatus) {
        // 校验1：项目是否存在
        Project project = getById(projectId);
        if (project == null) {
            logError("更新状态失败：项目ID不存在（projectId=" + projectId + "）");
            return false;
        }

        // 校验2：新状态是否有效
        if (!isStatusValid(newStatus)) {
            logError("更新状态失败：无效的项目状态（newStatus=" + newStatus + "）");
            return false;
        }

        // 执行更新
        project.setStatus(newStatus);
        return updateById(project);
    }

    /**
     * 查询指定类型的有效项目
     */
    @Override
    public List<Project> getValidProjectsByType(String type) {
        return projectMapper.selectValidByType(type);
    }

    /**
     * 校验预算是否合理（不能为负数）
     */
    @Override
    public boolean isFundingValid(BigDecimal funding) {
        return funding != null && funding.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * 校验状态是否为合法值
     * 可根据实际业务扩展允许的状态列表
     */
    private boolean isStatusValid(String status) {
        if (status == null) {
            return false;
        }
        // ⭐ 和 project.status 的 enum 完全对齐
        List<String> validStatus = List.of("申报中", "在研", "已结题", "已终止");
        return validStatus.contains(status);
    }

    /**
     * 错误日志打印
     */
    private void logError(String message) {
        System.err.println("[ProjectService] " + message);
    }

    @Override
    public List<Project> getRecentProjects(int limit) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Project::getStartDate)
                .last("LIMIT " + limit);
        return projectMapper.selectList(wrapper);
    }
}
