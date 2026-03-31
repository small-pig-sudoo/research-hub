package com.univ.researchhub.controller.rbac;

import com.univ.researchhub.common.Result;
import com.univ.researchhub.entity.rbac.Role;
import com.univ.researchhub.service.rbac.RbacService;
import com.univ.researchhub.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;   // ? Boot3 ±ØÐëÓÃ jakarta
import java.util.List;

@RestController
@RequestMapping("/api/rbac")
public class RbacController {

    @Autowired
    private RbacService rbacService;

    @Autowired
    private JwtUtils jwtUtils;

    // ===== roles =====
    @GetMapping("/roles")
    public Result<?> listRoles(@RequestParam(required = false) String keyword) {
        return Result.success(rbacService.listRoles(keyword));
    }

    @PostMapping("/roles")
    public Result<?> createRole(@RequestBody Role role) {
        return rbacService.saveRole(role) ? Result.success(role) : Result.error("´´½¨½ÇÉ«Ê§°Ü");
    }

    @PutMapping("/roles/{id}")
    public Result<?> updateRole(@PathVariable("id") Long id, @RequestBody Role role) {
        role.setRoleId(id);
        return rbacService.updateRole(role) ? Result.success(true) : Result.error("¸üÐÂ½ÇÉ«Ê§°Ü");
    }

    @DeleteMapping("/roles/{id}")
    public Result<?> deleteRole(@PathVariable("id") Long id) {
        return rbacService.deleteRole(id) ? Result.success(true) : Result.error("É¾³ý½ÇÉ«Ê§°Ü");
    }

    @PostMapping("/roles/{id}/status")
    public Result<?> setRoleStatus(@PathVariable("id") Long id, @RequestParam Integer status) {
        return rbacService.setRoleStatus(id, status) ? Result.success(true) : Result.error("¸üÐÂ×´Ì¬Ê§°Ü");
    }

    // ===== menus =====
    @GetMapping("/menus/tree")
    public Result<?> menuTree() {
        return Result.success(rbacService.menuTree());
    }

    // ===== role-menus =====
    @GetMapping("/roles/{id}/menus")
    public Result<?> getRoleMenus(@PathVariable("id") Long id) {
        return Result.success(rbacService.getRoleMenuIds(id));
    }

    @PostMapping("/roles/{id}/menus")
    public Result<?> saveRoleMenus(@PathVariable("id") Long id, @RequestBody List<Long> menuIds) {
        return rbacService.saveRoleMenus(id, menuIds) ? Result.success(true) : Result.error("±£´æÊ§°Ü");
    }

    // ===== user-roles =====
    @GetMapping("/users/{id}/roles")
    public Result<?> getUserRoles(@PathVariable("id") Long id) {
        return Result.success(rbacService.getUserRoleIds(id));
    }

    @PostMapping("/users/{id}/roles")
    public Result<?> saveUserRoles(@PathVariable("id") Long id, @RequestBody List<Long> roleIds) {
        return rbacService.saveUserRoles(id, roleIds) ? Result.success(true) : Result.error("±£´æÊ§°Ü");
    }

    // ===== role-users =====
    @GetMapping("/roles/{id}/users")
    public Result<?> getRoleUsers(@PathVariable("id") Long id) {
        return Result.success(rbacService.getRoleUsers(id));
    }

    // ===== my menus =====
    @GetMapping("/menus/my")
    public Result<?> myMenus(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || auth.isBlank()) return Result.error("È±ÉÙtoken");

        String token = auth.startsWith("Bearer ") ? auth.substring(7) : auth;
        Long userId = jwtUtils.getUserIdFromToken(token);
        return Result.success(rbacService.getMyMenuPaths(userId));
    }
}
