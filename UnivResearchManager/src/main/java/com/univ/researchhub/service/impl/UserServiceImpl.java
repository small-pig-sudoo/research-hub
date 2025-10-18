package com.univ.researchhub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.univ.researchhub.entity.User;
import com.univ.researchhub.mapper.UserMapper;
import com.univ.researchhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户业务实现类：实现UserService接口定义的业务逻辑
 * 继承ServiceImpl<UserMapper, User>：自动关联UserMapper，无需手动注入即可使用Mapper方法
 */
@Service // 标记为Spring服务类，让Spring扫描并管理，后续可注入到Controller
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    // 1. 实现“根据用户名查询用户”：使用LambdaQueryWrapper构建条件查询
    @Override
    public User getByUsername(String username) {
        // LambdaQueryWrapper：避免字符串拼写错误，更简洁的条件构造
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username); // 条件：username = 传入的用户名
        return baseMapper.selectOne(queryWrapper); // baseMapper即UserMapper，自动继承自ServiceImpl
    }

    // 2. 实现“校验用户名是否已存在”
    @Override
    public boolean checkUsernameExists(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        // selectCount：查询符合条件的记录数，>0说明用户名已存在
        return baseMapper.selectCount(queryWrapper) > 0;
    }

    // 3. 实现“根据角色查询用户列表”
    @Override
    public List<User> getUsersByRole(String role) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getRole, role); // 条件：role = 传入的角色（如"ADMIN"、"STUDENT"）
        return baseMapper.selectList(queryWrapper);
    }
}