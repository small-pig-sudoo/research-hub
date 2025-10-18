package com.univ.researchhub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univ.researchhub.entity.Paper;
import com.univ.researchhub.entity.Project;
import com.univ.researchhub.entity.User;
import com.univ.researchhub.mapper.PaperMapper;
import com.univ.researchhub.mapper.ProjectMapper;
import com.univ.researchhub.mapper.UserMapper;
import com.univ.researchhub.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private UserMapper userMapper; // 用于校验作者是否存在（关联user表）

    @Autowired
    private ProjectMapper projectMapper; // 用于校验项目是否存在（若关联项目）

    /**
     * 新增论文（带业务校验）
     */
    @Override
    @Transactional
    public boolean addPaper(Paper paper) {
        // 校验1：作者必须存在（author_id关联user表）
        Integer authorId = paper.getAuthorId();
        User author = userMapper.selectById(authorId);
        if (author == null) {
            logError("新增失败：作者ID不存在（authorId=" + authorId + "）");
            return false;
        }

        // 校验2：论文标题不能为空
        if (paper.getTitle() == null || paper.getTitle().trim().isEmpty()) {
            logError("新增失败：论文标题不能为空");
            return false;
        }

        // 校验3：发表日期不能为未来日期（可选，根据业务需求调整）
        Date publishDate = paper.getPublishDate();
        if (publishDate != null && publishDate.after(new Date())) {
            logError("新增失败：发表日期不能晚于当前日期");
            return false;
        }

        // 校验4：若关联项目，项目必须存在
        Integer projectId = paper.getProjectId();
        if (projectId != null) {
            Project project = projectMapper.selectById(projectId);
            if (project == null) {
                logError("新增失败：关联的项目ID不存在（projectId=" + projectId + "）");
                return false;
            }
        }

        // 校验5：论文级别合法性（如核心、普刊、SCI等）
        if (!isLevelValid(paper.getLevel())) {
            logError("新增失败：无效的论文级别（level=" + paper.getLevel() + "）");
            return false;
        }

        // 校验通过，执行新增
        return save(paper);
    }

    /**
     * 根据作者ID查询论文
     */
    @Override
    public List<Paper> getPapersByAuthorId(Integer authorId) {
        return paperMapper.selectByAuthorId(authorId);
    }

    /**
     * 根据项目ID查询关联的论文
     */
    @Override
    public List<Paper> getPapersByProjectId(Integer projectId) {
        return paperMapper.selectByProjectId(projectId);
    }

    /**
     * 根据论文级别查询
     */
    @Override
    public List<Paper> getPapersByLevel(String level) {
        return paperMapper.selectByLevel(level);
    }

    /**
     * 更新论文关联的项目
     */
    @Override
    @Transactional
    public boolean updatePaperProject(Integer paperId, Integer projectId) {
        // 校验1：论文是否存在
        Paper paper = getById(paperId);
        if (paper == null) {
            logError("更新失败：论文ID不存在（paperId=" + paperId + "）");
            return false;
        }

        // 校验2：若项目ID不为空，需校验项目存在
        if (projectId != null) {
            Project project = projectMapper.selectById(projectId);
            if (project == null) {
                logError("更新失败：关联的项目ID不存在（projectId=" + projectId + "）");
                return false;
            }
        }

        // 执行更新
        paper.setProjectId(projectId);
        return updateById(paper);
    }

    /**
     * 查询指定日期范围内的论文
     */
    @Override
    public List<Paper> getPapersByDateRange(Date startDate, Date endDate) {
        return paperMapper.selectByPublishDateRange(startDate, endDate);
    }

    /**
     * 校验论文级别是否合法
     * 可根据实际业务扩展允许的级别列表
     */
    private boolean isLevelValid(String level) {
        if (level == null) {
            return false;
        }
        // 假设合法级别为：核心、普刊、SCI、EI、SSCI
        List<String> validLevels = List.of("核心", "普刊", "SCI", "EI", "SSCI");
        return validLevels.contains(level);
    }

    /**
     * 错误日志打印
     */
    private void logError(String message) {
        System.err.println("[PaperService] " + message);
    }
}
