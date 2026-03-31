package com.univ.researchhub.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.univ.researchhub.entity.Project;
import com.univ.researchhub.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // 统一返回格式封装
    private Map<String, Object> buildResponse(boolean success, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    // 1. 新增项目（POST）
    @PostMapping
    public ResponseEntity<Map<String, Object>> addProject(@RequestBody Project project) {
        try {
            boolean success = projectService.addProject(project);
            if (success) {
                return ResponseEntity.ok(buildResponse(true, "项目创建成功", project));
            } else {
                return ResponseEntity.badRequest()
                        .body(buildResponse(false, "项目创建失败（参数校验未通过）", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "服务器错误：" + e.getMessage(), null));
        }
    }

    // 2. 项目统计接口（必须在 /{projectId} 之前定义）
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "projectType", required = false) String projectType,
            @RequestParam(value = "status", required = false) String status
    ) {
        try {
            // 构建查询条件
            LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
            
            if (StringUtils.hasText(projectType)) {
                wrapper.eq(Project::getProjectType, projectType);
            }
            if (StringUtils.hasText(status)) {
                wrapper.eq(Project::getStatus, status);
            }

            // 获取所有符合条件的项目
            List<Project> projects = projectService.list(wrapper);
            
            // 计算统计数据
            int total = projects.size();
            int ongoing = (int) projects.stream().filter(p -> "进行中".equals(p.getStatus())).count();
            int completed = (int) projects.stream().filter(p -> "已结题".equals(p.getStatus())).count();
            
            // 计算本年新增（根据开始日期）
            int currentYear = java.time.Year.now().getValue();
            int newThisYear = (int) projects.stream()
                    .filter(p -> p.getStartDate() != null && 
                            p.getStartDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().getYear() == currentYear)
                    .count();

            // 项目类型分布
            Map<String, Integer> typeCount = new HashMap<>();
            typeCount.put("国家级项目", 0);
            typeCount.put("省级项目", 0);
            typeCount.put("校级项目", 0);
            typeCount.put("横向项目", 0);
            typeCount.put("其他项目", 0);
            
            for (Project p : projects) {
                String type = p.getProjectType();
                if (type != null) {
                    switch (type) {
                        case "国家级":
                            typeCount.put("国家级项目", typeCount.get("国家级项目") + 1);
                            break;
                        case "省级":
                            typeCount.put("省级项目", typeCount.get("省级项目") + 1);
                            break;
                        case "校级":
                            typeCount.put("校级项目", typeCount.get("校级项目") + 1);
                            break;
                        case "横向":
                            typeCount.put("横向项目", typeCount.get("横向项目") + 1);
                            break;
                        default:
                            typeCount.put("其他项目", typeCount.get("其他项目") + 1);
                    }
                }
            }

            // 构建类型分布数据
            List<Map<String, Object>> typeDistribution = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : typeCount.entrySet()) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", entry.getKey());
                item.put("value", entry.getValue());
                typeDistribution.add(item);
            }

            // 项目状态分布
            Map<String, Integer> statusCount = new HashMap<>();
            statusCount.put("未开始", 0);
            statusCount.put("进行中", 0);
            statusCount.put("已结题", 0);
            statusCount.put("已暂停", 0);
            statusCount.put("申报中", 0);
            
            for (Project p : projects) {
                String s = p.getStatus();
                if (s != null && statusCount.containsKey(s)) {
                    statusCount.put(s, statusCount.get(s) + 1);
                } else if (s != null) {
                    // 处理其他状态
                    statusCount.put("其他", statusCount.getOrDefault("其他", 0) + 1);
                }
            }

            List<Map<String, Object>> statusDistribution = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : statusCount.entrySet()) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", entry.getKey());
                item.put("value", entry.getValue());
                statusDistribution.add(item);
            }

            // 年度趋势（近5年）
            List<Map<String, Object>> yearTrend = new ArrayList<>();
            for (int i = 4; i >= 0; i--) {
                int y = currentYear - i;
                final int targetYear = y;
                int count = (int) projects.stream()
                        .filter(p -> p.getStartDate() != null && 
                                p.getStartDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().getYear() == targetYear)
                        .count();
                Map<String, Object> item = new HashMap<>();
                item.put("year", String.valueOf(y));
                item.put("count", count);
                yearTrend.add(item);
            }

            // 构建响应数据
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("total", total);
            statistics.put("ongoing", ongoing);
            statistics.put("completed", completed);
            statistics.put("newThisYear", newThisYear);

            Map<String, Object> charts = new HashMap<>();
            charts.put("typeDistribution", typeDistribution);
            charts.put("statusDistribution", statusDistribution);
            charts.put("yearTrend", yearTrend);

            Map<String, Object> data = new HashMap<>();
            data.put("statistics", statistics);
            data.put("charts", charts);

            return ResponseEntity.ok(buildResponse(true, "查询成功", data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "查询失败：" + e.getMessage(), null));
        }
    }

    // 3. 根据ID查询项目详情（GET）
    @GetMapping("/{projectId}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer projectId) {
        try {
            Project project = projectService.getById(projectId);
            if (project != null) {
                return ResponseEntity.ok(buildResponse(true, "查询成功", project));
            } else {
                return ResponseEntity.badRequest()
                        .body(buildResponse(false, "项目不存在", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "查询失败：" + e.getMessage(), null));
        }
    }

    // 4. 根据状态查询项目（GET）
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getByStatus(@PathVariable String status) {
        try {
            List<Project> projects = projectService.getProjectsByStatus(status);
            return ResponseEntity.ok(buildResponse(true, "查询成功", projects));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "查询失败：" + e.getMessage(), null));
        }
    }

    // 5. 根据负责人查询项目（GET）
    @GetMapping("/leader/{leaderId}")
    public ResponseEntity<Map<String, Object>> getByLeader(@PathVariable Integer leaderId) {
        try {
            List<Project> projects = projectService.getProjectsByLeaderId(leaderId);
            return ResponseEntity.ok(buildResponse(true, "查询成功", projects));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "查询失败：" + e.getMessage(), null));
        }
    }

    // 6. 更新项目状态（PUT）
    @PutMapping("/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @RequestParam Integer projectId,
            @RequestParam String newStatus) {
        try {
            boolean success = projectService.updateProjectStatus(projectId, newStatus);
            if (success) {
                return ResponseEntity.ok(buildResponse(true, "状态更新成功", null));
            } else {
                return ResponseEntity.badRequest()
                        .body(buildResponse(false, "状态更新失败（项目不存在或状态无效）", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "更新失败：" + e.getMessage(), null));
        }
    }

    // 7. 查询有效项目（按类型）（GET）
    @GetMapping("/valid/{type}")
    public ResponseEntity<Map<String, Object>> getValidByType(@PathVariable String type) {
        try {
            List<Project> projects = projectService.getValidProjectsByType(type);
            return ResponseEntity.ok(buildResponse(true, "查询成功", projects));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "查询失败：" + e.getMessage(), null));
        }
    }

    /**
     * 8. 仪表盘 - 最近项目（按开始时间倒序，取最近5条）
     */
    @GetMapping("/recent")
    public ResponseEntity<Map<String, Object>> getRecentProjects() {
        try {
            List<Project> projects = projectService.getRecentProjects(5);
            return ResponseEntity.ok(buildResponse(true, "查询成功", projects));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "查询失败：" + e.getMessage(), null));
        }
    }

    /**
     * 9. 项目列表（分页 + 筛选）
     *    GET /api/project/list?pageNum=1&pageSize=10&name=...&projectType=...&status=...
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listProjects(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "projectType", required = false) String projectType,
            @RequestParam(value = "status", required = false) String status
    ) {
        try {
            LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();

            if (StringUtils.hasText(name)) {
                wrapper.like(Project::getName, name);
            }
            if (StringUtils.hasText(projectType)) {
                wrapper.eq(Project::getProjectType, projectType);
            }
            if (StringUtils.hasText(status)) {
                wrapper.eq(Project::getStatus, status);
            }

            Page<Project> page = new Page<>(pageNum, pageSize);
            projectService.page(page, wrapper);

            Map<String, Object> pageData = new HashMap<>();
            pageData.put("records", page.getRecords());
            pageData.put("total", page.getTotal());
            pageData.put("current", page.getCurrent());
            pageData.put("size", page.getSize());

            return ResponseEntity.ok(buildResponse(true, "查询成功", pageData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "查询失败：" + e.getMessage(), null));
        }
    }
}