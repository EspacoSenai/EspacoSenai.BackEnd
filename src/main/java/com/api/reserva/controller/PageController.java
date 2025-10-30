package com.api.reserva.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @GetMapping("/")
    public String index() {
        // Página inicial básica: direciona para login; o JS da home garante redirecionamento pós-login
        return "login";
    }
}
