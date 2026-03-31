package com.univ.researchhub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.univ.researchhub.entity.FundingUsage;

import java.math.BigDecimal;
import java.util.List;

public interface FundingUsageService extends IService<FundingUsage> {

    /**
     * 根据项目ID查询已使用的经费总额
     */
    BigDecimal getTotalUsedByProjectId(Integer projectId);

    /**
     * 根据项目ID查询经费使用记录
     */
    List<FundingUsage> getByProjectId(Integer projectId);

}
