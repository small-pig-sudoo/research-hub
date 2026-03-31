package com.univ.researchhub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univ.researchhub.entity.User;
import com.univ.researchhub.mapper.UserMapper;
import com.univ.researchhub.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户业务实现类
 * 继承 ServiceImpl<UserMapper, User> 后可直接使用 this.page / baseMapper 等能力
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 根据用户名查询用户
     */
    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 校验用户名是否已存在
     */
    @Override
    public boolean checkUsernameExists(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return baseMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 根据角色查询用户列表（非分页）
     */
    @Override
    public List<User> getUsersByRole(String role) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getRole, role);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 分页查询用户（支持关键字 + 角色筛选）
     * keyword：匹配 username 或 real_name（Java字段：realName）
     */
    @Override
    public IPage<User> pageUsers(Integer page, Integer size, String keyword, String role) {

        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 关键字：username / realName
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or()
                    .like(User::getRealName, keyword)   // ✅ 注意：这里必须是 getRealName
            );
        }

        // 角色筛选
        if (StringUtils.hasText(role)) {
            wrapper.eq(User::getRole, role);
        }

        // ✅ 建议按创建时间倒序展示（更符合用户列表习惯）
        // 如果你的 User.java 已加入 createdAt 字段，会自动生效
        wrapper.orderByDesc(User::getCreatedAt);

        // ✅ 不要使用 select(...) 过滤字段，避免 realName/createdAt 丢失
        return this.page(pageParam, wrapper);
    }
}
