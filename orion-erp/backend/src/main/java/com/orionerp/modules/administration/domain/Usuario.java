package com.orionerp.modules.administration.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario extends TenantEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "perfil_id", nullable = false)
    private Perfil perfil;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(name = "senha_hash", nullable = false, length = 300)
    private String senhaHash;

    @Column(length = 20)
    private String telefone;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    @Column(name = "tentativas_login", nullable = false)
    private Integer tentativasLogin = 0;

    @Column(nullable = false)
    private Boolean bloqueado = false;

    @Column(name = "bloqueado_ate")
    private LocalDateTime bloqueadoAte;

    @Column(name = "trocar_senha", nullable = false)
    private Boolean trocarSenha = false;

    public boolean isLockedNow() {
        if (!Boolean.TRUE.equals(bloqueado)) {
            return false;
        }
        return bloqueadoAte == null || bloqueadoAte.isAfter(LocalDateTime.now());
    }
}
