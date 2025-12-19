package com.oem.evwarranty.controller;

import com.oem.evwarranty.model.WarrantyClaim;
import com.oem.evwarranty.model.User;
import com.oem.evwarranty.service.WarrantyClaimService;
import com.oem.evwarranty.service.VehicleService;
import com.oem.evwarranty.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for Warranty Claims (Service Center).
 */
@Controller
@RequestMapping("/sc/claims")
public class WarrantyClaimController {

    private final WarrantyClaimService claimService;
    private final VehicleService vehicleService;
    private final UserService userService;

    public WarrantyClaimController(WarrantyClaimService claimService,
            VehicleService vehicleService,
            UserService userService) {
        this.claimService = claimService;
        this.vehicleService = vehicleService;
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElse(null);
        Page<WarrantyClaim> claims;

        if (user != null && user.getServiceCenter() != null) {
            claims = claimService.findByServiceCenter(
                    user.getServiceCenter(),
                    PageRequest.of(page, size, Sort.by("createdAt").descending()));
        } else {
            claims = claimService.searchClaims(
                    search, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        }

        model.addAttribute("claims", claims);
        model.addAttribute("search", search);
        model.addAttribute("statuses", WarrantyClaim.ClaimStatus.values());
        return "sc/claims/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("claim", new WarrantyClaim());
        model.addAttribute("vehicles", vehicleService.findAll());
        return "sc/claims/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute WarrantyClaim claim,
            BindingResult result,
            @RequestParam Long vehicleId,
            @RequestParam(required = false) Long vehiclePartId,
            Authentication auth,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("vehicles", vehicleService.findAll());
            return "sc/claims/form";
        }
        try {
            User user = userService.findByUsername(auth.getName()).orElse(null);
            Long userId = user != null ? user.getId() : null;
            claim.setServiceCenter(user != null ? user.getServiceCenter() : null);

            claimService.createClaim(claim, vehicleId, vehiclePartId, userId);
            redirectAttributes.addFlashAttribute("success", "Warranty claim created successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            model.addAttribute("vehicles", vehicleService.findAll());
            return "sc/claims/form";
        }
        return "redirect:/sc/claims";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        WarrantyClaim claim = claimService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found"));
        model.addAttribute("claim", claim);
        model.addAttribute("technicians", userService.findTechnicians());
        return "sc/claims/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        WarrantyClaim claim = claimService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found"));
        model.addAttribute("claim", claim);
        model.addAttribute("vehicles", vehicleService.findAll());
        return "sc/claims/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @Valid @ModelAttribute WarrantyClaim claim,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "sc/claims/form";
        }
        try {
            claimService.updateClaim(id, claim);
            redirectAttributes.addFlashAttribute("success", "Claim updated successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/sc/claims/" + id;
    }

    @PostMapping("/{id}/submit")
    public String submit(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            claimService.submitClaim(id);
            redirectAttributes.addFlashAttribute("success", "Claim submitted for review");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/sc/claims/" + id;
    }

    @PostMapping("/{id}/assign")
    public String assignTechnician(@PathVariable Long id,
            @RequestParam Long technicianId,
            RedirectAttributes redirectAttributes) {
        try {
            claimService.assignTechnician(id, technicianId);
            redirectAttributes.addFlashAttribute("success", "Technician assigned successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/sc/claims/" + id;
    }

    @PostMapping("/{id}/complete")
    public String complete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            claimService.completeClaim(id);
            redirectAttributes.addFlashAttribute("success", "Claim completed successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/sc/claims/" + id;
    }
}
