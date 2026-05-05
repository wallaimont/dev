package com.orionerp.modules.administration.service;

import com.orionerp.modules.administration.dto.MenuDto;
import com.orionerp.modules.administration.mapper.MenuMapper;
import com.orionerp.modules.administration.repository.MenuRepository;
import com.orionerp.security.SecurityUtils;
import com.orionerp.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;

    @Transactional(readOnly = true)
    public List<MenuDto> listVisibleMenus() {
        UserPrincipal currentUser = SecurityUtils.currentUser();

        if (currentUser == null) {
            return List.of();
        }

        boolean admin = currentUser.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        return menuRepository.findByAtivoTrueAndDeletedFalseOrderByOrdemAsc()
                .stream()
                .filter(menu -> admin || canAccessMenu(currentUser, menu.getPermissaoRecurso()))
                .map(menuMapper::toDto)
                .toList();
    }

    private boolean canAccessMenu(UserPrincipal user, String permissaoRecurso) {
        if (permissaoRecurso == null || permissaoRecurso.isBlank()) {
            return true;
        }

        return user.getPermissoes().stream()
                .anyMatch(permission -> permission.startsWith(permissaoRecurso + ":"));
    }
}
