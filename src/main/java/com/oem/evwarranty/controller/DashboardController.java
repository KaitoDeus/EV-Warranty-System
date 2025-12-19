package com.oem.evwarranty.controller;

import com.oem.evwarranty.model.User;
import com.oem.evwarranty.service.ReportService;
import com.oem.evwarranty.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Map;

/**
 * Controller for Dashboard pages based on user role.
 */
@Controller
public class DashboardController {

    private final ReportService reportService;
    private final UserService userService;

    public DashboardController(ReportService reportService, UserService userService) {
        this.reportService = reportService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        Map<String, Object> stats = reportService.getDashboardStats();
        model.addAttribute("stats", stats);

        // Get current user
        User user = userService.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("currentUser", user);

        // Determine which dashboard to show based on role
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "dashboard/admin";
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EVM_STAFF"))) {
            return "dashboard/evm";
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SC_STAFF"))) {
            if (user != null && user.getServiceCenter() != null) {
                stats.putAll(reportService.getServiceCenterStats(user.getServiceCenter()));
            }
            return "dashboard/sc-staff";
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SC_TECHNICIAN"))) {
            return "dashboard/sc-technician";
        }

        return "dashboard/default";
    }
}
