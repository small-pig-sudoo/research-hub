package com.univ.researchhub.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("funding_usage") // 对应数据库中的funding_usage表
public class FundingUsage {
    private Long usageId; // 对应数据库中的usage_id字段
    private Integer projectId; // 对应数据库中的project_id字段
    private BigDecimal usedAmount; // 对应数据库中的used_amount字段
    private Date usageDate; // 对应数据库中的usage_date字段
    private String description; // 对应数据库中的description字段
}
