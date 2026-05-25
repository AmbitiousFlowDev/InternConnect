package uca.github.org.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import uca.github.org.models.User;
import uca.github.org.services.StatusLabelService;
import uca.github.org.services.UserDisplayService;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final UserDisplayService userDisplayService;
    private final StatusLabelService statusLabelService;

    @ModelAttribute("userInitials")
    public String userInitials(@AuthenticationPrincipal User user) {
        return userDisplayService.getInitials(user);
    }

    @ModelAttribute("userDisplayName")
    public String userDisplayName(@AuthenticationPrincipal User user) {
        return userDisplayService.getDisplayName(user);
    }

    @ModelAttribute("statusLabels")
    public StatusLabelService statusLabels() {
        return statusLabelService;
    }
}
