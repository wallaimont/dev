package com.sigaseguros.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping({"/app", "/app/dashboard"})
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/app/clientes")
    public String clientes() {
        return "clientes";
    }

    @GetMapping("/app/propostas")
    public String propostas() {
        return "propostas";
    }

    @GetMapping("/app/apolices")
    public String apolices() {
        return "apolices";
    }

    @GetMapping("/app/renovacoes")
    public String renovacoes() {
        return "renovacoes";
    }

    @GetMapping("/app/sinistros")
    public String sinistros() {
        return "sinistros";
    }

    @GetMapping("/app/financeiro")
    public String financeiro() {
        return "financeiro";
    }

    @GetMapping("/app/comissoes")
    public String comissoes() {
        return "comissoes";
    }

    @GetMapping("/app/seguradoras")
    public String seguradoras() {
        return "seguradoras";
    }

    @GetMapping("/app/corretoras")
    public String corretoras() {
        return "corretoras";
    }

    @GetMapping("/app/usuarios")
    public String usuarios() {
        return "usuarios";
    }

    @GetMapping("/app/auditoria")
    public String auditoria() {
        return "auditoria";
    }
}
