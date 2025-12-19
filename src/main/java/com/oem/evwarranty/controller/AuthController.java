package com.oem.evwarranty.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for authentication pages.
 */
@Controller
public class AuthController {

    @GetMapping("/login")
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
