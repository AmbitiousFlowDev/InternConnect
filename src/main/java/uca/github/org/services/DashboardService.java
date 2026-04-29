package uca.github.org.services;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import uca.github.org.models.User;

public interface DashboardService {
    public String getDashboard(Model model, @AuthenticationPrincipal User currentUser);
}
