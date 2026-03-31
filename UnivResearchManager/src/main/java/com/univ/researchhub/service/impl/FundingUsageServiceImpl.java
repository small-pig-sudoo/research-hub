package com.univ.researchhub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univ.researchhub.entity.FundingUsage;
import com.univ.researchhub.mapper.FundingUsageMapper;
import com.univ.researchhub.service.FundingUsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FundingUsageServiceImpl extends ServiceImpl<FundingUsageMapper, FundingUsage> implements FundingUsageService {

    @Autowired
    private FundingUsageMapper fundingUsageMapper;

    @Override
    public BigDecimal getTotalUsedByProjectId(Integer projectId) {
        return fundingUsageMapper.getTotalUsedByProjectId(projectId);
    }

    @Override
    public List<FundingUsage> getByProjectId(Integer projectId) {
        LambdaQueryWrapper<FundingUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FundingUsage::getProjectId, projectId)
                .orderByDesc(FundingUsage::getUsageDate);
        return fundingUsageMapper.selectList(wrapper);
    }

}
