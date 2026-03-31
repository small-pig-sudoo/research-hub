package com.univ.researchhub.service.rbac.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.univ.researchhub.entity.User;
import com.univ.researchhub.entity.rbac.Menu;
import com.univ.researchhub.entity.rbac.Role;
import com.univ.researchhub.entity.rbac.RoleMenu;
import com.univ.researchhub.entity.rbac.UserRole;
import com.univ.researchhub.mapper.UserMapper;
import com.univ.researchhub.mapper.rbac.MenuMapper;
import com.univ.researchhub.mapper.rbac.RoleMapper;
import com.univ.researchhub.mapper.rbac.RoleMenuMapper;
import com.univ.researchhub.mapper.rbac.UserRoleMapper;
import com.univ.researchhub.service.rbac.RbacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RbacServiceImpl implements RbacService {

    @Autowired private RoleMapper roleMapper;
    @Autowired private MenuMapper menuMapper;
    @Autowired private RoleMenuMapper roleMenuMapper;
    @Autowired private UserRoleMapper userRoleMapper;
    @Autowired private UserMapper userMapper;

    @Override
    public List<Role> listRoles(String keyword) {
        LambdaQueryWrapper<Role> qw = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            qw.like(Role::getRoleName, keyword).or().like(Role::getRoleCode, keyword);
        }
        qw.orderByDesc(Role::getRoleId);
        return roleMapper.selectList(qw);
    }

    @Override
    public boolean saveRole(Role role) {
        if (role.getStatus() == null) role.setStatus(1);
        return roleMapper.insert(role) > 0;
    }

    @Override
    public boolean updateRole(Role role) {
        return roleMapper.updateById(role) > 0;
    }

    @Override
    @Transactional
    public boolean deleteRole(Long roleId) {
        roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId));
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, roleId));
        return roleMapper.deleteById(roleId) > 0;
    }

    @Override
    public boolean setRoleStatus(Long roleId, Integer status) {
        Role r = new Role();
        r.setRoleId(roleId);
        r.setStatus(status);
        return roleMapper.updateById(r) > 0;
    }

    @Override
    public List<MenuNode> menuTree() {
        List<Menu> menus = menuMapper.selectList(
                new LambdaQueryWrapper<Menu>().eq(Menu::getStatus, 1).orderByAsc(Menu::getSortNum, Menu::getMenuId)
        );
        Map<Long, List<Menu>> byParent = menus.stream().collect(Collectors.groupingBy(m -> Optional.ofNullable(m.getParentId()).orElse(0L)));
        return buildNodes(0L, byParent);
    }

    private List<MenuNode> buildNodes(Long parentId, Map<Long, List<Menu>> byParent) {
        List<Menu> children = byParent.getOrDefault(parentId, Collections.emptyList());
        List<MenuNode> nodes = new ArrayList<>();
        for (Menu m : children) {
            MenuNode n = new MenuNode();
            n.id = m.getMenuId();
            n.label = m.getName();
            n.path = m.getPath();
            n.children = buildNodes(m.getMenuId(), byParent);
            nodes.add(n);
        }
        return nodes;
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        return roleMenuMapper.selectList(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId))
                .stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean saveRoleMenus(Long roleId, List<Long> menuIds) {
        roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId));
        if (menuIds == null || menuIds.isEmpty()) return true;
        for (Long mid : menuIds) {
            RoleMenu rm = new RoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(mid);
            roleMenuMapper.insert(rm);
        }
        return true;
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId))
                .stream().map(UserRole::getRoleId).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean saveUserRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        if (roleIds == null || roleIds.isEmpty()) return true;
        for (Long rid : roleIds) {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleId(rid);
            userRoleMapper.insert(ur);
        }
        return true;
    }

    @Override
    public List<String> getMyMenuPaths(Long userId) {
        List<Long> roleIds = getUserRoleIds(userId);

        // 兼容旧逻辑：如果 user_role 没配，退回到 user.role（单角色）
        if (roleIds.isEmpty()) {
            User u = userMapper.selectById(userId);
            if (u != null && StringUtils.isNotBlank(u.getRole())) {
                Role r = roleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleCode, u.getRole()));
                if (r != null) roleIds = List.of(r.getRoleId());
            }
        }
        if (roleIds.isEmpty()) return Collections.emptyList();

        List<RoleMenu> rms = roleMenuMapper.selectList(new LambdaQueryWrapper<RoleMenu>().in(RoleMenu::getRoleId, roleIds));
        if (rms.isEmpty()) return Collections.emptyList();

        List<Long> menuIds = rms.stream().map(RoleMenu::getMenuId).distinct().collect(Collectors.toList());
        List<Menu> menus = menuMapper.selectList(new LambdaQueryWrapper<Menu>().in(Menu::getMenuId, menuIds).eq(Menu::getStatus, 1));

        return menus.stream().map(Menu::getPath).distinct().collect(Collectors.toList());
    }

    @Override
    public List<UserInfo> getRoleUsers(Long roleId) {
        // 先获取角色编码
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            return Collections.emptyList();
        }

        // 查询拥有该角色的用户ID列表（从user_role表）
        List<Long> userIds = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, roleId)
        ).stream().map(UserRole::getUserId).collect(Collectors.toList());

        List<User> users;
        
        if (userIds.isEmpty()) {
            // 如果user_role表中没有数据，从user表的role字段查询
            users = userMapper.selectList(
                    new LambdaQueryWrapper<User>().eq(User::getRole, role.getRoleCode())
            );
        } else {
            // 查询用户信息
            users = userMapper.selectList(
                    new LambdaQueryWrapper<User>().in(User::getUserId, userIds)
            );
        }

        return users.stream().map(u -> {
            UserInfo info = new UserInfo();
            info.userId = u.getUserId();
            info.username = u.getUsername();
            info.realName = u.getRealName();
            info.email = u.getEmail();
            return info;
        }).collect(Collectors.toList());
    }
}
