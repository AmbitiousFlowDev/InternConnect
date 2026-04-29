package uca.github.org.services.impl;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import uca.github.org.models.User;
import uca.github.org.services.DashboardService;

public class DashboardServiceImpl implements DashboardService {
    public String getDashboard(Model model, @AuthenticationPrincipal User currentUser) {
        return null;
    }
}
