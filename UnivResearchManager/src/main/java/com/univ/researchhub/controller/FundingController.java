package com.univ.researchhub.controller;

import com.univ.researchhub.entity.FundingUsage;
import com.univ.researchhub.entity.Project;
import com.univ.researchhub.service.FundingUsageService;
import com.univ.researchhub.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/funding")
public class FundingController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private FundingUsageService fundingUsageService;

    // 统一返回格式封装
    private Map<String, Object> buildResponse(boolean success, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    /**
     * 获取所有项目的经费使用情况
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getFundingStatistics() {
        try {
            // 获取所有项目
            List<Project> projects = projectService.list();
            
            // 处理经费数据
            List<Map<String, Object>> fundingList = new ArrayList<>();
            BigDecimal totalFunding = BigDecimal.ZERO;
            BigDecimal totalUsed = BigDecimal.ZERO;
            
            for (Project project : projects) {
                // 获取项目总经费
                BigDecimal projectFunding = project.getFunding() != null ? project.getFunding() : BigDecimal.ZERO;
                // 获取已使用经费
                BigDecimal usedFunding = fundingUsageService.getTotalUsedByProjectId(project.getProjectId());
                // 计算剩余经费
                BigDecimal remainingFunding = projectFunding.subtract(usedFunding);
                // 计算使用率
                BigDecimal usageRate = BigDecimal.ZERO;
                if (projectFunding.compareTo(BigDecimal.ZERO) > 0) {
                    usageRate = usedFunding.divide(projectFunding, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                }
                
                // 确定状态
                String status = "正常";
                if (usageRate.compareTo(BigDecimal.ZERO) == 0) {
                    status = "未使用";
                } else if (usageRate.compareTo(new BigDecimal(100)) > 0) {
                    status = "超支";
                }
                
                // 构建项目经费信息
                Map<String, Object> fundingInfo = new HashMap<>();
                fundingInfo.put("projectId", project.getProjectId());
                fundingInfo.put("projectName", project.getName());
                fundingInfo.put("leaderId", project.getLeaderId());
                fundingInfo.put("totalFunding", projectFunding);
                fundingInfo.put("usedFunding", usedFunding);
                fundingInfo.put("remainingFunding", remainingFunding);
                fundingInfo.put("usageRate", usageRate);
                fundingInfo.put("status", status);
                
                fundingList.add(fundingInfo);
                totalFunding = totalFunding.add(projectFunding);
                totalUsed = totalUsed.add(usedFunding);
            }
            
            // 计算总体统计数据
            BigDecimal totalRemaining = totalFunding.subtract(totalUsed);
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalFunding", totalFunding);
            statistics.put("usedFunding", totalUsed);
            statistics.put("remainingFunding", totalRemaining);
            statistics.put("projectCount", projects.size());
            
            Map<String, Object> data = new HashMap<>();
            data.put("statistics", statistics);
            data.put("fundingList", fundingList);
            
            return ResponseEntity.ok(buildResponse(true, "查询成功", data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "查询失败：" + e.getMessage(), null));
        }
    }

    /**
     * 获取项目的经费使用记录
     */
    @GetMapping("/usage/{projectId}")
    public ResponseEntity<Map<String, Object>> getFundingUsage(@PathVariable Integer projectId) {
        try {
            List<FundingUsage> usageList = fundingUsageService.getByProjectId(projectId);
            return ResponseEntity.ok(buildResponse(true, "查询成功", usageList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "查询失败：" + e.getMessage(), null));
        }
    }

    /**
     * 添加经费使用记录
     */
    @PostMapping("/usage")
    public ResponseEntity<Map<String, Object>> addFundingUsage(@RequestBody FundingUsage fundingUsage) {
        try {
            boolean success = fundingUsageService.save(fundingUsage);
            if (success) {
                return ResponseEntity.ok(buildResponse(true, "添加成功", fundingUsage));
            } else {
                return ResponseEntity.badRequest()
                        .body(buildResponse(false, "添加失败", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "添加失败：" + e.getMessage(), null));
        }
    }

    /**
     * 获取单个项目的经费详情
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<Map<String, Object>> getProjectFunding(@PathVariable Integer projectId) {
        try {
            // 获取项目信息
            Project project = projectService.getById(projectId);
            if (project == null) {
                return ResponseEntity.badRequest()
                        .body(buildResponse(false, "项目不存在", null));
            }
            
            // 获取已使用经费
            BigDecimal usedFunding = fundingUsageService.getTotalUsedByProjectId(projectId);
            // 获取总经费
            BigDecimal totalFunding = project.getFunding() != null ? project.getFunding() : BigDecimal.ZERO;
            // 计算剩余经费
            BigDecimal remainingFunding = totalFunding.subtract(usedFunding);
            // 计算使用率
            BigDecimal usageRate = BigDecimal.ZERO;
            if (totalFunding.compareTo(BigDecimal.ZERO) > 0) {
                usageRate = usedFunding.divide(totalFunding, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            }
            
            // 确定状态
            String status = "正常";
            if (usageRate.compareTo(BigDecimal.ZERO) == 0) {
                status = "未使用";
            } else if (usageRate.compareTo(new BigDecimal(100)) > 0) {
                status = "超支";
            }
            
            // 构建经费详情
            Map<String, Object> fundingDetail = new HashMap<>();
            fundingDetail.put("projectId", project.getProjectId());
            fundingDetail.put("projectName", project.getName());
            fundingDetail.put("leaderId", project.getLeaderId());
            fundingDetail.put("totalFunding", totalFunding);
            fundingDetail.put("usedFunding", usedFunding);
            fundingDetail.put("remainingFunding", remainingFunding);
            fundingDetail.put("usageRate", usageRate);
            fundingDetail.put("status", status);
            
            // 获取经费使用记录
            List<FundingUsage> usageList = fundingUsageService.getByProjectId(projectId);
            fundingDetail.put("usageList", usageList);
            
            return ResponseEntity.ok(buildResponse(true, "查询成功", fundingDetail));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildResponse(false, "查询失败：" + e.getMessage(), null));
        }
    }

}
