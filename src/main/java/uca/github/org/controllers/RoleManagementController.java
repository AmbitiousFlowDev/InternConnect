package uca.github.org.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uca.github.org.models.Permission;
import uca.github.org.models.User;
import uca.github.org.services.RoleManagementService;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleManagementController {

    private final RoleManagementService roleManagementService;

    @GetMapping("/roles")
    public String roles(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("user", currentUser);
        model.addAttribute("roles", roleManagementService.getAllRoles());
        model.addAttribute("users", roleManagementService.getAllUsers());
        model.addAttribute("permissions", Arrays.asList(Permission.values()));
        return "pages/admin/roles";
    }

    @PostMapping("/roles/users/assign")
    public String assignRole(
            @RequestParam Long userId,
            @RequestParam Long roleId,
            RedirectAttributes redirectAttributes) {

        roleManagementService.assignRole(userId, roleId);
        redirectAttributes.addFlashAttribute("successMessage", "Role utilisateur mis a jour.");
        return "redirect:/roles";
    }

    @PostMapping("/roles/users/{userId}/role")
    public String assignRoleToUser(
            @PathVariable Long userId,
            @RequestParam Long roleId,
            RedirectAttributes redirectAttributes) {

        return assignRole(userId, roleId, redirectAttributes);
    }

    @PostMapping("/roles/permissions")
    public String updatePermissions(
            @RequestParam Long roleId,
            @RequestParam(required = false) Set<Permission> permissions,
            RedirectAttributes redirectAttributes) {

        roleManagementService.updatePermissions(
                roleId,
                permissions == null ? Set.of() : new LinkedHashSet<>(permissions)
        );
        redirectAttributes.addFlashAttribute("successMessage", "Permissions du role mises a jour.");
        return "redirect:/roles";
    }
}
