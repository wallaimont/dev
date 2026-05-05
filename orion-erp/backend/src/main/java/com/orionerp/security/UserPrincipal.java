package com.orionerp.security;

import com.orionerp.modules.administration.domain.Permissao;
import com.orionerp.modules.administration.domain.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long userId;
    private final Long empresaId;
    private final Long filialId;
    private final String nome;
    private final String email;
    private final String password;
    private final boolean ativo;
    private final boolean locked;
    private final Set<String> permissoes;
    private final Collection<? extends GrantedAuthority> authorities;

    private UserPrincipal(Usuario usuario) {
        this.userId = usuario.getId();
        this.empresaId = usuario.getEmpresaId();
        this.filialId = usuario.getFilialId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.password = usuario.getSenhaHash();
        this.ativo = Boolean.TRUE.equals(usuario.getAtivo()) && !Boolean.TRUE.equals(usuario.getDeleted());
        this.locked = usuario.isLockedNow();

        Set<String> permissionKeys = new HashSet<>();
        Set<SimpleGrantedAuthority> granted = new HashSet<>();

        if (usuario.getPerfil() != null) {
            if (Boolean.TRUE.equals(usuario.getPerfil().getAdmin())) {
                granted.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            granted.add(new SimpleGrantedAuthority("ROLE_USER"));

            for (Permissao permissao : usuario.getPerfil().getPermissoes()) {
                String key = permissao.getRecurso() + ":" + permissao.getAcao();
                permissionKeys.add(key);
                granted.add(new SimpleGrantedAuthority(key));
            }
        }

        this.permissoes = permissionKeys;
        this.authorities = granted;
    }

    public static UserPrincipal from(Usuario usuario) {
        return new UserPrincipal(usuario);
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }
}
