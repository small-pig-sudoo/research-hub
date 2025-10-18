package com.univ.researchhub.controller;

import com.univ.researchhub.entity.ProjectMember;
import com.univ.researchhub.service.ProjectMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project-member")
public class ProjectMemberController {

    @Autowired
    private ProjectMemberService projectMemberService;

    // 统一返回格式封装
    private Map<String, Object> buildResponse(boolean success, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    // 1. 新增项目成员（POST）
    @PostMapping
    public ResponseEntity<Map<String, Object>> addMember(@RequestBody ProjectMember projectMember) {
        try {
            boolean success = projectMemberService.addProjectMember(projectMember);
            if (success) {
                return ResponseEntity.ok(buildResponse(true, "添加项目成员成功", projectMember));
            } else {
                return ResponseEntity.badRequest().body(buildResponse(false, "添加失败（项目/用户不存在或已加入）", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "服务器错误：" + e.getMessage(), null));
        }
    }

    // 2. 查询项目所有成员（GET）
    @GetMapping("/project/{projectId}")
    public ResponseEntity<Map<String, Object>> getByProjectId(@PathVariable Integer projectId) {
        try {
            List<ProjectMember> members = projectMemberService.getMembersByProjectId(projectId);
            return ResponseEntity.ok(buildResponse(true, "查询成功", members));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "查询失败：" + e.getMessage(), null));
        }
    }

    // 3. 查询用户参与的项目（GET）
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getByUserId(@PathVariable Integer userId) {
        try {
            List<ProjectMember> projects = projectMemberService.getProjectsByUserId(userId);
            return ResponseEntity.ok(buildResponse(true, "查询成功", projects));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "查询失败：" + e.getMessage(), null));
        }
    }

    // 4. 修改成员角色（PUT）
    @PutMapping("/role")
    public ResponseEntity<Map<String, Object>> updateRole(
            @RequestParam Integer projectId,
            @RequestParam Integer userId,
            @RequestParam String newRole) {
        try {
            boolean success = projectMemberService.updateMemberRole(projectId, userId, newRole);
            if (success) {
                return ResponseEntity.ok(buildResponse(true, "角色修改成功", null));
            } else {
                return ResponseEntity.badRequest().body(buildResponse(false, "修改失败（用户不是项目成员）", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "修改失败：" + e.getMessage(), null));
        }
    }

    // 5. 移除项目成员（DELETE）
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> removeMember(
            @RequestParam Integer projectId,
            @RequestParam Integer userId) {
        try {
            boolean success = projectMemberService.removeMember(projectId, userId);
            if (success) {
                return ResponseEntity.ok(buildResponse(true, "移除成员成功", null));
            } else {
                return ResponseEntity.badRequest().body(buildResponse(false, "移除失败（成员不存在）", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "移除失败：" + e.getMessage(), null));
        }
    }

    // 6. 校验用户是否为项目成员（GET）
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkMember(
            @RequestParam Integer projectId,
            @RequestParam Integer userId) {
        try {
            boolean isMember = projectMemberService.isMemberOfProject(projectId, userId);
            String message = isMember ? "该用户是项目成员" : "该用户不是项目成员";
            return ResponseEntity.ok(buildResponse(true, message, isMember));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "校验失败：" + e.getMessage(), null));
        }
    }
}
