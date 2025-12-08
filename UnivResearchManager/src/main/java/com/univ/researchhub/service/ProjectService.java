package com.univ.researchhub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.univ.researchhub.entity.Project;

import java.math.BigDecimal;
import java.util.List;

/**
 * 项目业务逻辑接口
 * 处理项目的创建、查询、状态更新等核心业务
 */
public interface ProjectService extends IService<Project> {
    // 1. 新增项目（带业务校验：负责人存在、日期合法等）
    boolean addProject(Project project);

    // 2. 根据状态查询项目（如"进行中"、"已结项"）
    List<Project> getProjectsByStatus(String status);

    // 3. 根据负责人ID查询项目
    List<Project> getProjectsByLeaderId(Integer leaderId);

    // 4. 更新项目状态（如从"进行中"改为"已结项"）
    boolean updateProjectStatus(Integer projectId, String newStatus);

    // 5. 查询指定类型的有效项目（在有效期内）
    List<Project> getValidProjectsByType(String type);

    // 6. 校验项目预算是否合理（如不能为负数）
    boolean isFundingValid(BigDecimal funding);
    List<Project> getRecentProjects(int limit);

}
