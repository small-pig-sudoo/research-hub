package com.univ.researchhub.service.rbac;

import com.univ.researchhub.entity.rbac.Menu;
import com.univ.researchhub.entity.rbac.Role;

import java.util.List;

public interface RbacService {

    // roles
    List<Role> listRoles(String keyword);
    boolean saveRole(Role role);
    boolean updateRole(Role role);
    boolean deleteRole(Long roleId);
    boolean setRoleStatus(Long roleId, Integer status);

    // menus
    List<MenuNode> menuTree();

    // role-menus
    List<Long> getRoleMenuIds(Long roleId);
    boolean saveRoleMenus(Long roleId, List<Long> menuIds);

    // user-roles
    List<Long> getUserRoleIds(Long userId);
    boolean saveUserRoles(Long userId, List<Long> roleIds);

    // role-users
    List<UserInfo> getRoleUsers(Long roleId);

    // current user menus
    List<String> getMyMenuPaths(Long userId);

    // user info dto
    class UserInfo {
        public Long userId;
        public String username;
        public String realName;
        public String email;
    }

    // tree node dto
    class MenuNode {
        public Long id;
        public String label;
        public String path;
        public List<MenuNode> children;
    }
}
