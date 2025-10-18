package com.univ.researchhub.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("project") // 对应数据库中的project表
public class Project {
    private Integer projectId; // 对应数据库中的project_id字段
    private String name; // 对应数据库中的name字段
    private String projectType; // 对应数据库中的project_type字段，enum类型在实体类中用String接收
    private Date startDate; // 对应数据库中的start_date字段
    private Date endDate; // 对应数据库中的end_date字段
    private BigDecimal funding; // 对应数据库中的funding字段，decimal类型用BigDecimal接收
    private String status; // 对应数据库中的status字段，enum类型在实体类中用String接收
    private Integer leaderId; // 对应数据库中的leader_id字段
}