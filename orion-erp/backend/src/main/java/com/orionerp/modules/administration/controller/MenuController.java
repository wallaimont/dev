package com.orionerp.modules.administration.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.administration.dto.MenuDto;
import com.orionerp.modules.administration.service.MenuService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/administracao/menus")
@RequiredArgsConstructor
@Tag(name = "Administracao")
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuDto>>> listMenus() {
        return ResponseEntity.ok(ApiResponse.ok(menuService.listVisibleMenus()));
    }
}
