package com.oem.evwarranty.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller for authentication pages.
 */
@Controller
@Tag(name = "Authentication", description = "Endpoints for user login and access control")
public class AuthController {

    @GetMapping("/login")
    @Operation(summary = "Login page", description = "Standard login form for all system users")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/error/403")
    public String accessDenied(Model model) {
        model.addAttribute("error", "Access Denied");
        model.addAttribute("message", "You don't have permission to access this resource.");
        return "error/403";
    }
}
