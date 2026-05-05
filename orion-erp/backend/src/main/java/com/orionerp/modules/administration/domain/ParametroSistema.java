package com.orionerp.modules.administration.domain;

import com.orionerp.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "parametros_sistema")
@Getter
@Setter
public class ParametroSistema extends BaseEntity {

    @Column(name = "empresa_id")
    private Long empresaId;

    @Column(name = "filial_id")
    private Long filialId;

    @Column(nullable = false, length = 100)
    private String chave;

    @Column(columnDefinition = "TEXT")
    private String valor;

    @Column(nullable = false, length = 30)
    private String tipo = "STRING";

    @Column(length = 300)
    private String descricao;

    @Column(length = 50)
    private String modulo;

    @Column(nullable = false)
    private Boolean editavel = true;
}
