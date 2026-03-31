package com.univ.researchhub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.univ.researchhub.entity.FundingUsage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

public interface FundingUsageMapper extends BaseMapper<FundingUsage> {

    /**
     * 根据项目ID查询已使用的经费总额
     */
    @Select("SELECT COALESCE(SUM(used_amount), 0) FROM funding_usage WHERE project_id = #{projectId}")
    BigDecimal getTotalUsedByProjectId(@Param("projectId") Integer projectId);

}
