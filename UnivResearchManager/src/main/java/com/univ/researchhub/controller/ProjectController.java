package com.univ.researchhub.controller;

import com.univ.researchhub.entity.Project;
import com.univ.researchhub.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                return ResponseEntity.badRequest().body(buildResponse(false, "项目创建失败（参数校验未通过）", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "服务器错误：" + e.getMessage(), null));
        }
    }

    // 2. 根据ID查询项目详情（GET）
    @GetMapping("/{projectId}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer projectId) {
        try {
            Project project = projectService.getById(projectId);
            if (project != null) {
                return ResponseEntity.ok(buildResponse(true, "查询成功", project));
            } else {
                return ResponseEntity.badRequest().body(buildResponse(false, "项目不存在", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "查询失败：" + e.getMessage(), null));
        }
    }

    // 3. 根据状态查询项目（GET）
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

    // 4. 根据负责人查询项目（GET）
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

    // 5. 更新项目状态（PUT）
    @PutMapping("/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @RequestParam Integer projectId,
            @RequestParam String newStatus) {
        try {
            boolean success = projectService.updateProjectStatus(projectId, newStatus);
            if (success) {
                return ResponseEntity.ok(buildResponse(true, "状态更新成功", null));
            } else {
                return ResponseEntity.badRequest().body(buildResponse(false, "状态更新失败（项目不存在或状态无效）", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "更新失败：" + e.getMessage(), null));
        }
    }

    // 6. 查询有效项目（按类型）（GET）
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
}
