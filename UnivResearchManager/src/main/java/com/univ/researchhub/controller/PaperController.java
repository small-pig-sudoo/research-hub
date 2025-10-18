package com.univ.researchhub.controller;

import com.univ.researchhub.entity.Paper;
import com.univ.researchhub.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/paper")
public class PaperController {

    @Autowired
    private PaperService paperService;

    // 统一返回格式封装
    private Map<String, Object> buildResponse(boolean success, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    // 1. 新增论文（POST）
    @PostMapping
    public ResponseEntity<Map<String, Object>> addPaper(@RequestBody Paper paper) {
        try {
            boolean success = paperService.addPaper(paper);
            if (success) {
                return ResponseEntity.ok(buildResponse(true, "论文添加成功", paper));
            } else {
                return ResponseEntity.badRequest().body(buildResponse(false, "论文添加失败（参数校验未通过）", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "服务器错误：" + e.getMessage(), null));
        }
    }

    // 2. 根据ID查询论文详情（GET）
    @GetMapping("/{paperId}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer paperId) {
        try {
            Paper paper = paperService.getById(paperId);
            if (paper != null) {
                return ResponseEntity.ok(buildResponse(true, "查询成功", paper));
            } else {
                return ResponseEntity.badRequest().body(buildResponse(false, "论文不存在", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "查询失败：" + e.getMessage(), null));
        }
    }

    // 3. 根据作者ID查询论文（GET）
    @GetMapping("/author/{authorId}")
    public ResponseEntity<Map<String, Object>> getByAuthor(@PathVariable Integer authorId) {
        try {
            List<Paper> papers = paperService.getPapersByAuthorId(authorId);
            return ResponseEntity.ok(buildResponse(true, "查询成功", papers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "查询失败：" + e.getMessage(), null));
        }
    }

    // 4. 根据项目ID查询论文（GET）
    @GetMapping("/project/{projectId}")
    public ResponseEntity<Map<String, Object>> getByProject(@PathVariable Integer projectId) {
        try {
            List<Paper> papers = paperService.getPapersByProjectId(projectId);
            return ResponseEntity.ok(buildResponse(true, "查询成功", papers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "查询失败：" + e.getMessage(), null));
        }
    }

    // 5. 根据级别查询论文（GET）
    @GetMapping("/level/{level}")
    public ResponseEntity<Map<String, Object>> getByLevel(@PathVariable String level) {
        try {
            List<Paper> papers = paperService.getPapersByLevel(level);
            return ResponseEntity.ok(buildResponse(true, "查询成功", papers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "查询失败：" + e.getMessage(), null));
        }
    }

    // 6. 更新论文关联的项目（PUT）
    @PutMapping("/project")
    public ResponseEntity<Map<String, Object>> updateProject(
            @RequestParam Integer paperId,
            @RequestParam(required = false) Integer projectId) {
        try {
            boolean success = paperService.updatePaperProject(paperId, projectId);
            if (success) {
                return ResponseEntity.ok(buildResponse(true, "项目关联更新成功", null));
            } else {
                return ResponseEntity.badRequest().body(buildResponse(false, "更新失败（论文或项目不存在）", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "更新失败：" + e.getMessage(), null));
        }
    }

    // 7. 查询指定日期范围内的论文（GET）
    @GetMapping("/date-range")
    public ResponseEntity<Map<String, Object>> getByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            List<Paper> papers = paperService.getPapersByDateRange(startDate, endDate);
            return ResponseEntity.ok(buildResponse(true, "查询成功", papers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "查询失败：" + e.getMessage(), null));
        }
    }
}
